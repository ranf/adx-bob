start java -cp "lib/*" se.sics.tasim.sim.Main
start java -cp "lib/*" tau.tac.adx.agentware.Main -config config/giza-aw-1.conf
start java -cp "lib/*" tau.tac.adx.agentware.Main -config config/agent00-aw-1.conf
start java -cp "lib/*" tau.tac.adx.agentware.Main -config config/bcm-aw-1.conf
::start java -cp "lib/*" tau.tac.adx.agentware.Main -config config/oos-aw-1.conf
::start java -cp "lib/*" tau.tac.adx.agentware.Main -config config/x-aw-1.conf
start java -cp "lib/*" tau.tac.adx.agentware.Main -config config/ibm-aw-1.conf
cd lib2\wiz
start java -cp "lib/*;." tau.tac.adx.agentware.Main -config config/aw-1.conf
cd ..\x
start java -cp "lib/*" tau.tac.adx.agentware.Main -config config/aw-1.conf