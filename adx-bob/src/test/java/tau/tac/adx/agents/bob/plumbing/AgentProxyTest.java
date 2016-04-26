package tau.tac.adx.agents.bob.plumbing;

import org.junit.Test;
import se.sics.tasim.aw.Message;
import tau.tac.adx.agents.bob.AgentBob;
import tau.tac.adx.agents.bob.learn.LearnManager;

import java.io.ByteArrayOutputStream;
import java.util.logging.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AgentProxyTest {

    @Test
    public void testAgentProxy() {
        Logger logger1 = Logger.getLogger(Initializer.class.getName());
        Logger logger2 = Logger.getLogger(LearnManager.class.getName());
        Formatter formatter = new SimpleFormatter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Handler handler = new StreamHandler(out, formatter);
        logger1.addHandler(handler);
        logger2.addHandler(handler);

        AgentProxy proxy = new AgentProxy();

        assertThat(proxy.bob).isNotNull();
        handler.flush();
        assertThat(out.toString()).isNullOrEmpty();
    }

    @Test
    public void testForwardMessagesToBob() {
        AgentProxy proxy = new AgentProxy();
        proxy.bob = mock(AgentBob.class);
        Message msg = mock(Message.class);

        proxy.messageReceived(msg);
        verify(proxy.bob).messageReceived(msg, proxy);

        //TODO error with getName()
        //proxy.simulationSetup();
        //verify(proxy.bob).simulationSetup(proxy.getName());

        proxy.simulationFinished();
        verify(proxy.bob).simulationFinished();
    }
}
