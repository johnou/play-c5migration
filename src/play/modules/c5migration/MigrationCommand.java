package play.modules.c5migration;

import com.carbonfive.db.migration.DriverManagerMigrationManager;
import com.carbonfive.db.migration.Migration;
import com.carbonfive.db.migration.MigrationException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import play.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;

/**
 * @author Johno Crawford (johno@hellface.com)
 * @author heikkiu
 */
public enum MigrationCommand {

    MIGRATE {
        @Override
        public void execute(String configurationPath) {
            try {
                DriverManagerMigrationManager migrationManager = MigrationManagerFactory.createMigrationManager(configurationPath);
                SortedSet<Migration> pendingMigrations = migrationManager.pendingMigrations();
                if (pendingMigrations != null && pendingMigrations.size() > 0) {
                    migrationManager.migrate();
                }
            } catch (MigrationException e) {
                Logger.error(e, "~ ERROR: Failed to run migrations. ");
            }
        }
    }, NEW {
        private static final String versionPattern = "yyyyMMddHHmmss";
        private static final String versionTimeZone = "UTC";

        @Override
        public void execute(String configurationPath) {
            String path = System.getProperty("migration.path", "db/migrations");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please write description for you migration:");
            try {
                String description = reader.readLine();
                File directory = new File(path);
                if (!directory.exists()) {
                    System.out.println("Creating non-existent directory " + directory.getAbsolutePath());
                    directory.createNewFile();
                }
                String version = FastDateFormat.getInstance(versionPattern, TimeZone.getTimeZone(versionTimeZone)).format(new Date());
                description = StringUtils.replaceChars(description, ' ', '_');
                description = StringUtils.replaceChars(description, ".,:;", "");
                description = description.toLowerCase();
                File migrationFile = new File(directory, version + "_" + description + ".sql");
                migrationFile.createNewFile();
                PrintWriter writer = new PrintWriter(new FileWriter(migrationFile));
                writer.println("-- You can write you comments here ");
                writer.close();
                System.out.println("New migration file created " + migrationFile.getAbsolutePath());
            } catch (Exception e) {
                Logger.error(e, "~ ERROR: Failed to create migration file. ");
            }
            System.exit(0);
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
