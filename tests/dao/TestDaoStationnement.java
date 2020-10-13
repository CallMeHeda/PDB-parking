/**
 * @author E. Fatima
 */
package dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
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
import java.sql.SQLException;
import java.sql.Timestamp;
//import java.time.LocalDateTime;

import database.uri.Databases;
import model.Personnel;
import model.Place;
import model.SortieVoiture;
import model.Stationnement;
import utils.DatabaseUtil;

public class TestDaoStationnement {
	private DAOFactory factory;

	//private Personnel p = new Personnel("CCC-333","Agenaes","Luc",null);
	//private final Stationnement sta1 = new Stationnement(p, "P01", Timestamp.valueOf("2020-01-16 19:15:02.576").toLocalDateTime());
	

	/**
	 * Test un getFromId
	 */
	@Test
	public void testGetDaoStationnement() {
		IStationnementDAO dao = factory.getStationnementDAO();
		//Optional<Stationnement> sta = dao.getFromID(sta1.getPersonne().getImmatr());
//		assertTrue(sta.isPresent());
//		assertEquals(sta.get(),sta1);
//		assertEquals(sta.get().getMomentA(), sta1.getMomentA());
//		//System.out.println(sta);
	}
	
	/**
	 * Test un getListe
	 */
	@Test
	public void testGetListeDaoStationnement() {
		IStationnementDAO dao = factory.getStationnementDAO();
		List<Stationnement> l = dao.getListe(null);
		//assertEquals(l.size(), 4);
		for(int i = 0; i<l.size();i++) {
			System.out.println("GET LIST");
			System.out.println(l.get(i).toString());
			System.out.println("FIN GET LIST");
		}
		// Vérifie si sta1 existe dans la liste
		//assertEquals(l.get(0), sta1);
	}
	
//	/**
//	 * Test arrivee_voit(immatr) + arrivee_voit(immatr,place)
//	 * @throws ParkingSQLException 
//	 * @throws SQLException 
//	 */
//	@Test
//	public void test_arrivee_voit() throws ParkingSQLException, SQLException {
//		IStationnementDAO dao = factory.getStationnementDAO();
//		// REMPLIE TOUTES LES PLACES POUR TESTER PARKING PLEIN A L'APPEL DE ARRIVEE_VOIT(IMMATR)
////		Stationnement voit3 = dao.arrivee_voit("DDD-444","P02");
////		System.out.println(voit3);
////		Stationnement voit2 = dao.arrivee_voit("AAA-111","P03");
////		System.out.println(voit2);
////		Stationnement voit4 = dao.arrivee_voit("EEE-555","P04");
////		System.out.println(voit4);
//		
//		//Arrivée d'une voiture avec juste l'immatr
//		Stationnement voit1 = dao.arrivee_voit("BBB-222");
//		System.out.println(voit1);
//		
//		// Arrivée d'une voiture avec immatriculation et place de stationnement
//		Stationnement voit2 = dao.arrivee_voit("AAA-111","P03");
//		System.out.println(voit2);
//	}
//	
//	/**
//	 * Test sortie_voit()
//	 */
	@Test
	public void test_sortie_voit() throws SQLException, ParkingException{
		IStationnementDAO dao = factory.getStationnementDAO();
		SortieVoiture voit = dao.sortie_voit("CCC-333");
		//String voit2 = dao.sortie_voit("DDD-444");
		System.out.println(voit);
		//System.out.println(voit2);
	}
//	
//	/**
//	 * 
//	 * test methode count()
//	 */
//	@Test (expectedExceptions = UnsupportedOperationException.class)
//	public void testCount() {
//		IStationnementDAO dao = factory.getStationnementDAO();
//		Integer i=dao.count();
//		assertEquals(i.intValue(),dao.getListe(null).size());
//		//System.out.println(i);
//	}

/////////////////////////////////////////////////// TestDAOStationnement PROF /////////////////////////////////////////////////////
	
	@Test
	public void testArrivSortieVoit() throws Exception {
		IStationnementDAO dao = factory.getStationnementDAO();
		Stationnement s = dao.arrivee_voit("AAA-111", "P02");
		assertEquals("AAA-111", s.getPersonne().getImmatr());
		assertEquals("P02", s.getPlace());
		assertNotNull(s.getMomentA());
		SortieVoiture info = dao.sortie_voit("AAA-111");
		assertEquals("P02", info.getPlace());
		assertTrue(info.getDuree() < 2);
	}

