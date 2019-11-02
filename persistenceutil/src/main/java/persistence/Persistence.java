package persistence;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import annotations.Columna;
import annotations.Entidad;

public class Persistence {

  private static final String PROPERTIES_PATH = "/persistence.properties";

  public void persist(Object obj) throws Exception {

    String query = getSQLPersistQuery(obj);

    System.out.println(query);

    PreparedStatement pstmt = createPreparedStatement(query, obj);
    pstmt.executeUpdate();
  }

  public void update(Object obj) throws Exception {

    String query = getSQLUpdateQuery(obj);

    System.out.println(query);

    PreparedStatement pstmt = createPreparedStatement2(query, obj,true);
    pstmt.execute();
  }

  
 public ResultSet SelectAll(Object obj) throws Exception {

    String query = getSQLSelectQuery(obj, null);

    System.out.println(query);

    Statement state = getConnection().createStatement();
    
    return state.executeQuery(query);

 }
 
 public ResultSet SelectById(Object obj, String id) throws Exception {

   String query = getSQLSelectQuery(obj, id);

   System.out.println(query);

   Statement state = getConnection().createStatement();
   
   return state.executeQuery(query);

}
 
 public Boolean Delect(Object obj, String id) throws Exception {

   String query = getSQLDeleteQuery(obj, id);

   System.out.println(query);

   Statement state = getConnection().createStatement();
   
   return state.execute(query);

}
 
  private Connection getConnection() throws Exception {

    Properties prop = loadProperties();

    Class.forName(prop.getProperty("driver"));
    return DriverManager.getConnection(prop.getProperty("url"),
        prop.getProperty("user"), prop.getProperty("password"));
  }

  private Properties loadProperties() throws IOException {
    Properties prop = new Properties();

    InputStream input = getClass().getResourceAsStream(PROPERTIES_PATH);
    prop.load(input);

    return prop;
  }

  private PreparedStatement createPreparedStatement(String query, Object obj)
      throws Exception {
    Class clase = obj.getClass();
    List values = getValues(obj);
    System.out.println(values);

    Connection conn = getConnection();

    PreparedStatement pstmt = conn.prepareStatement(query);

     int i = 1;
     for (Object value : values) {
     pstmt.setObject(i, value);
     i++;
     }

    return pstmt;
  }
  private PreparedStatement createPreparedStatement2(String query, Object obj ,boolean si)
      throws Exception {
    Class clase = obj.getClass();
    List values = getValues(obj);
    System.out.println(values);

    Connection conn = getConnection();

    PreparedStatement pstmt = conn.prepareStatement(query);

    int j = 1, i;

    if (si) {
        i = 1;
    } else {
        i = 2;
    }

    while (j < values.size()) {
      pstmt.setObject(i, values.get(j));
        i++;
        j++;
    }

    if (si) {
      pstmt.setObject(values.size(), values.get(0));
    } else {
      pstmt.setObject(1, values.get(0));
    }

    return pstmt;
  }

  /*private PreparedStatement createPreparedStatementSelect(String query, Object obj)
      throws Exception {
    Class clase = obj.getClass();
    List values = getValues(obj);
    System.out.println(values);

    Connection conn = getConnection();

    PreparedStatement pstmt = conn.prepareStatement(query);
    

    return pstmt;
  }
  */
  private List<Object> getValues(Object obj)
      throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
    Class clase = obj.getClass();

    List list = new ArrayList<>();
    Field[] attributes = clase.getDeclaredFields();

    for (Field field : attributes) {

      Columna col = (Columna) field.getAnnotation(Columna.class);

      if (col == null)
        break;

      String getter = "get" + field.getName().substring(0, 1).toUpperCase()
          + field.getName().substring(1);
      Method method = clase.getMethod(getter);

      Object value = method.invoke(obj);
      System.out.println(getter + "=" + value);
      
      list.add(value);
    }

