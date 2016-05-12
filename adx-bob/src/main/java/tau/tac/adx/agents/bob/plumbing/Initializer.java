package tau.tac.adx.agents.bob.plumbing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tau.tac.adx.agents.bob.learn.LearnManager;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.agents.bob.ucs.UcsManager;

import java.io.IOException;
import java.util.logging.Logger;

public class Initializer {

    private final Logger log = Logger.getLogger(Initializer.class.getName());

    private MarketSegmentProbability marketSegmentProbability;
    private UcsManager ucsManager;
    private LearnManager learnManager;

    @Inject
    public Initializer(MarketSegmentProbability marketSegmentProbability, UcsManager ucsManager, LearnManager
            learnManager) {
        this.marketSegmentProbability = marketSegmentProbability;
        this.ucsManager = ucsManager;
        this.learnManager = learnManager;
    }

    public void init() {
        try {
            marketSegmentProbability.load();
        } catch (IOException e) {
            log.severe("could not load market segments: " + e.getMessage());
        }
        try {
            ucsManager.loadUcsConfig();
        } catch (IOException e) {
            log.severe("could not load ucs config: " + e.getMessage());
        }
        learnManager.loadStorage();
    }
}
