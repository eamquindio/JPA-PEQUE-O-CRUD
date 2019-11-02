package test;

import modelo.Persona;
import persistence.Persistence;

public class PruebaInsert {

	public static void main(String[] args) {
		
		Persistence persistence = new Persistence();
		try {
			persistence.persist(new Persona("jhon", "14", 27));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
