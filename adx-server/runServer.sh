#!/bin/bash
#
# Usage
#   sh ./runServer.sh
#

TACAA_HOME=`pwd`
echo $TACAA_HOME
echo $CLASSPATH

java -cp "lib/*" tau.tac.adx.agentware.Main -config config/giza-aw-1.conf &
java -cp "lib/*" tau.tac.adx.agentware.Main -config config/agent00-aw-1.conf &
java -cp "lib/*" tau.tac.adx.agentware.Main -config config/bcm-aw-1.conf &
java -cp "lib/*" tau.tac.adx.agentware.Main -config config/oos-aw-1.conf &
java -cp "lib/*" se.sics.tasim.sim.Main
