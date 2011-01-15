import os
import subprocess

MODULE = 'c5migration'

COMMANDS = ['c5migration:create', 'c5migration:drop', 'c5migration:migrate', 'c5migration:reset', 'c5migration:new', 'c5migration:check']

HELP = {
    'c5migration:create': ' Create empty database',
    'c5migration:drop': ' Drop database (use with caution!)',
    'c5migration:migrate': ' Apply all pending migrations',
    'c5migration:reset': ' Drop the existing database, create a new one, and apply all pending migrations (use with caution!)',
    'c5migration:new': ' Create a new, empty migration script',
    'c5migration:check': ' Check for pending migrations, fail the build if the db is not up to date'
}

def callJava(app, args, command):
    java_args = []
    java_args.append(command);
    configuration = os.path.join(app.path, 'conf/application.conf')
    java_args.append(configuration)
    java_cmd = app.java_cmd(args, None, 'play.modules.c5migration.MigrationMain', java_args)
    try:
        subprocess.call(java_cmd, env=os.environ)
    except OSError:
        print "~ ERROR: Could not execute the java executable, please make sure the JAVA_HOME environment variable is set properly (the java executable should reside at JAVA_HOME/bin/java). "
        sys.exit(-1)
    print "~ "

def execute(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    if command.startswith("c5migration:"):
        command = command.split(":");
        callJava(app, args, command[1])
