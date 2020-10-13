/**
 * @author E. Fatima
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Personnel;
import model.Place;
import model.SortieVoiture;
import model.Stationnement;

/**
 * @author E. Fatima
 */
public abstract class SQLStationnementDAO implements IStationnementDAO {
	private static final String SQL_FROM_ID = "Select * from TStationnement where FKPersonne_Sta=?";
	private static final String SQL_LISTE = "Select * from TStationnement";
	private static final String SQL_COUNT = "Select count (*) from TStationnement ";
	private static final String SQL_ARRIVEE_VOIT = "CALL ARRIVEE_VOIT(?,?,?)";
	private static final String SQL_SORTIE_VOIT = "CALL SORTIE_VOIT(?,?,?)";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(SQLStationnementDAO.class);

	// La factory pour avoir la connexion
	private final SQLDAOFactory factory;

	/**
	 * Construction pour avoir l'accès à la factory et ainsi obtenir la connexion
	 * 
	 * @param factory
	 */
	public SQLStationnementDAO(SQLDAOFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * getFromID
	 * @param l'immatriculaion de la voiture
	 * @return un Objet stationnement
	 */
	public Optional<Stationnement> getFromID(String id) {
		Stationnement sta = null;
		Optional<Personnel> p = null;
		if (id != null)
			id = id.trim();
		try (PreparedStatement query = factory.getConnexion().prepareStatement(SQL_FROM_ID);) {
			ResultSet rs;
			query.setString(1, id);
			rs = query.executeQuery();
			if (rs.next()) {
				p = factory.getPersonnelDAO().getFromID(id);
				sta = new Stationnement(p.get(), rs.getString(2).trim(), rs.getTimestamp(3).toLocalDateTime());
			}
		} catch (SQLException e) {
			logger.error("Erreur SQL ", e);

		}
		return Optional.ofNullable(sta);
	}

	/**
	 * getList
	 * @param string
	 * @return une liste de Stationnement
	 */
	public List<Stationnement> getListe(String regExpr) {
		List<Stationnement> liste = new ArrayList<>();
		Optional<Personnel> p = null;
		try (PreparedStatement query = factory.getConnexion().prepareStatement(SQL_LISTE)) {
			Stationnement sta;
			ResultSet rs;
			//query.setString(1, id);
			rs = query.executeQuery();
			while (rs.next()) {
				p = factory.getPersonnelDAO().getFromID(rs.getString(1));
				sta = new Stationnement(p.get(), rs.getString(2), rs.getTimestamp(3).toLocalDateTime());
				liste.add(sta);
			}
		} catch (SQLException e) {
			logger.error("Erreur lors du chargement des Stationnements", e);
		}
		return liste;
	}
		
	public Integer getCount(String regExpr ) {
		Integer i=null;
		try (PreparedStatement query = factory.getConnexion().prepareStatement(SQL_COUNT)) {
			ResultSet rs;
			rs = query.executeQuery();
			rs.next();
			i=rs.getInt(1);
		} catch (SQLException e) {
			logger.error("Erreur lors du chargement des Stationnements", e);
		}
		return i;
	}
	
	/**
	 * Methode ARRIVEE_VOIT(String immatr,String place) 
	 */
	public Stationnement arrivee_voit(String immatr, String place) throws SQLException, ParkingException {
		Stationnement arriveeVoit2 = null;
		Optional<Personnel> p = null;
		if(immatr != null && !(immatr.isEmpty()) && (place != null && !(place.isEmpty()))) {
			immatr = immatr.trim();
			place = place.trim();
			
		}
		try (CallableStatement call = factory.getConnexion().prepareCall(SQL_ARRIVEE_VOIT)){
			call.setString(1, immatr);
			call.setString(2, place);
			call.registerOutParameter(2, Types.CHAR);
			call.registerOutParameter(3, Types.TIMESTAMP);
			call.execute();
			if(call.getResultSet().next()) {	
				p = factory.getPersonnelDAO().getFromID(immatr);
				arriveeVoit2 = new Stationnement(p.get(),call.getString(2),call.getTimestamp(3).toLocalDateTime());
			}
			if(!call.getConnection().getAutoCommit()) {
				logger.debug("arrivee_voit en commit manuel");
				call.getConnection().commit();
			} else {
				logger.debug("arrivee_voit en AutoCommit");
			}
		}catch(SQLException e){	
			logger.error("ERREUR ARRIVEE_VOIT", e);
			if (!factory.getConnexion().getAutoCommit())
				this.factory.getConnexion().rollback();
			factory.dispatchSpecificException(e);
		}
		
		return arriveeVoit2;
	}
	
	/**
	 * Methode ARRIVEE_VOIT(String immatr) 
	 */
	public Stationnement arrivee_voit(String immatr) throws ParkingException, SQLException {
		return arrivee_voit(immatr,null);
//		Stationnement arriveeVoit = null;
//		Personnel p = null;
//		if(immatr != null && !(immatr.isEmpty())) {
//			immatr = immatr.trim();
//		}
//		try (CallableStatement call = factory.getConnexion().prepareCall(SQL_ARRIVEE_VOIT)){
//			call.setString(1, p.getImmatr());
//			call.registerOutParameter(2, Types.CHAR);
//			call.registerOutParameter(3, Types.TIMESTAMP);
//			call.execute();
//			if(call.getResultSet().next()) {
//				arriveeVoit = new Stationnement(p,call.getString(2),call.getTimestamp(3).toLocalDateTime());
//				factory.getConnexion().commit();
//				call.close();
//			}
//			else {
//				fail();
//			}
//		}catch(SQLException e){
//			//logger.error("ERREUR ARRIVEE_VOIT A 1 PARAM", e);
//				throw new VOSQLException("PARKING PLEIN", e.getErrorCode());
//		}
//		return arriveeVoit;
	}
	
	/**
	 * Methode SORTIE_VOIT(String immatr)
	 */
	public SortieVoiture sortie_voit(String immatr){
		SortieVoiture sortieVoit = null;
		try (CallableStatement call = factory.getConnexion().prepareCall(SQL_SORTIE_VOIT)){
			call.setString(1, immatr);
			call.registerOutParameter(2, Types.CHAR);
			call.registerOutParameter(3, Types.INTEGER);
			call.execute();
			if(call.getResultSet().next()) {
				sortieVoit = new SortieVoiture(call.getString(2),call.getInt(3));
				factory.getConnexion().commit();
				call.close();
			} else {
				fail();
			}
		}catch(SQLException e){
			logger.error("ERREUR SORTIE_VOIT", e);
		}
		return sortieVoit;
	}

//	private static void dispatchSpecificException(SQLException e) throws ParkingSQLException {
//		switch (e.getErrorCode()) {
//		case 335544665:// PK
//			throw new ParkingPKException(e.getMessage(), e.getErrorCode(), "CODE");
//		case 335544347:// Contrainte d'unicité, check
//			throw new ParkingConstraintException(e.getMessage(), e.getErrorCode(), extractField(e.getMessage()));
//		default:
//			throw new ParkingSQLException(e.getMessage(), e.getErrorCode());
//		}
//	}
//
//	private static String extractField(String msg) {
//		return msg.substring(msg.indexOf('.') + 2, msg.indexOf(',') - 5);
//	}

}