package tau.tac.adx.agents.bob.sim;

import com.google.inject.Inject;
import tau.tac.adx.agents.bob.learn.LearnManager;
import tau.tac.adx.agents.bob.ucs.UcsManager;

import java.io.IOException;

public class SimulationManager {

    private GameData gameData;
    private UcsManager ucsManager;
    private LearnManager learnManager;

    @Inject
    public SimulationManager(GameData gameData, UcsManager ucsManager, LearnManager learnManager) {
        this.gameData = gameData;
        this.ucsManager = ucsManager;
        this.learnManager = learnManager;
    }

    public void start() {
        gameData.setDay(0);
        gameData.setQualityScore(1.0);
    }

    public void end() {
        // TODO reset all game data
        gameData.campaignReports.clear();
        try {
            ucsManager.updateUcsConfig();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        learnManager.saveStorage();
    }
}
