package tau.tac.adx.agents.bob.plumbing;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import tau.tac.adx.agents.bob.BaseTestCase;
import tau.tac.adx.agents.bob.learn.LearnManager;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.agents.bob.ucs.UcsManager;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class InitializerTest extends BaseTestCase {
    @InjectMocks
    private Initializer initializer;
    @Mock
    private MarketSegmentProbability marketSegmentProbability;
    @Mock
    private UcsManager ucsManager;
    @Mock
    private LearnManager learnManager;

    @Test
    public void testInitializer() {
        assertThat(initializer).isNotNull();
    }

    @Test
    public void testInitMarketSegmentProbability() throws IOException {
        initializer.init();
        verify(marketSegmentProbability).load();
    }

    @Test
    public void testInitUcsManager() throws IOException {
        initializer.init();
        verify(ucsManager).loadUcsConfig();
    }

    @Test
    public void testLearnManager() throws IOException {
        initializer.init();
        verify(learnManager).loadStorage();
    }
}
