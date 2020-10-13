package dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import org.testng.annotations.AfterClass;
//import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
//import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import dao.DAOFactory.TypePersistance;
import database.connexion.ConnexionFromFile;
import database.connexion.ConnexionSingleton;
import database.connexion.PersistanceException;
import database.uri.Databases;
import model.Place;
import utils.DatabaseUtil;

public class TestDaoPlace {
	private DAOFactory factory;

	private final Place pla = new Place("P01", 10, false);
	private final Place plb = new Place("P02", 10, true);

	/**
	 * Test un getFromId
	 */
	@Test
	public void testGetDaoPlace() {
		IPlaceDAO dao = factory.getPlaceDAO();
		Optional<Place> pl = dao.getFromID(pla.getCode());
		assertTrue(pl.isPresent());	
		assertEquals(pl.get(), pla);
		// teste une autre place occupé
		pl = dao.getFromID(plb.getCode());
		assertNotNull(pl);
		assertEquals(pl.get(), plb);
	}
	
	
	/**
	 * Teste un getListe
	 */
	@Test
	public void testGetListeDaoPlace() {
		IPlaceDAO dao = factory.getPlaceDAO();
		List<Place> l = dao.getListe(null);
		//assertEquals(l.size(), 5);
		for(int i = 0; i<l.size();i++) {
			System.out.println(l.get(i).toString());
		}
		// Vérifie si pa existe dans la liste
		assertEquals(l.get(0), pla);
	}
	
	/**
	 * Test l'ajout et la suppression
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetInsertDeleteDaoPlace() throws Exception {
		IPlaceDAO dao = factory.getPlaceDAO();
		// Un nouvelle Place
		Place pln = new Place("P10", 10, true);
		String code = dao.insert(pln).getCode();
		// vérifie si le code renvoyé est le bon
		assertEquals(code, pln.getCode());
		// vérifie que l'on récupére bien la même place
		assertEquals(dao.getFromID(code).get(), pln); 
		// Test Supression
		assertTrue(dao.delete(pln));
		// Verifie qu'il n'existe plus
		Optional<Place> p = dao.getFromID(code);
		assertNull(p);
	}
	
	@Test
	public void testInsertUpdateDelete() throws Exception {
		IPlaceDAO dao = factory.getPlaceDAO();
		// Un nouvelle Place
		Place plNew = new Place("P12",10, true);
		Place plModif = new Place("P12",5, false);
		
		String code = dao.insert(plNew).getCode();
		// vérifie que l'on récupére bien la même place
		assertEquals(dao.getFromID(code).get(), plNew);
		// Modification
		boolean res = dao.update(plModif);
		// test ok
		assertTrue(res);
		// récupère l'objet pour voir si changement ok
		Optional<Place> plRetour = dao.getFromID(plModif.getCode());
		assertEquals(plModif, plRetour.get());
		// Annule l'enregistrement pour revenir au même point de départ
		assertTrue(dao.delete(plRetour.get()));
	}
	
	//Insertion avec Exception attendue
	@Test (expectedExceptions = ParkingPKException.class)
	public void testBadPKInsert() throws Exception {
		IPlaceDAO dao = factory.getPlaceDAO();
		// Une nouvelle place
		Place pln = new Place("P01", 10, false);
		dao.insert(pln).getCode();
	}
	
	//Methode count()
	@Test (expectedExceptions = UnsupportedOperationException.class)
	public void testCount() {	
		IPlaceDAO dao = factory.getPlaceDAO();
		Integer i = dao.count();
		assertEquals(i.intValue(),dao.getListe(null).size());
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
