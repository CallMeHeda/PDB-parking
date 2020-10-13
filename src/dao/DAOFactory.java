package dao;

import java.sql.Connection;

/**
 * Ma fabrique abstraite
 * 
 * @author Didier
 *
 */
public abstract class DAOFactory {
	/**
	 * Types de persistance possibles
	 * 
	 * @author Didier
	 *
	 */
	public enum TypePersistance {
		FIREBIRD, H2
	}

	// retourne les implémentations des DAO en fonction du type de persistance
	public abstract IPersonnelDAO getPersonnelDAO();
	
	public abstract IPlaceDAO getPlaceDAO();
	
	public abstract IStationnementDAO getStationnementDAO();
	
	public abstract IHistoriqueDAO getHistoriqueDAO();
	
	abstract void dispatchSpecificException(Exception e) throws ParkingException;
	
	/**
	 * Méthode statique pour générer une fabrique concrète
	 * 
	 * @param type      de persistance
	 * @param connexion une connexion SQL si bdSQL sinon null
	 * @return une fabrique concrète pour le type de persistance
	 */
	public static DAOFactory getDAOFactory(TypePersistance type, Connection connexion) {
		switch (type) {
		case FIREBIRD:
			if (connexion != null)
				return new FBDAOFactory(connexion);// une fabrique concrète pour Firebird
		case H2:
			return null;
		default:
			break;
		}
		return null;
	}
}
