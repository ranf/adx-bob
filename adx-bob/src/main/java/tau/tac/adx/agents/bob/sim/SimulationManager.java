package tau.tac.adx.agents.bob.sim;

import com.google.inject.Inject;
import tau.tac.adx.agents.bob.campaign.CampaignStorage;
import tau.tac.adx.agents.bob.learn.LearnManager;
import tau.tac.adx.agents.bob.ucs.UcsManager;

import java.io.IOException;
import java.util.logging.Logger;

public class SimulationManager {

    private final Logger log = Logger.getLogger(SimulationManager.class.getName());

    private GameData gameData;
    private UcsManager ucsManager;
    private LearnManager learnManager;
    private CampaignStorage campaignStorage;

    @Inject
    public SimulationManager(GameData gameData, UcsManager ucsManager, LearnManager learnManager, CampaignStorage
            campaignStorage) {
        this.gameData = gameData;
        this.ucsManager = ucsManager;
        this.learnManager = learnManager;
        this.campaignStorage = campaignStorage;
    }

    public void start() {
        gameData.setDay(0);
        gameData.setQualityScore(1.0);
    }

    public void end() {
        // TODO reset all game data
        gameData.campaignReports.clear();
        campaignStorage.reset();
        try {
            ucsManager.updateUcsConfig();
        } catch (IOException e) {
            log.severe("error updating ucs config - " + e.getMessage());
            e.printStackTrace();
        }
        learnManager.saveStorage();
    }
}
