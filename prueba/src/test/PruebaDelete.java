package test;

import java.sql.ResultSet;

import modelo.Persona;
import persistence.Persistence;

public class PruebaDelete {

public static void main(String[] args) {
    
    Persistence persistence = new Persistence();
    try {
     
      persistence.Delect(new Persona(), "1234") ;
     System.out.println("Se ha eliminado correctamente");
    } catch (Exception e) {
      System.out.println("error");
      e.printStackTrace();
    }
  }
}
