package tau.tac.adx.agents.bob.plumbing;

import com.google.inject.Guice;
import com.google.inject.Injector;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import tau.tac.adx.agents.bob.AgentBob;

public class AgentProxy extends Agent {

	private AgentBob bob;

	public AgentProxy() {
		Injector injector = Guice.createInjector(new BobModule());
		bob = injector.getInstance(AgentBob.class);
	}
	
	public void sendMessageToServer(String receiver, Transportable content){
		sendMessage(receiver, content);
	}

	@Override
	protected void messageReceived(Message message) {
		bob.messageReceived(message, this);
	}

		

	@Override
	protected void simulationSetup() {
		bob.simulationSetup(getName());
	}

	@Override
	protected void simulationFinished() {
		bob.simulationFinished();
	}
}
