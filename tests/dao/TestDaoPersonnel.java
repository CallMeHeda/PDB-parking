package dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
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
import model.Personnel;
import utils.DatabaseUtil;

public class TestDaoPersonnel {
	private DAOFactory factory;

	private final Personnel pa = new Personnel("AAA-111", "de Block", "Anne", "anne.deblock@isfce.be");
	private final Personnel pb = new Personnel("BBB-222", "Deboeck", "Robert", null);

	/**
	 * Test un getFromId
	 */
	@Test
	public void testGetDaoPersonnel() {
		IPersonnelDAO dao = factory.getPersonnelDAO();
		Optional<Personnel> p = dao.getFromID(pa.getImmatr());
		// Vérifie si l'objet existe
		assertTrue(p.isPresent());
		assertEquals(p.get(), pa);
		// teste une autre personne avec une adresse email à null
		p = dao.getFromID(pb.getImmatr());
		assertTrue(p.isPresent());
		assertEquals(p.get(), pb);
		System.out.println(p);
	}

	/**
	 * Teste un getListe
	 */
	@Test
	public void testGetListeDaoPersonnel() {
		IPersonnelDAO dao = factory.getPersonnelDAO();
		List<Personnel> l = dao.getListe(null);
		//assertEquals(l.size(), 5);
		for(int i = 0; i<l.size();i++) {
			System.out.println(l.get(i).toString());
		}
		// Vérifie si pa existe dans la liste
		assertEquals(l.get(0), pa);
	}

	/**
	 * Test l'ajout et la suppression
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetInsertDeleteDaoPersonnel() throws Exception {
		IPersonnelDAO dao = factory.getPersonnelDAO();
		// Un nouvelle personne
		Personnel pn = new Personnel("XXX-123", "Test", "test", "test.Test@gmail.com");
		String immatr = dao.insert(pn).getImmatr();
		// vérifie si le code renvoyé est le bon
		assertEquals(immatr, pn.getImmatr());
		// vérifie que l'on récupére bien la même personne
		Optional<Personnel> p = dao.getFromID(immatr);
		assertTrue(p.isPresent());
		assertEquals(p.get(), pn);
		// Test Supression
		assertTrue(dao.delete(pn));
		// Verifie qu'il n'existe plus
		//p = dao.getFromID(immatr);
		//assertFalse(p.isPresent());
	}

	@Test
	public void testInsertUpdateDelete() throws Exception {
		IPersonnelDAO dao = factory.getPersonnelDAO();
		// Un nouvelle personne
		Personnel pNew = new Personnel("XXX-123", "Test", "test", "test.Test@gmail.com");
		Personnel pModif = new Personnel("XXX-123", "Test2", "test2", "test2.Test@gmail.com");
		String immatr = dao.insert(pNew).getImmatr();
		// vérifie que l'on récupére bien la même personne
		Optional<Personnel> p = dao.getFromID(immatr);
		assertTrue(p.isPresent());
		assertEquals(p.get(), pNew);
		// Modification
		boolean res = dao.update(pModif);
		// test ok
		assertTrue(res);
		// récupère l'objet pour voir si changement ok
		Optional<Personnel> pRetour = dao.getFromID(pModif.getImmatr());
		assertTrue(pRetour.isPresent());
		assertEquals(pModif, pRetour.get());
		// Annule l'enregistrement pour revenir au même point de départ
		assertTrue(dao.delete(pRetour.get()));

	}

	// Insertion avec Exception attendue
	@Test(expectedExceptions = ParkingPKException.class)
	public void testBadPKInsert() throws Exception {
		IPersonnelDAO dao = factory.getPersonnelDAO();
		// Un nouvelle personne
		Personnel pn = new Personnel("AAA-111", "Test", "test", "test.Test@gmail.com");
		dao.insert(pn).getImmatr();
	}

	// Insertion avec Exception attendue
	@Test(expectedExceptions = ParkingConstraintException.class)
	public void testPasDeNomInsert() throws Exception {
		IPersonnelDAO dao = factory.getPersonnelDAO();
		// Un nouvelle personne
		Personnel pn = new Personnel("XYZ-444", null, "test", "test.Test@gmail.com");
		dao.insert(pn).getImmatr();
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

//	@Test (expectedExceptions = UnsupportedOperationException.class)
//	public void testCount() {
//		IPersonnelDAO dao = factory.getPersonnelDAO();
//		int i=dao.count();
//	}

}
