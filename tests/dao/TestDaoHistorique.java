package dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import dao.DAOFactory.TypePersistance;
import database.connexion.ConnexionFromFile;
import database.connexion.ConnexionSingleton;
import database.connexion.PersistanceException;

import java.io.FileNotFoundException;
import java.sql.Timestamp;

import database.uri.Databases;
import model.Historique;
import utils.DatabaseUtil;

public class TestDaoHistorique {
	private DAOFactory factory;

	//String momA = "2017-03-22 20:02:19.272" ;
	//String momS ="2017-03-28 16:08:47.634";
//	private final Historique histo1 = new Historique("BBB-222", java.sql.Timestamp.valueOf("2017-03-22 20:02:19.272").toLocalDateTime(),
//			"P04", java.sql.Timestamp.valueOf("2017-03-28 16:08:47.634").toLocalDateTime());

	/**
	 * Test un getFromId
	 */
	@Test
	public void testGetDaoHistorique() {
		//IHistoriqueDAO dao = factory.getHistoriqueDAO();
		//Optional<Historique> histo = dao.getFromID(histo1.getFKPersonne());
//		assertTrue(histo.isPresent());
//		assertEquals(histo.get().getFKPersonne(),histo1.getFKPersonne());
//		assertEquals(histo.get().getMomentA(),histo1.getMomentA());
//		assertEquals(histo.get().getFKPlace(), histo1.getFKPlace());
//		assertEquals(histo.get().getMomentS(),histo1.getMomentS());
		//System.out.println(histo);
		//System.out.println("\n");
	}
	
	/**
	 * Test un getListe
	 */
	@Test
	public void testGetListeDaoHistorique() {
		IHistoriqueDAO dao = factory.getHistoriqueDAO();
		//Retirer car trop long à l'affichage
		//List<Historique> l = dao.getListe(null);
		//assertEquals(l.size(), 4);
//		for(int i = 0; i<l.size();i++) {
//			//System.out.println(l.get(i).toString());
//		}
		System.out.println("ICI SE TROUVE L'HISTORIQUE DES VOITURES QUI ONT STATIONNEES DANS LE PARKING");
		System.out.println("\n");
		// Vérifie si sta1 existe dans la liste
		//assertEquals(l.get(0), histo1);
	}
	
	/**
	 * Test historique_voit(String immatr)
	 */
	@Test
	public void test_Historique_Voit() {
		IHistoriqueDAO dao = factory.getHistoriqueDAO();
		List<Historique> histo = dao.historique_voit("CCC-333");
//		if(!(histo != null))
//			System.out.println("Entrez une immatriculation");
//		else
			System.out.println(histo);	
	}

	/**
	 * 
	 * test methode count()
	 */
	@Test (expectedExceptions = UnsupportedOperationException.class)
	public void testCount() {
		IStationnementDAO dao = factory.getStationnementDAO();
		Integer i=dao.count();
		assertEquals(i.intValue(),dao.getListe(null).size());
		//System.out.println(i);
	}

	
	/**
	 * Exécuté une fois et ceci avant tous les tests de cette classe
	 * 
	 * @throws PersistanceException
	 * @throws FileNotFoundException
	 */
	@BeforeClass
	public void beforeClass() throws PersistanceException, FileNotFoundException {
		ConnexionSingleton.setInfoConnexion(
				new ConnexionFromFile("./resources/connexionParking_Test.properties", Databases.FIREBIRD));
		// Réinitialise la base de données dans son état initial
		// DatabaseInitialize.resetDatabase(ConnexionSingleton.getConnexion(),"./resources/scriptInitDBTest.sql");
		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
	}

	/**
	 * Exécuté une fois et ceci après tous les tests de cette classe
	 * 
	 * @throws PersistanceException
	 * @throws FileNotFoundException
	 */
	@AfterClass
	public void afterClass() throws FileNotFoundException, PersistanceException {
		// Réinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(), "./resources/scriptInitDBTest.sql");
		ConnexionSingleton.liberationConnexion();
	}

}
