package tau.tac.adx.agents.bob.ucs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tau.tac.adx.agents.bob.campaign.CampaignStorage;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

;

@Singleton
public class UcsManager {

    private double[] ucsBidsFromConfig;
    private ArrayList<ArrayList<Double>> currentGameUcsBids = new ArrayList<>();

    private GameData gameData;
    private UcsConfigManager ucsConfigManager;
    private CampaignStorage campaignStorage;

    @Inject
    public UcsManager(GameData gameData, UcsConfigManager ucsConfigManager, CampaignStorage campaignStorage) {
        this.gameData = gameData;
        this.ucsConfigManager = ucsConfigManager;
        this.campaignStorage = campaignStorage;
    }

    /*generates ucs bid based on ucs level that is calculated from total number of remaining impression,day in game,
    market segment probability */
    public double generateUcsBid() {
        double ucs_level = 0;
        int dayInGame = gameData.getDay() + 1;
        int totalNumberOfRemainingImpression = campaignStorage.getTotalNumberOfRemainingImpression(dayInGame);
        boolean isMarketSegmentPercentageLow = campaignStorage.isMarketSegmentPercentageLow(dayInGame, 0.2);
        if (!(totalNumberOfRemainingImpression == 0)) {
            ucs_level = 0.8;
            if (dayInGame <= 5) {
                ucs_level = 0.95;
            } else {
                if (totalNumberOfRemainingImpression > 10000) {
                    ucs_level = ucs_level + 0.05;
                } else {
                    ucs_level = ucs_level - 0.05;
                }
                if (isMarketSegmentPercentageLow) {
                    ucs_level = ucs_level + 0.05;
                } else {
                    ucs_level = ucs_level - 0.05;
                }
            }
        }
        gameData.setUcsBid(calculateBidFromLevel(ucs_level) * Utils.randDouble(0.9, 1.1));
        System.out.println("Day " + gameData.getDay() + ": ucs level reported: " + ucs_level);
        System.out.println("Day " + gameData.getDay() + ": Initial ucs bid is " + gameData.getUcsBid());
        return gameData.getUcsBid();
    }

    /*updates list of ucs bid and level with current results*/
    public void addToCurrentGameUcsBids(double level, double bid) {
        int i = (int) (Utils.logb(level, 0.9) + 0.01);
        currentGameUcsBids.get(i).add(bid);
    }

    /*calculate the ucs bid that is adjusted with the ucs level based on data from previous bids*/
    private double calculateBidFromLevel(double ucsLevel) {
        double avg = 0;
        if (0.95 <= ucsLevel) {
            avg = getOptimumWeightedUcdBidAvgFromAllGames(0);
        }
        if (0.9 < ucsLevel && ucsLevel < 0.95) {
            avg = getOptimumWeightedUcdBidAvgFromAllGames(1);
        }
        if (0.85 < ucsLevel && ucsLevel <= 0.9) {
            avg = getOptimumWeightedUcdBidAvgFromAllGames(2);
        }
        if (0.80 <= ucsLevel && ucsLevel <= 0.85) {
            avg = getOptimumWeightedUcdBidAvgFromAllGames(3);
        }
        return avg;
    }

    /*calculate the optimum ucs bid weighted avg,based on previous results and data from the current simulation*/
    private double getOptimumWeightedUcdBidAvgFromAllGames(int levelIndex) {
        double avg = 0;
        if (!currentGameUcsBids.get(levelIndex).isEmpty()) {
            avg = getWeightedAvgFromCurrentGame(currentGameUcsBids.get(levelIndex));
            if (ucsBidsFromConfig[levelIndex] != 0) {
                avg = (0.9 * avg + 0.1 * ucsBidsFromConfig[levelIndex]);
            }
        } else {
            avg = ucsBidsFromConfig[levelIndex];
        }
        return avg;
    }

    /*calculate the ucs bid weighted avg,based on data from the current simulation*/
    private double getWeightedAvgFromCurrentGame(List<Double> bidsList) {
        int size = bidsList.size();
        double avg = 0;
        if (size > 1) {
            for (int i = 0; i < size - 1; i++) {
                avg = avg + bidsList.get(i);
            }
            avg = avg / (size - 1);
            avg = (avg + bidsList.get(size - 1)) / 2;
        } else {
            avg = bidsList.get(size - 1);
        }

        return avg;

    }

    /*Load ucs data from ucs config*/
    public void loadUcsConfig() throws IOException {
        ucsBidsFromConfig = ucsConfigManager.getUcsBidsFromConf();
        for (int i = 0; i < ucsBidsFromConfig.length; i++) {
            currentGameUcsBids.add(new ArrayList<>());
        }
    }

    /*Calculate ucs data from this simulation and store it in ucs config*/
    public void updateUcsConfig() throws IOException {
        double[] updatedUcsBids = new double[ucsBidsFromConfig.length];
        for (int i = 0; i < ucsBidsFromConfig.length; i++) {
            double bidValue;
            if (!currentGameUcsBids.get(i).isEmpty()) {
                bidValue = 0.8 * ucsBidsFromConfig[i] + 0.2 * Utils.listAvg(currentGameUcsBids.get(i));
            } else {
                bidValue = ucsBidsFromConfig[i];
            }
            updatedUcsBids[i] = bidValue;
        }
        ucsConfigManager.setUcsBidsInConf(updatedUcsBids);
    }

}
