start java -cp "lib/*" tau.tac.adx.agentware.Main -config config/giza-aw-1.conf > giza.log
start java -cp "lib/*" tau.tac.adx.agentware.Main -config config/agent00-aw-1.conf > agent00.log 
start java -cp "lib/*" tau.tac.adx.agentware.Main -config config/bcm-aw-1.conf > bcm.log 
start java -cp "lib/*" tau.tac.adx.agentware.Main -config config/oos-aw-1.conf > oos.log 
start java -cp "lib/*" tau.tac.adx.agentware.Main -config config/ibm-aw-1.conf > ibm.log 
start java -cp "lib/*" se.sics.tasim.sim.Main > server.log