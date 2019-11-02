package test;

import modelo.Persona;
import persistence.Persistence;

public class PruebaUpdate {
public static void main(String[] args) {
    
    Persistence persistence = new Persistence();
    try {
     Persona persona = new Persona("jhonny","13456",27);
     persistence.persist(persona);
         persona.setNombre("CVBN");
      persistence.update(persona);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
