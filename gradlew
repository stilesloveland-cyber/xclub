#!/bin/sh
export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export ANDROID_HOME=/opt/android-sdk
exec /root/.local/share/mise/installs/gradle/8.14.4/gradle-8.14.4/bin/gradle "$@"
