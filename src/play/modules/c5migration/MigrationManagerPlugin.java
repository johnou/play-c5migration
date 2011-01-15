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
        try {
            DriverManagerMigrationManager migrationManager = MigrationManagerFactory.createMigrationManager(null);
            SortedSet<Migration> pendingMigrations = migrationManager.pendingMigrations();
            if (pendingMigrations != null && pendingMigrations.size() > 0) {
                migrationManager.migrate();
            }
        } catch (MigrationException e) {
            Logger.error(e, "Failed to run migrations.");
        }
    }
}
