#!/bin/bash
#
# Usage
#   sh ./runServer.sh
#

TACAA_HOME=`pwd`
echo $TACAA_HOME
echo $CLASSPATH
java -cp "lib/*" se.sics.tasim.logtool.Main -handler tau.tac.adx.parser.GeneralHandler -file game.slg.gz -ucs -rating -bank -campaign -adnet
