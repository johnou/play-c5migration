package play.modules.c5migration;

import com.carbonfive.db.migration.DriverManagerMigrationManager;
import com.carbonfive.db.migration.ResourceMigrationResolver;
import play.Logger;
import play.Play;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Johno Crawford (johno@hellface.com)
 */
public class MigrationManagerFactory {

    public static final String DEFAULT_MIGRATIONS_PATH = "db/migrations";

    public static DriverManagerMigrationManager createMigrationManager(String configurationPath) {
        DriverManagerMigrationManager migrationManager = null;
        try {
            Properties properties = new Properties();
            if (configurationPath != null) {
                properties.load(new FileInputStream(configurationPath));
            } else {
                properties = Play.configuration;
            }

            String dbDriver = properties.getProperty("db.driver");
            String dbUrl = properties.getProperty("db.url");
            String dbUsername = properties.getProperty("db.user");
            String dbPassword = properties.getProperty("db.pass");
            String migrationsPath = properties.getProperty("db.migrations.path", DEFAULT_MIGRATIONS_PATH);
            migrationManager = new DriverManagerMigrationManager(dbDriver, dbUrl, dbUsername, dbPassword);
            migrationManager.setMigrationResolver(new ResourceMigrationResolver(migrationsPath));
        } catch (IOException e) {
            Logger.error(e, "~ Error: creating migration manager failed!");
        }
        return migrationManager;
    }
}
