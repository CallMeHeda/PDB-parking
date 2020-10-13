package dao;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SQLDAOFactory extends DAOFactory {
	private Connection connexion;

	/**
	 * Mémorise la connexion à la base de données
	 * 
	 * @param connexion
	 */
	public SQLDAOFactory(Connection connexion) {
		super();
		this.connexion = connexion;
	}

	/**
	 * Permet d'accéder à la connexion
	 * 
	 * @return
	 */
	public Connection getConnexion() {
		return connexion;
	}
	
}
