package play.modules.c5migration;

import com.carbonfive.db.migration.DriverManagerMigrationManager;
import com.carbonfive.db.migration.Migration;
import com.carbonfive.db.migration.MigrationException;
import com.carbonfive.db.migration.ResourceMigrationResolver;
import play.Logger;
import play.Play;
import play.PlayPlugin;

import java.util.Properties;
import java.util.SortedSet;

/**
 * @author Johno Crawford (johno@hellface.com)
 */
public class MigrationManagerPlugin extends PlayPlugin {

    private static final String DEFAULT_MIGRATIONS_PATH = "db/migrations";

    @Override
    public void onApplicationStart() {
        Properties properties = Play.configuration;
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
            Logger.error("Failed to run migrations.", e);
        }
        if (pendingMigrations != null && pendingMigrations.size() > 0) {
            migrationManager.migrate();
        }
    }
}
