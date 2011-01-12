package play.modules.c5migration;

import com.carbonfive.db.migration.DriverManagerMigrationManager;
import com.carbonfive.db.migration.Migration;
import com.carbonfive.db.migration.MigrationException;
import com.carbonfive.db.migration.ResourceMigrationResolver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.SortedSet;

/**
 * @author Johno Crawford (johno@hellface.com)
 */
public class MigrateMigrationMain {

    private static final String DEFAULT_MIGRATIONS_PATH = "db/migrations";

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(args[0]));
            String dbDriver = properties.getProperty("db.driver");
            String dbUrl = properties.getProperty("db.url");
            String dbUsername = properties.getProperty("db.user");
            String dbPassword = properties.getProperty("db.pass");
            String migrationsPath = properties.getProperty("db.migrations.path", DEFAULT_MIGRATIONS_PATH);
            DriverManagerMigrationManager migrationManager = new DriverManagerMigrationManager(
                    dbDriver,
                    dbUrl,
                    dbUsername,
                    dbPassword);
            migrationManager.setMigrationResolver(new ResourceMigrationResolver(migrationsPath));
            SortedSet<Migration> pendingMigrations = null;
            try {
                pendingMigrations = migrationManager.pendingMigrations();
            } catch (MigrationException e) {
                System.err.println("Failed to run migrations. " + e.toString());
            }
            if (pendingMigrations != null && pendingMigrations.size() > 0) {
                migrationManager.migrate();
            }
        } catch (IOException e) {
            System.err.println("Error creating database. " + e.toString());
        }
    }
}
