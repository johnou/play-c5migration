package play.modules.c5migration;

import com.carbonfive.db.migration.DriverManagerMigrationManager;
import com.carbonfive.db.migration.Migration;
import com.carbonfive.db.migration.ResourceMigrationResolver;
import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Johno Crawford (johno@hellface.com)
 */
public class CheckMigrationMain {

    private static final String DEFAULT_MIGRATIONS_PATH = "db/migrations";

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(args[0]));
        String dbDriver = properties.getProperty("db.driver");
        String dbUrl = properties.getProperty("db.url");
        String dbUsername = properties.getProperty("db.user");
        String dbPassword = properties.getProperty("db.pass");

        String migrationsPath = properties.getProperty("db.migrations.path", DEFAULT_MIGRATIONS_PATH);

        System.out.println("Checking migrations at " + migrationsPath + ".");

        Set<Migration> pendingMigrations;
        DriverManagerMigrationManager migrationManager = new DriverManagerMigrationManager(
                dbDriver,
                dbUrl,
                dbUsername,
                dbPassword);
        migrationManager.setMigrationResolver(new ResourceMigrationResolver(migrationsPath));
        pendingMigrations = migrationManager.pendingMigrations();

        if (pendingMigrations.isEmpty()) {
            return;
        }

        List<String> pendingMigrationsNames = new ArrayList<String>();
        for (Migration migration : pendingMigrations) {
            pendingMigrationsNames.add(migration.getFilename());
        }

        String msg = String.format("There %s %d pending migrations: \n\n    %s\n\n    Run c5migration:migrate to apply pending migrations.",
                pendingMigrations.size() == 1 ? "is" : "are",
                pendingMigrations.size(),
                StringUtils.join(pendingMigrationsNames, "\n    "));
        System.out.println(msg);
    }
}
