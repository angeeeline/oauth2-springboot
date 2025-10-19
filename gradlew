#!/usr/bin/env sh

# Gradle wrapper start script for Unix-like systems

set -e

# Resolve script location to support symlinks
PRG="$0"
while [ -h "$PRG" ]; do
  ls_output=$(ls -ld "$PRG")
  link=$(expr "$ls_output" : '.*-> \(.*\)$')
  if expr "$link" : '/.*' >/dev/null; then
    PRG="$link"
  else
    PRG=$(dirname "$PRG")/"$link"
  fi
done
SAVED_PWD=$(pwd)
cd "$(dirname "$PRG")" >/dev/null
APP_HOME=$(pwd -P)
cd "$SAVED_PWD" >/dev/null

CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Determine Java command
if [ -n "$JAVA_HOME" ] ; then
  JAVACMD="$JAVA_HOME/bin/java"
  if [ ! -x "$JAVACMD" ] ; then
    echo "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME" >&2
    echo "Please set the JAVA_HOME variable in your environment to match the location of your Java installation." >&2
    exit 1
  fi
else
  JAVACMD="java"
fi

# Default JVM options (can be overridden via env)
DEFAULT_JVM_OPTS=${DEFAULT_JVM_OPTS:-"-Xmx64m -Xms64m"}

exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
  -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
