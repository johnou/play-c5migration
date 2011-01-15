package play.modules.c5migration;

import com.carbonfive.db.jdbc.schema.CreateDatabase;
import com.carbonfive.db.jdbc.schema.DropDatabase;
import com.carbonfive.db.migration.DriverManagerMigrationManager;
import com.carbonfive.db.migration.Migration;
import com.carbonfive.db.migration.MigrationException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import play.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;

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
                Logger.error(e, "~ Error: creating database failed.");
            } catch (SQLException e) {
                Logger.error(e, "~ Error: creating database failed.");
            } catch (IOException e) {
                Logger.error(e, "~ Error: creating database failed.");
            }
        }
    }, DROP {
        @Override
        public void execute(String configurationPath) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(configurationPath));

                String dbDriver = properties.getProperty("db.driver");
                String dbUrl = properties.getProperty("db.url");
                String dbUsername = properties.getProperty("db.user");
                String dbPassword = properties.getProperty("db.pass");
                new DropDatabase(dbDriver, dbUrl, dbUsername, dbPassword).execute();
            } catch (ClassNotFoundException e) {
                Logger.error(e, "~ Error: creating database failed.");
            } catch (SQLException e) {
                Logger.error(e, "~ Error: creating database failed.");
            } catch (IOException e) {
                Logger.error(e, "~ Error: creating database failed.");
            }
        }
    }, MIGRATE {
        @Override
        public void execute(String configurationPath) {
            try {
                DriverManagerMigrationManager migrationManager = MigrationManagerFactory.createMigrationManager(configurationPath);
                SortedSet<Migration> pendingMigrations = null;
                try {
                    pendingMigrations = migrationManager.pendingMigrations();
                } catch (MigrationException e) {
                    Logger.error(e, "~ Error: failed to run migrations. ");
                }
                if (pendingMigrations != null && pendingMigrations.size() > 0) {
                    migrationManager.migrate();
                }
            } catch (MigrationException ignore) {
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
                new CreateDatabase(dbDriver, dbUrl, dbUsername, dbPassword).execute();
                DriverManagerMigrationManager migrationManager = MigrationManagerFactory.createMigrationManager(configurationPath);
                migrationManager.migrate();
            } catch (IOException e) {
                Logger.error(e, "~ Error: resetting database failed.");
            } catch (ClassNotFoundException e) {
                Logger.error(e, "~ Error: resetting database failed.");
            } catch (SQLException e) {
                Logger.error(e, "~ Error: resetting database failed.");
            } catch (MigrationException ignore) {
            }
        }
    }, NEW {
        private String versionPattern = "yyyyMMddHHmmss";
        private String versionTimeZone = "UTC";

        @Override
        public void execute(String configurationPath) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(configurationPath));
                String migrationsPath = properties.getProperty("db.migrations.path", MigrationManagerFactory.DEFAULT_MIGRATIONS_PATH);

                // Make sure the directory ends with a separator.
                if (!migrationsPath.endsWith("/") && !migrationsPath.endsWith("\"")) {
                    migrationsPath += "/";
                }

                migrationsPath = FilenameUtils.separatorsToUnix(FilenameUtils.getFullPath(migrationsPath));

                // Create the parent directories if they don't exist.
                try {
                    new File(migrationsPath).mkdirs();
                } catch (Exception e) {
                    Logger.error(e, "Failed to create migrations directory: %s", migrationsPath);
                }

                // Determine the name of the migration.
                StringBuffer sb = new StringBuffer(FastDateFormat.getInstance(versionPattern, TimeZone.getTimeZone(versionTimeZone)).format(new Date()));
                String name = System.getProperty("name", "");

                if (StringUtils.isNotBlank(name)) {
                    sb.append("_").append(name);
                }
                sb.append(".sql");

                String filename = migrationsPath + sb.toString();

                // Finally, create the file.
                Logger.info("Creating new migration %s.", filename);

                try {
                    new File(filename).createNewFile();
                } catch (IOException e) {
                    Logger.error(e, "Failed to create migration file: %s" + filename);
                }
            } catch (IOException e) {
                Logger.error(e, "~ Failed to create migration file. ");
            }
        }
    }, CHECK {
        @Override
        public void execute(String configurationPath) {
            try {

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
                Logger.info(msg);
            } catch (MigrationException ignore) {
            }
        }
    };

    abstract void execute(String configurationPath);
}
