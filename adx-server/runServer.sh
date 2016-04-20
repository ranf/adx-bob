#!/bin/bash
#
# Usage
#   sh ./runServer.sh
#

TACAA_HOME=`pwd`
echo $TACAA_HOME
echo $CLASSPATH

java -cp "lib/*" tau.tac.adx.agentware.Main -config config/giza-aw-1.conf > giza.log &
java -cp "lib/*" tau.tac.adx.agentware.Main -config config/agent00-aw-1.conf > agent00.log &
java -cp "lib/*" tau.tac.adx.agentware.Main -config config/bcm-aw-1.conf > bcm.log &
java -cp "lib/*" tau.tac.adx.agentware.Main -config config/oos-aw-1.conf > oos.log &
java -cp "lib/*" tau.tac.adx.agentware.Main -config config/ibm-aw-1.conf > ibm.log &
java -cp "lib/*" se.sics.tasim.sim.Main > server.log