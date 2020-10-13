package dao;

//import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Personnel;

public abstract class SQLPersonnelDAO implements IPersonnelDAO {
	private static final String SQL_FROM_ID = "Select IMMATR_PER,NOM_PER,PRENOM_PER,EMAIL_PER from TPERSONNEL where IMMATR_PER=?";
	private static final String SQL_LISTE = "Select IMMATR_PER,NOM_PER,PRENOM_PER,EMAIL_PER from TPERSONNEL";
	private static final String SQL_INSERT = "INSERT INTO TPERSONNEL (IMMATR_PER,NOM_PER,PRENOM_PER,EMAIL_PER) VALUES (?,?,?,?)";
	private static final String SQL_DELETE = "Delete from TPERSONNEL WHERE IMMATR_PER=?";
	private static final String SQL_UPDATE = "update TPERSONNEL set NOM_PER = ?,PRENOM_PER = ?,EMAIL_PER = ? where trim(IMMATR_PER) = ?";
	private static final String SQL_COUNT = "Select count (*) from TPERSONNEL ";
	// Logger
	private static final Logger logger = LoggerFactory.getLogger(SQLPersonnelDAO.class);

	// La factory pour avoir la connexion
	private final SQLDAOFactory factory;

	/**
	 * Construction pour avoir l'accès à la factory et ainsi obtenir la connexion
	 * 
	 * @param factory
	 */
	public SQLPersonnelDAO(SQLDAOFactory factory) {
		this.factory = factory;
	}
	
	

	public Optional<Personnel> getFromID(String id) {

		Optional<Personnel> p = null;
		if (id != null)
			id = id.trim();
		try (PreparedStatement query = factory.getConnexion().prepareStatement(SQL_FROM_ID);) {
			ResultSet rs;
			// préparation d'un query

			// associe une valeur au paramètre (code_art)
			query.setString(1, id);
			// exécution
			rs = query.executeQuery();
			// parcourt du ResultSet
			if (rs.next()) {
				p = Optional.of(new Personnel(id, rs.getString(2), rs.getString(3), rs.getString(4)));
			}
		} catch (SQLException e) {
			logger.error("Erreur SQL ", e);

		}
		return p;
	}

	public List<Personnel> getListe(String regExpr) {
		List<Personnel> liste = new ArrayList<>();
		try (PreparedStatement query = factory.getConnexion().prepareStatement(SQL_LISTE)) {
			Personnel p;
			ResultSet rs;

			rs = query.executeQuery();
			while (rs.next()) {
				p = new Personnel(rs.getString(1).trim(), rs.getString(2), rs.getString(3), rs.getString(4));
				liste.add(p);
			}
		} catch (SQLException e) {
			logger.error("Erreur lors du chargement des Personnes", e);
		}
		return liste;
	}
	
	public Personnel insert(Personnel s) throws Exception {
		if (s == null)
			return null;
		try (PreparedStatement query = factory.getConnexion().prepareStatement(SQL_INSERT)) {
			query.setString(1, s.getImmatr());
			query.setString(2, s.getNom());
			query.setString(3, s.getPrenom());
			query.setString(4, s.getEmail());
			query.executeUpdate();
			if (!query.getConnection().getAutoCommit()) {
				logger.debug("Insert en Commit Manuel");
				query.getConnection().commit();
			} else
				logger.debug("Insert en AutoCommit");

		} catch (SQLException e) {
			logger.error("Erreur d'insertion de la personne ", e);
			if (!factory.getConnexion().getAutoCommit())
				this.factory.getConnexion().rollback();
			factory.dispatchSpecificException(e);
		}
		return s;
	}

	/**
	 * Supprime une Personne
	 */

	public boolean delete(Personnel s) throws Exception {
		boolean ok = false;
		if (s == null)
			return false;
		int cpt;
		String code = s.getImmatr().trim();
		try (PreparedStatement querySupp = factory.getConnexion().prepareStatement(SQL_DELETE)) {
			querySupp.setString(1, code);
			cpt = querySupp.executeUpdate();
			ok = (cpt != 0);
			logger.debug("Une personne a été supprimée:", code);
			if (!querySupp.getConnection().getAutoCommit()) {
				logger.debug("Delete en Commit Manuel");
				querySupp.getConnection().commit();
			} else
				logger.debug("Delete en AutoCommit");
		} catch (SQLException e) {
			logger.error("Erreur de suppression de la personne " + s.toString(), e);
			if (!factory.getConnexion().getAutoCommit())
				this.factory.getConnexion().rollback();
			factory.dispatchSpecificException(e);
		}

		return ok;
	}

	@Override
	public boolean update(Personnel s) throws Exception {
		if (s == null)
			return false;

		try (PreparedStatement query = this.factory.getConnexion().prepareStatement(SQL_UPDATE)) {
			query.setString(1, s.getNom());
			query.setString(2, s.getPrenom());
			query.setString(3, s.getEmail());
			query.setString(4, s.getImmatr());
			query.execute();
			if (!query.getConnection().getAutoCommit()) {
				logger.debug("Update en Commit Manuel");
				query.getConnection().commit();
			} else
				logger.debug("Update en AutoCommit");
		} catch (SQLException e) {
			logger.error("Erreur de mise à jour d'une personne " + s.toString(), e);
			if (!factory.getConnexion().getAutoCommit())
				this.factory.getConnexion().rollback();
			factory.dispatchSpecificException(e);
		}

		return true;
	}
	
	public Integer getCount(String regExpr ) {
		Integer i=null;
		try (PreparedStatement query = factory.getConnexion().prepareStatement(SQL_COUNT)) {
			ResultSet rs;
			rs = query.executeQuery();
			rs.next();
			i=rs.getInt(1);
		} catch (SQLException e) {
			logger.error("Erreur lors du chargement des Personnes", e);
		}
		return i;
	}

//	private static void dispatchSpecificException(SQLException e) throws ParkingException {
//
//		switch (e.getErrorCode()) {
//		case 335544665:// PK
//			throw new ParkingPKException(e.getMessage(), e.getErrorCode(), "IMMATR");
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
