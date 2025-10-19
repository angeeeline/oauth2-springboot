#!/usr/bin/env sh
# Apache Maven Wrapper startup script
# Minimal implementation for Linux/macOS

set -e

# Resolve location
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
BASE_DIR=$(pwd -P)
cd "$SAVED_PWD" >/dev/null

WRAPPER_DIR="$BASE_DIR/.mvn/wrapper"
WRAPPER_JAR="$WRAPPER_DIR/maven-wrapper.jar"
PROPS_FILE="$WRAPPER_DIR/maven-wrapper.properties"

# Determine Java command
if [ -n "$JAVA_HOME" ] ; then
  JAVACMD="$JAVA_HOME/bin/java"
  if [ ! -x "$JAVACMD" ] ; then
    echo "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME" >&2
    exit 1
  fi
else
  JAVACMD="java"
fi

# Ensure wrapper dir exists
mkdir -p "$WRAPPER_DIR"

# Download wrapper jar if missing
if [ ! -f "$WRAPPER_JAR" ]; then
  WRAPPER_URL=$(sed -n 's/^wrapperUrl=//p' "$PROPS_FILE")
  if [ -z "$WRAPPER_URL" ]; then
    WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
  fi
  echo "Downloading Maven Wrapper from: $WRAPPER_URL" >&2
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL -o "$WRAPPER_JAR" "$WRAPPER_URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$WRAPPER_JAR" "$WRAPPER_URL"
  else
    echo "ERROR: Need curl or wget to download Maven Wrapper." >&2
    exit 1
  fi
fi

# JVM options
DEFAULT_JVM_OPTS=${DEFAULT_JVM_OPTS:-"-Xmx64m -Xms64m"}

exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $MAVEN_OPTS \
  -classpath "$WRAPPER_JAR" \
  -Dmaven.multiModuleProjectDirectory="$BASE_DIR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
