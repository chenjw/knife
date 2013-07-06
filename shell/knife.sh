#!/bin/bash
cp $JAVA_HOME/lib/tools.jar lib/tools.jar
java -cp .:lib/sinetfactory.jar:lib/jline-1.0.jar:lib/fastjson-1.1.17.jar:lib/knife-client.jar:lib/tools.jar com.chenjw.knife.client.ClientMain $*
