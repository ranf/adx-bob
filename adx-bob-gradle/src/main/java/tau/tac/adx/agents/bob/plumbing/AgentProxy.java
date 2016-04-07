package tau.tac.adx.agents.bob.plumbing;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import tau.tac.adx.agents.bob.AgentBob;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;

public class AgentProxy extends Agent {
	
	private final Logger log = Logger.getLogger(AgentProxy.class.getName());

	private AgentBob bob;

	public AgentProxy() {
		Injector injector = Guice.createInjector(new BobModule());
		bob = injector.getInstance(AgentBob.class);
		MarketSegmentProbability marketSegmentProbability = injector
				.getInstance(MarketSegmentProbability.class);
		try {
			marketSegmentProbability.load();
		} catch (IOException e) {
			log.severe("could not load market segments: " + e.getMessage());
		}
	}

	public void sendMessageToServer(String receiver, Transportable content) {
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
