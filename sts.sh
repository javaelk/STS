#!/bin/bash -x
export JAVA_HOME=/home/wliu/java/jdk1.7.0_03/bin
echo $JAVA_HOME
export PATH=/home/wliu/java/jdk1.7.0_03/bin:$PATH
echo $PATH
which java
java -Dlog4j.configuration="file:log4j.properties" -jar sts.jar config/configuration.property
date
