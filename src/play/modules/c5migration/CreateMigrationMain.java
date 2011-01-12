package play.modules.c5migration;

import com.carbonfive.db.jdbc.schema.CreateDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Johno Crawford (johno@hellface.com)
 */
public class CreateMigrationMain {

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(args[0]));
            String dbDriver = properties.getProperty("db.driver");
            String dbUrl = properties.getProperty("db.url");
            String dbUsername = properties.getProperty("db.user");
            String dbPassword = properties.getProperty("db.pass");
            CreateDatabase createDatabase = new CreateDatabase(dbDriver, dbUrl, dbUsername, dbPassword);
            createDatabase.execute();
        } catch (IOException e) {
            System.err.println("Error creating database. " + e.toString());
        } catch (ClassNotFoundException e) {
            System.err.println("Error creating database. " + e.toString());
        } catch (SQLException e) {
            System.err.println("Error creating database. " + e.toString());
        }
    }
}
