import os
import subprocess
from time import time

MODULE = 'c5migration'

COMMANDS = ['c5migration:create', 'c5migration:migrate', 'c5migration:reset', 'c5migration:new', 'c5migration:check']

HELP = {
    'c5migration:create': ' Create a new, empty database',
    'c5migration:migrate': ' Apply all pending migrations',
    'c5migration:reset': ' Drop the existing database, create a new one, and apply all pending migrations',
    'c5migration:new': ' Create a new, empty migration script',
    'c5migration:check': ' Check for pending migrations, fail the build if the db is not up to date'
}

def callJava(app, args, message, clazz):
    print "~ %s" % (message)
    java_args = []
    configuration = os.path.join(app.path, 'conf/application.conf')
    java_args.append(configuration)
    java_cmd = app.java_cmd(args, None, clazz, java_args)
    try:
        subprocess.call(java_cmd, env=os.environ)
    except OSError:
        print "Could not execute the java executable, please make sure the JAVA_HOME environment variable is set properly (the java executable should reside at JAVA_HOME/bin/java). "
        sys.exit(-1)
    print "~ "

def execute(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    if command == "c5migration:create":
        callJava(app, args, "Creating new database..", "play.modules.c5migration.CreateMigrationMain")

    if command == "c5migration:migrate":
        callJava(app, args, "Applying all pending migrations..", "play.modules.c5migration.MigrateMigrationMain")

    if command == "c5migration:reset":
        callJava(app, args, "Dropping existing database, creating new one and applying all migrations..", "play.modules.c5migration.ResetMigrationMain")

    if command == "c5migration:new":
        callJava(app, args, "Creating new migration script..", "play.modules.c5migration.NewMigrationMain");

    if command == "c5migration:check":
        callJava(app, args, "Checking for pending migrations..", "play.modules.c5migration.CheckMigrationMain");
