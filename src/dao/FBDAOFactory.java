package dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Fabrique concrète pour Firebird
 * 
 * @author Didier
 *
 */
public class FBDAOFactory extends SQLDAOFactory {

	public FBDAOFactory(Connection connexion) {
		super(connexion);
	}

	/**
	 * retourne une implémentation DAOPersonnel pour Firebird
	 */
	@Override
	public IPersonnelDAO getPersonnelDAO() {
		return new FBPersonnelDAO(this);
	}
	
	@Override
	public IPlaceDAO getPlaceDAO() {
		return new FBPlaceDAO(this);
	}
	
	@Override
	public IStationnementDAO getStationnementDAO() {
		return new FBStationnementDAO(this);
	}

	@Override
	public IHistoriqueDAO getHistoriqueDAO() {
		return new FBHistoriqueDAO(this);
	}
	
	/**
	 * Permet d'extraire le code d'erreur de Firebird et de redispatcher sous une
	 * exception indépendante du système de persistance
	 * 
	 * @param exc
	 * @throws ParkingException
	 */ 
	void dispatchSpecificException(Exception exc) throws ParkingException {
		SQLException e;
		if(!(exc instanceof SQLException))
			throw new ParkingException(exc.getMessage(),-1);
		
		e = (SQLException) exc;	
		switch (e.getErrorCode()) {
		case 335544665:// PK
			throw new ParkingPKException(e.getMessage(), e.getErrorCode(), extractFieldPK(e.getMessage()));
		case 335544347:// Contrainte d'unicité, check
			throw new ParkingConstraintException(e.getMessage(), e.getErrorCode(), extractField(e.getMessage()));
		case 335544517:
			if(e.getMessage().contains("Parking Plein"))
				throw new ParkingFullException(e.getMessage(),e.getErrorCode());
			else if (e.getMessage().contains("non disponible"))
				throw new ParkingPlaceBusyException(e.getMessage(),e.getErrorCode(), extractPlace(e.getMessage()));
		default:
			throw new ParkingException(e.getMessage(), e.getErrorCode());
		}
	}

	String extractFieldPK(String msg) {
		return msg.substring(msg.indexOf('(') + 2, msg.indexOf(')') - 13);
	}
	String extractField(String msg) {
		return msg.substring(msg.indexOf('.') + 2, msg.indexOf(',') - 5);
	}
	
	// RecupPlace pour récupérer juste la place dans le message et non toute la phrase (comme pour extractFieldPK par exemple)
		String extractPlace(String msg) {
			return msg.substring(msg.indexOf(';') + 21, msg.lastIndexOf(';') - 20);
		}
}
