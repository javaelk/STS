#!/bin/bash -x
export JAVA_HOME=/home/wliu/java/jdk1.7.0_09/bin
echo $JAVA_HOME
export PATH=/home/wliu/java/jdk1.7.0_09/bin:$PATH
echo $PATH
which java
java -Dlog4j.configuration="file:log4j.properties" -jar arts.jar config/ARTSConfiguration.property
date
