package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Place;

public abstract class SQLPlaceDAO implements IPlaceDAO{
	private static final String SQL_FROM_ID = "Select CODE_PLA,TAILLE_PLA,LIBRE_PLA from TPLACE where CODE_PLA=?";
	private static final String SQL_LISTE = "Select CODE_PLA,TAILLE_PLA,LIBRE_PLA from TPLACE";
	private static final String SQL_INSERT = "INSERT INTO TPLACE (CODE_PLA,TAILLE_PLA,LIBRE_PLA) VALUES (?,?,?)";
	private static final String SQL_DELETE = "Delete from TPLACE WHERE CODE_PLA=?";
	private static final String SQL_UPDATE = "update TPLACE set TAILLE_PLA = ?,LIBRE_PLA = ? where trim(CODE_PLA) = ?";
	private static final String SQL_COUNT = "Select count (*) from TPLACE ";
	
	// Logger
		private static final Logger logger = LoggerFactory.getLogger(SQLPlaceDAO.class);

		// La factory pour avoir la connexion
		private final SQLDAOFactory factory;

		/**
		 * Construction pour avoir l'accès à la factory et ainsi obtenir la connexion
		 * 
		 * @param factory
		 */
		public SQLPlaceDAO(SQLDAOFactory factory) {
			this.factory = factory;
		}
		
		public Optional<Place> getFromID(String id) {

			Optional<Place> pl = null;
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
					pl = Optional.of(new Place(id, rs.getInt(2), rs.getBoolean(3)));
				}
			} catch (SQLException e) {
				logger.error("Erreur SQL ", e);

			}
			return pl;
		}
		
		public List<Place> getListe(String regExpr) {
			List<Place> liste = new ArrayList<>();
			try (PreparedStatement query = factory.getConnexion().prepareStatement(SQL_LISTE)) {
				Place pl;
				ResultSet rs;
				rs = query.executeQuery();
				while (rs.next()) {
					pl = new Place(rs.getString(1).trim(), rs.getInt(2), rs.getBoolean(3));
					liste.add(pl);
				}
			} catch (SQLException e) {
				logger.error("Erreur lors du chargement des Places", e);
			}
			return liste;
		}
		
		public Place insert(Place s) throws Exception {
			if (s == null)
				return null;
			try (PreparedStatement query = factory.getConnexion().prepareStatement(SQL_INSERT)) {
				query.setString(1, s.getCode());
				query.setInt(2, s.getTaille());
				query.setBoolean(3, s.getLibre());
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
		
		public boolean delete(Place s) throws Exception {
			boolean ok = false;
			if (s == null)
				return false;
			int cpt;
			String code = s.getCode().trim();
			try (PreparedStatement querySupp = factory.getConnexion().prepareStatement(SQL_DELETE)) {
				querySupp.setString(1, code);
				cpt = querySupp.executeUpdate();
				ok = (cpt != 0);
				logger.debug("Une place a été supprimée:", code);
				if (!querySupp.getConnection().getAutoCommit()) {
					logger.debug("Delete en Commit Manuel");
					querySupp.getConnection().commit();
				} else
					logger.debug("Delete en AutoCommit");
			} catch (SQLException e) {
				logger.error("Erreur de suppression de la Place " + s.toString(), e);
				if (!factory.getConnexion().getAutoCommit())
					this.factory.getConnexion().rollback();
				factory.dispatchSpecificException(e);
			}

			return ok;
		}
		
		@Override
		public boolean update(Place s) throws Exception {
			if (s == null)
				return false;

			try (PreparedStatement query = this.factory.getConnexion().prepareStatement(SQL_UPDATE)) {
				query.setInt(1, s.getTaille());
				query.setBoolean(2, s.getLibre());
				query.setString(3, s.getCode());
				query.execute();
				if (!query.getConnection().getAutoCommit()) {
					logger.debug("Update en Commit Manuel");
					query.getConnection().commit();
				} else
					logger.debug("Update en AutoCommit");
			} catch (SQLException e) {
				logger.error("Erreur de mise à jour d'une place " + s.toString(), e);
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

		
//		private static void dispatchSpecificException(SQLException e) throws ParkingException {
//
//			switch (e.getErrorCode()) {
//			case 335544665:// PK
//				throw new ParkingPKException(e.getMessage(), e.getErrorCode(), "CODE");
//			case 335544347:// Contrainte d'unicité, check
//				throw new ParkingConstraintException(e.getMessage(), e.getErrorCode(), extractField(e.getMessage()));
//			default:
//				throw new ParkingException(e.getMessage(), e.getErrorCode());
//			}
//		}
//
//		private static String extractField(String msg) {
//			return msg.substring(msg.indexOf('.') + 2, msg.indexOf(',') - 5);
//		}
}

