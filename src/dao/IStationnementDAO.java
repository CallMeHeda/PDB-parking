package dao;

import java.sql.SQLException;

import model.SortieVoiture;
import model.Stationnement;

public interface IStationnementDAO extends IDAO<Stationnement, String>{
	/**
	 * ARRIVEE_VOIT(String immatr)
	 * @param immatr de la voiture
	 * @return un objet de type Stationnement en faisant appel à la même méthode avec 2 param
	 * @throws ParkingException
	 * @throws SQLException
	 */
	public Stationnement arrivee_voit(String immatr) throws ParkingException, SQLException;
	
	/**
	 * ARRIVEE_VOIT(IMMATR,PLACE)
	 * @param immatr de la voiture
	 * @param place de stationnement
	 * @return un Objet de type Stationnement
	 * @throws SQLException
	 * @throws ParkingException
	 */
	public Stationnement arrivee_voit(String immatr, String place) throws SQLException, ParkingException;
	
	/**
	 * SORTIE_VOIT(String immatr)
	 * @param immatr de la voiture
	 * @return Objet de type sortieVoiture qui contient la place et la durée de stationnement
	 */
	public SortieVoiture sortie_voit(String immatr) throws SQLException, ParkingException;
}