    return list;
  }

  private String getSQLPersistQuery(Object obj) {
    Class clase = obj.getClass();
    StringBuilder query = new StringBuilder();

    Entidad entidad = (Entidad) clase.getAnnotation(Entidad.class);

    if (entidad != null) {
      query.append("INSERT INTO ").append(entidad.schema()).append(".")
          .append(entidad.value());
      System.out.println(
          "Entidad=" + entidad.value() + ",esquema=" + entidad.schema());
    } else {
      query.append("INSERT INTO ").append(clase.getSimpleName());
    }

    query.append("(");

    StringBuilder params = new StringBuilder();
    StringBuilder cols = new StringBuilder();

    Field[] attributes = clase.getDeclaredFields();
    for (Field field : attributes) {
      Columna col = (Columna) field.getAnnotation(Columna.class);

      if (col == null)
        break;

      System.out.println("col_name=" + col.name() + ",is_pk=" + col.isPk());
      cols.append(col.name()).append(",");
      params.append("?").append(",");
    } 
    
    query.append(cols.substring(0, cols.length() - 1)).append(")");
    query.append(" VALUES (").append(params.substring(0, params.length() - 1))
        .append(")");

    return query.toString();
  }

  private String getSQLSelectQuery(Object obj,Object id) {
    Class clase = obj.getClass();
    StringBuilder query = new StringBuilder();
    Entidad entidad = (Entidad) clase.getAnnotation(Entidad.class);

    if (entidad != null) {
      query.append("SELECT * FROM ").append(entidad.schema()).append(".")
          .append(entidad.value());
      System.out.println(
          "Entidad=" + entidad.value() + ",esquema=" + entidad.schema());
      
    } else {
      query.append("SELECT * FROM ").append(clase.getSimpleName());
    }

  if (id != null) {
    String igual = "";
    query.append(" WHERE ");
    
    Field[] atrib = clase.getDeclaredFields();
    for (Field field : atrib) {
      Columna column = (Columna) field.getAnnotation(Columna.class);
      
      if (column == null) {
        break;
      }
      if(column.isPk()) {
         igual += column.name() + "=";
         
         switch (id.getClass().getSimpleName()) {
          case "String" :
            igual += "\'" + id + "\'";
            break;

          default :
            igual += id ;
        }
      }
    }
    return query + igual + ";";
  }else {
  
    return query.toString();
  }
  }
  

  private String getSQLDeleteQuery(Object obj,Object id) {
    Class clase = obj.getClass();
    StringBuilder query = new StringBuilder();
    Entidad entidad = (Entidad) clase.getAnnotation(Entidad.class);

    if (entidad != null) {
      query.append("DELETE FROM ").append(entidad.schema()).append(".")
          .append(entidad.value());
      System.out.println(
          "Entidad=" + entidad.value() + ",esquema=" + entidad.schema());
      
    } else {
      query.append("DELETE FROM ").append(clase.getSimpleName());
    }

  if (id != null) {
    String igual = "";
    query.append(" WHERE ");
    
    Field[] atrib = clase.getDeclaredFields();
    for (Field field : atrib) {
      Columna column = (Columna) field.getAnnotation(Columna.class);
      
      if (column == null) {
        break;
      }
      if(column.isPk()) {
         igual += column.name() + "=";
         
         switch (id.getClass().getSimpleName()) {
          case "String" :
            igual += "\'" + id + "\'";
            break;

          default :
            igual += id ;
        }
      }
    }
    return query + igual + ";";
  }else {
  
    return query.toString();
  }
  }
  
  
  private String getSQLUpdateQuery(Object obj) {
    Class clase = obj.getClass();
    StringBuilder query = new StringBuilder();
    Entidad entidad = (Entidad) clase.getAnnotation(Entidad.class);

    if (entidad != null) {
      query.append("UPDATE ").append(entidad.schema()).append(".")
          .append(entidad.value());
      System.out.println(
          "Entidad=" + entidad.value() + ",esquema=" + entidad.schema());
      
    } else {
      query.append("UPDATE ").append(clase.getSimpleName());
    }
    query.append(" SET ");
    String colu = "";

    Field[] attributes = clase.getDeclaredFields();
    Field id = null;
    for (Field field : attributes) {
        Columna column = (Columna) field.getAnnotation(Columna.class);

        if (column == null) {
            break;
        }

        if (!column.isPk()) {
            colu += column.name() + "=?,";
        } else {
            id = field;
        }
    }
String igual = "";
    /////////  
   igual += colu.substring(0, colu.length() - 1) + " WHERE " + ((Columna) id.getAnnotation(Columna.class)).name() + "=";

  //  query.append(colu.substring(0, colu.length() - 1)).append(" WHERE ").append((Columna) id.getAnnotation(Columna.class)).getClass();
   
 //query.append(" = ");

    return query +igual+ "?;";
  }
}
