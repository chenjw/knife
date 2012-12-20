#!/bin/bash
java -cp .:lib/jline-1.0.jar:lib/fastjson-1.1.17.jar:lib/knife-client.jar:$JAVA_HOME/lib/tools.jar com.chenjw.knife.client.ProxyMain $*
