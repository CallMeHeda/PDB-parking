package database.connexion;

import static org.testng.Assert.assertNotNull;

import java.sql.Connection;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import database.uri.Databases;

public class TestConnexion {
	@Test
	public void testConnexionEmployee() throws PersistanceException {
		ConnexionSingleton.setInfoConnexion(new ConnexionFB_Employee());
		Connection con1 = ConnexionSingleton.getConnexion();
		assertNotNull(con1);
		ConnexionSingleton.liberationConnexion();
	}

//	@Test
//	public void testConnexionFromFile() throws PersistanceException {
//		ConnexionSingleton
//				.setInfoConnexion(new ConnexionFromFile("./resources/connexion_test.properties", Databases.FIREBIRD));
//		Connection con1 = ConnexionSingleton.getConnexion();
//		assertNotNull(con1);
//		ConnexionSingleton.liberationConnexion();
//	}

	@Test(dataProvider = "getDPFichiers")
	public void testConnexionsFromFileDP(String fichier) throws PersistanceException {
		ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(fichier, Databases.FIREBIRD));
		Connection con1 = ConnexionSingleton.getConnexion();
		assertNotNull(con1);
		ConnexionSingleton.liberationConnexion();
	}

	@DataProvider
	private Object[][] getDPFichiers() {
		return new Object[][] { 
			    //new Object[] { "./resources/connexion_test.properties" },
				new Object[] { "./resources/connexionParking_Test.properties" },
				new Object[] { "./resources/connexionParking.properties" },
				 };
	}

}
