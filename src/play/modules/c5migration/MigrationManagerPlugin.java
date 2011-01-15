package play.modules.c5migration;

import com.carbonfive.db.migration.DriverManagerMigrationManager;
import com.carbonfive.db.migration.Migration;
import com.carbonfive.db.migration.MigrationException;
import play.Logger;
import play.PlayPlugin;

import java.util.SortedSet;

/**
 * @author Johno Crawford (johno@hellface.com)
 */
public class MigrationManagerPlugin extends PlayPlugin {

    @Override
    public void onApplicationStart() {
        DriverManagerMigrationManager migrationManager = MigrationManagerFactory.createMigrationManager(null);
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
