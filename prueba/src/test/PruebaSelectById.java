package test;

import java.sql.ResultSet;

import modelo.Persona;
import persistence.Persistence;

public class PruebaSelectById {
public static void main(String[] args) {
    
    Persistence persistence = new Persistence();
    try {
     ResultSet rs = persistence.SelectById(new Persona(),"1234");
     
     while(rs.next()) {
       System.out.println("Nombre: " + rs.getString("name")+ "; id: " + rs.getString("id") +  "; edad: "+ rs.getInt("age"));
     }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
