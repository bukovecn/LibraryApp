package utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
	
	private Properties prop = new Properties();
	
    public DBUtil()
    {
    	try {
			getValuesFromConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	private void getValuesFromConfig() throws IOException {
		InputStream input = null;

		try {
			String propFileName = "/config/config.properties";
			input = getClass().getClassLoader().getResourceAsStream(propFileName);
			
			if (input != null) {
				prop.load(input);
			} else {
				System.out.println("Config file '" + propFileName + "' not found.");
			}
	
		} catch (Exception e) {
			System.out.println("Error while reading config file: " + e);
		} finally {
			if(input != null)
				input.close();
		}

	}
	
	public Connection connectToDB() throws URISyntaxException, SQLException, ClassNotFoundException {
	     
        //pattern: jdbc:postgresql://hostname:port/dbname?sslmode=require
        String connectionString = "jdbc:postgresql://" + prop.getProperty("db.host") + ':' + prop.getProperty("db.port") + "/" +prop.getProperty("db.name") + "?sslmode=require";
        Class.forName("org.postgresql.Driver");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(connectionString, prop.getProperty("db.user"), prop.getProperty("db.password"));
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
 
        return conn;
    }
}
