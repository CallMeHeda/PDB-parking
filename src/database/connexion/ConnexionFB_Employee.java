package database.connexion;

import java.util.Properties;

public class ConnexionFB_Employee implements IConnexionInfos {

	
	@Override
	public Properties getProperties() {
		Properties props = new Properties();
		String user="sysdba";
		String pw="masterkey";
		props.setProperty("user", user);
		props.setProperty("password", pw);
		props.setProperty("encoding", "NONE");
		props.setProperty("url","jdbc:firebirdsql:employee");
		props.setProperty("autoCommit", "false");
		return props;
	}

}
