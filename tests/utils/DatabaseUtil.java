package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;

import org.apache.ibatis.jdbc.ScriptRunner;

public class DatabaseUtil {

	/**
	 * Initialise la base de données de test Utilise MyBatis pour exécuter le script
	 * SQL
	 * 
	 * @param con  connection à la base de données
	 * @param file le fichier SQL à exécuter
	 * @throws FileNotFoundException
	 */

	public static void executeScriptSQL(Connection con, String file) throws FileNotFoundException {
		// Initialize the script runner
		ScriptRunner sr = new ScriptRunner(con);
		// Creating a reader object
		Reader reader = new BufferedReader(new FileReader(file));
		// Running the script
		sr.runScript(reader);
	}

}
