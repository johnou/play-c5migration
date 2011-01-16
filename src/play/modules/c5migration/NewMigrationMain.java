package play.modules.c5migration;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author heikkiu
 */
public class NewMigrationMain {

    private static String path = System.getProperty("migration.path", "conf/migrations");

    private static final SimpleDateFormat versionFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Please write description for you migration:");
            String description = reader.readLine();
            try {
                File directory = new File(path);
                if (!directory.exists()) {
                    System.out.println("Creating non-existent directory " + directory.getAbsolutePath());
                    directory.createNewFile();
                }
                String version = versionFormat.format(new Date());
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
                e.printStackTrace();
            }
            System.exit(0);
        }
    }
}