	@Test
	public void testArrivFindPlaceVoit() throws Exception {
		IStationnementDAO dao = factory.getStationnementDAO();
		Stationnement s = dao.arrivee_voit("DDD-444");
		assertEquals("DDD-444", s.getPersonne().getImmatr());
		assertEquals("P02", s.getPlace());
		assertNotNull(s.getMomentA());
		SortieVoiture info = dao.sortie_voit("DDD-444");
		assertEquals("P02", info.getPlace());
		assertTrue(info.getDuree() < 2);
	}

	@Test(expectedExceptions = ParkingFullException.class)
	public void testParkingFullVoit() throws Exception {
		// remplis le parking
		IStationnementDAO dao = factory.getStationnementDAO();
		Stationnement s = dao.arrivee_voit("AAA-111", "P02");
		assertEquals("P02", s.getPlace());
		s = dao.arrivee_voit("BBB-222", "P03");
		assertEquals("P03", s.getPlace());
		s = dao.arrivee_voit("DDD-444", "P04");
		assertEquals("P04", s.getPlace());
		try {
			s = dao.arrivee_voit("EEE-555");
		} catch (Exception e) {
			// libère les voitures
			assertEquals("P02", dao.sortie_voit("AAA-111").getPlace());
			assertEquals("P03", dao.sortie_voit("BBB-222").getPlace());
			assertEquals("P04", dao.sortie_voit("DDD-444").getPlace());
			throw e;// redéclenche l'exception
		} // force la recherche d'un place alors qu il n'y en a plus de libre
	}

	@Test(expectedExceptions = ParkingPlaceBusyException.class)
	public void testPlaceBloquee() throws Exception {
		// on va utiliser la place P05 qui est bloquée
		IStationnementDAO dao = factory.getStationnementDAO();
		try {
			Stationnement s = dao.arrivee_voit("AAA-111", "P05");
		} catch (Exception e) {
			// Vérifie que la place est précisée dans l'exception
			if (e instanceof ParkingPlaceBusyException) {
				assertEquals("P05", ((ParkingPlaceBusyException) e).getPlace());
			}
			throw e;// Repropage l'exception
		}
	}

	@Test(expectedExceptions = ParkingPlaceBusyException.class)
	public void testPlaceOccupee() throws Exception {
		// on va utiliser la place P01 qui est occupée par une voiture
		IStationnementDAO dao = factory.getStationnementDAO();
		try {
			Stationnement s = dao.arrivee_voit("AAA-111", "P01");
		} catch (Exception e) {
			// Vérifie que la place est précisée dans l'exception
			if (e instanceof ParkingPlaceBusyException) {
				assertEquals("P01", ((ParkingPlaceBusyException) e).getPlace());
			}
			throw e;// Repropage l'exception
		}
	}

	@Test(expectedExceptions = ParkingPKException.class)
	public void testVoitureDéjàGarée() throws Exception {
		// on va utiliser la voiture CCC-333 qui est déjà dans le parking
		// et la place p02 qui est libre
		IStationnementDAO dao = factory.getStationnementDAO();
		try {
			Stationnement s = dao.arrivee_voit("CCC-333", "P02");
		} catch (Exception e) {
			// Vérifie que le champ de la PK est précisé dans l'exception
			if (e instanceof ParkingPKException) {
				assertEquals("FKPERSONNE_STA", ((ParkingPKException) e).getChamp());
			}
			throw e;// Repropage l'exception
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	/**
//	 * Exécuté une fois et ceci avant tous les tests de cette classe
//	 * 
//	 * @throws PersistanceException
//	 */
//	@BeforeClass
//	public void beforeTest() throws PersistanceException {
//		ConnexionSingleton.setInfoConnexion(
//				new ConnexionFromFile("./resources/connexionParking_Test.properties", Databases.FIREBIRD));
//		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
//	}
//
//	/**
//	 * Exécuté une fois et ceci après tous les tests de cette classe
//	 */
//	@AfterClass
//	public void afterTest() {
//		ConnexionSingleton.liberationConnexion();
//	}
//
//}
	
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