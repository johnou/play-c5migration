package play.modules.c5migration;

import java.io.IOException;

/**
 * @author Johno Crawford (johno@hellface.com)
 */
public class MigrationMain {

    public static void main(String[] args) throws IOException {

        if (args.length < 2) {
            System.err.println("~ Arguments: <command> <application.conf>");
            System.exit(-1);
        }

        String command = args[0];
        MigrationCommand migrationCommand = MigrationCommand.valueOf(command.toUpperCase());
        if (migrationCommand != null) {
            migrationCommand.execute(args[1]);
        } else {
            System.err.println("~ Error: command not found!");
        }
    }
}
