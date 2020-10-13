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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Historique;

public abstract class SQLHistoriqueDAO implements IHistoriqueDAO {
	private static final String SQL_FROM_ID = "Select * from THistorique where FKPersonne_His=?";
	private static final String SQL_LISTE = "Select * from THistorique h order by h.MOMENTA_HIS desc";
	private static final String SQL_COUNT = "Select count (*) from THistorique ";
	private static final String SQL_HISTORIQUE = "CALL HISTORIQUE(?,?,?,?,?)";

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(SQLHistoriqueDAO.class);

	// La factory pour avoir la connexion
	private final SQLDAOFactory factory;

	/**
	 * Construction pour avoir l'accès à la factory et ainsi obtenir la connexion
	 * 
	 * @param factory
	 */
	public SQLHistoriqueDAO(SQLDAOFactory factory) {
		this.factory = factory;
	}
	
	public Optional<Historique> getFromID(String id) {
		Historique histo = null;
		if (id != null)
			id = id.trim();
		try (PreparedStatement query = factory.getConnexion().prepareStatement(SQL_FROM_ID);) {
			ResultSet rs;
			// préparation d'un query

			// associe une valeur au paramètre (FK_Personne)
			query.setString(1, id);
			// exécution
			rs = query.executeQuery();
			// parcourt du ResultSet
			if (rs.next()) {
				histo = new Historique(id, rs.getTimestamp(2).toLocalDateTime(), rs.getString(3), rs.getTimestamp(4).toLocalDateTime());
			}
		} catch (SQLException e) {
			logger.error("Erreur SQL ", e);

		}
		return Optional.ofNullable(histo);
	}

	public List<Historique> getListe(String regExpr) {
		List<Historique> liste = new ArrayList<>();
		try (PreparedStatement query = factory.getConnexion().prepareStatement(SQL_LISTE)) {
			Historique sta;
			ResultSet rs;
			rs = query.executeQuery();
			while (rs.next()) {
				sta = new Historique(rs.getString(1).trim(), rs.getTimestamp(2).toLocalDateTime(), rs.getString(3).trim(), rs.getTimestamp(4).toLocalDateTime());
				liste.add(sta);
			}
		} catch (SQLException e) {
			logger.error("Erreur lors du chargement de l'Historique", e);
		}
		return liste;
	}
	
	/**
	 * HISTORIQUE de stationnement d'une voiture
	 */
	public List<Historique> historique_voit(String immatr) {
		List<Historique> histoV = new ArrayList<>();
		Historique histo;
		try (CallableStatement call = factory.getConnexion().prepareCall(SQL_HISTORIQUE)) {
			call.setString(1, immatr);
			call.registerOutParameter(2, Types.TIMESTAMP);
			call.registerOutParameter(3, Types.TIMESTAMP);
			call.registerOutParameter(4, Types.INTEGER);
			call.registerOutParameter(5, Types.CHAR);
			call.execute();
			System.out.println("Stationnement(s) de la voiture " + immatr + " :");
			while (call.getResultSet().next()) {
//				histo = "Place de stationnement : "+ call.getString(5) + 
//						"\nMoment d'arrivee : " + call.getTimestamp(2) + "\nMoment de sortie : " + call.getTimestamp(3) + 
//						"\nDuree de stationnement : " + call.getInt(4) + "\n";
				histo = new Historique(immatr,call.getTimestamp(2).toLocalDateTime(),call.getString(5),call.getTimestamp(3).toLocalDateTime(),
						call.getInt(4));
				histoV.add(histo);
			}
		} catch (SQLException e) {
			logger.error("Erreur lors du chargement des Historiques", e);
		}
		return histoV;
	}
	
	/**
	 * 
	 * @param regExpr
	 * @return
	 */	
	public Integer getCount(String regExpr ) {
		Integer i=null;
		try (PreparedStatement query = factory.getConnexion().prepareStatement(SQL_COUNT)) {
			ResultSet rs;
			rs = query.executeQuery();
			rs.next();
			i=rs.getInt(1);
		} catch (SQLException e) {
			logger.error("Erreur lors du chargement de l'Historiques", e);
		}
		return i;
	}

//	private static void dispatchSpecificException(SQLException e) throws ParkingException {
//		switch (e.getErrorCode()) {
//		case 335544665:// PK
//			throw new ParkingPKException(e.getMessage(), e.getErrorCode(), "CODE");
//		case 335544347:// Contrainte d'unicité, check
//			throw new ParkingConstraintException(e.getMessage(), e.getErrorCode(), extractField(e.getMessage()));
//		default:
//			throw new ParkingException(e.getMessage(), e.getErrorCode());
//		}
//	}
//
//	private static String extractField(String msg) {
//		return msg.substring(msg.indexOf('.') + 2, msg.indexOf(',') - 5);
//	}

}