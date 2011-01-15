package play.modules.c5migration;

import com.carbonfive.db.jdbc.schema.CreateDatabase;
import com.carbonfive.db.jdbc.schema.DropDatabase;
import com.carbonfive.db.migration.DriverManagerMigrationManager;
import com.carbonfive.db.migration.Migration;
import com.carbonfive.db.migration.MigrationException;
import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author Johno Crawford (johno@hellface.com)
 */
public enum MigrationCommand {

    CREATE {
        @Override
        public void execute(String configurationPath) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(configurationPath));

                String dbDriver = properties.getProperty("db.driver");
                String dbUrl = properties.getProperty("db.url");
                String dbUsername = properties.getProperty("db.user");
                String dbPassword = properties.getProperty("db.pass");
                new CreateDatabase(dbDriver, dbUrl, dbUsername, dbPassword).execute();
            } catch (ClassNotFoundException e) {
                System.err.println("~ Error: creating database failed. " + e.toString());
            } catch (SQLException e) {
                System.err.println("~ Error: creating database failed. " + e.toString());
            } catch (IOException e) {
                System.err.println("~ Error: creating database failed. " + e.toString());
            }
        }
    }, MIGRATE {
        @Override
        public void execute(String configurationPath) {
            DriverManagerMigrationManager migrationManager = MigrationManagerFactory.createMigrationManager(configurationPath);
            SortedSet<Migration> pendingMigrations = null;
            try {
                pendingMigrations = migrationManager.pendingMigrations();
            } catch (MigrationException e) {
                System.err.println("~ Error: failed to run migrations. " + e.toString());
            }
            if (pendingMigrations != null && pendingMigrations.size() > 0) {
                migrationManager.migrate();
            }
        }
    }, RESET {
        @Override
        public void execute(String configurationPath) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(configurationPath));

                String dbDriver = properties.getProperty("db.driver");
                String dbUrl = properties.getProperty("db.url");
                String dbUsername = properties.getProperty("db.user");
                String dbPassword = properties.getProperty("db.pass");
                new DropDatabase(dbDriver, dbUrl, dbUsername, dbPassword).execute(DropDatabase.DROP_DATABASE_SQL);
                DriverManagerMigrationManager migrationManager = MigrationManagerFactory.createMigrationManager(configurationPath);
                migrationManager.migrate();
            } catch (IOException e) {
                System.err.println("~ Error: resetting database failed. " + e.toString());
            } catch (ClassNotFoundException e) {
                System.err.println("~ Error: resetting database failed. " + e.toString());
            } catch (SQLException e) {
                System.err.println("~ Error: resetting database failed. " + e.toString());
            }
        }
    }, NEW {
        @Override
        public void execute(String configurationPath) {
            System.out.println("~ NEW");
        }
    }, CHECK {
        @Override
        public void execute(String configurationPath) {
            DriverManagerMigrationManager migrationManager = MigrationManagerFactory.createMigrationManager(configurationPath);
            Set<Migration> pendingMigrations = migrationManager.pendingMigrations();

            if (pendingMigrations.isEmpty()) {
                return;
            }

            List<String> pendingMigrationsNames = new ArrayList<String>();
            for (Migration migration : pendingMigrations) {
                pendingMigrationsNames.add(migration.getFilename());
            }

            String msg = String.format("~ There %s %d pending migrations: \n\n    %s\n\n    Run c5migration:migrate to apply pending migrations.",
                    pendingMigrations.size() == 1 ? "is" : "are",
                    pendingMigrations.size(),
                    StringUtils.join(pendingMigrationsNames, "\n    "));
            System.out.println(msg);
        }
    };

    abstract void execute(String configurationPath);
}
