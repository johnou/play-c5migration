package play.modules.c5migration;

import com.carbonfive.db.migration.DataSourceMigrationManager;
import com.carbonfive.db.migration.ResourceMigrationResolver;
import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.db.DB;
import play.exceptions.UnexpectedException;

/**
 * @author heikkiu
 */
public class MigrationPlugin extends PlayPlugin {

    @Override
    public void onApplicationStart() {
        String pattern = (String) Play.configuration.get("migrations.pattern");
        if (pattern != null) {
            Logger.info("Running migrations from pattern " + pattern);
            try {
                DataSourceMigrationManager manager = new DataSourceMigrationManager(DB.datasource);
                ResourceMigrationResolver resolver = new ResourceMigrationResolver(pattern);
                manager.setMigrationResolver(resolver);
                manager.migrate();
            } catch (Exception e) {
                throw new UnexpectedException("Database migrations failed", e);
            }
        } else {
            Logger.info("Missing configuration migrations.pattern, database migrations will be ignored!");
        }
    }

}
