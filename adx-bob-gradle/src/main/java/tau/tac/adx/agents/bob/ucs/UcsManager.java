package tau.tac.adx.agents.bob.ucs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.campaign.CampaignStorage;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.agents.bob.utils.Utils;;

@Singleton
public class UcsManager {

	private double[] ucsBidsFromConfig;
	private ArrayList<ArrayList<Double>> currentGameUcsBids = new ArrayList<ArrayList<Double>>();

	private GameData gameData;
	private MarketSegmentProbability marketSegmentProbability;
	private UcsConfigManager ucsConfigManager;
	private Random random; // TODO - use random
	private CampaignStorage campaignStorage;

	@Inject
	public UcsManager(GameData gameData, Random random, MarketSegmentProbability marketSegmentProbability,
			UcsConfigManager ucsConfigManager, CampaignStorage campaignStorage) {
		this.gameData = gameData;
		this.random = random;
		this.marketSegmentProbability = marketSegmentProbability;
		this.ucsConfigManager = ucsConfigManager;
		this.campaignStorage = campaignStorage;
	}

	public double generateUcsBid() {
		double ucs_level = 0;
		int dayInGame = gameData.getDay() + 1;
		int totalNumberOfRemainingImpression = getTotalNumberOfRemainingImpression(dayInGame);
		boolean isMarketSegmentPercentageLow = isMarketSegmentPercentageLow(dayInGame);
		if (!(totalNumberOfRemainingImpression == 0)) {
			ucs_level = 0.8;
			if (dayInGame <= 5) {
				ucs_level = 0.95;
			} else {
				if (totalNumberOfRemainingImpression > 10000) {
					ucs_level = ucs_level + 0.05;
				}
				if (isMarketSegmentPercentageLow) {
					ucs_level = ucs_level + 0.05;
				}
			}
		}
		gameData.ucsBid = calculateBidFromLevel(ucs_level);
		// /*
		// * Adjust ucs bid s.t. target level is achieved. Note: The bid for the
		// * user classification service is piggybacked
		// */
		// if (gameData.adNetworkDailyNotification != null) {
		// double ucsLevel = gameData.adNetworkDailyNotification
		// .getServiceLevel();
		// gameData.ucsBid = 0.1 + random.nextDouble() / 10.0;
		System.out.println("Day " + gameData.getDay() + ": ucs level reported: " + ucs_level);
		System.out.println("Day " + gameData.getDay() + ": Initial ucs bid is " + gameData.ucsBid);
		return gameData.ucsBid;
	}

	// TODO need to check that we are always able to convert i to int
	public void addToCurrentGameUcsBids(double level, double bid) {
		int i = (int) (Utils.logb(level, 0.9) + 0.01);
		currentGameUcsBids.get(i).add(bid);
	}

	private int getTotalNumberOfRemainingImpression(int day) {//TODO move to CampaignManager/Storage
		int impCount = 0;
		for (CampaignData campaignData : campaignStorage.getMyActiveCampaigns(day)) {
			if (campaignData.getDayStart() <= day && campaignData.getDayEnd() > day) {
				impCount += campaignData.impsTogo();
			}
		}
		return impCount;
	}

	private boolean isMarketSegmentPercentageLow(int day) {
		for (CampaignData campaignData : campaignStorage.getMyActiveCampaigns(day)) {
			if (marketSegmentProbability.getMarketSegmentsRatio(campaignData.getTargetSegment()) <= 0.2) {
				return true;
			}
		}
		return false;
	}

	private double calculateBidFromLevel(double ucsLevel) {
		double avg = 0;
		if (0.95 <= ucsLevel) {
			avg = getOptimumWeightedAvgFromAllGames(0);
		}
		if (0.9 < ucsLevel && ucsLevel < 0.95) {
			avg = getOptimumWeightedAvgFromAllGames(1);
		}
		if (0.85 < ucsLevel && ucsLevel <= 0.9) {
			avg = getOptimumWeightedAvgFromAllGames(2);
		}
		if (0.80 <= ucsLevel && ucsLevel <= 0.85) {
			avg = getOptimumWeightedAvgFromAllGames(3);
		}
		return avg;
	}

	private double getOptimumWeightedAvgFromAllGames(int levelIndex) {
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

	public void loadUcsConfig() throws IOException {
		ucsBidsFromConfig = ucsConfigManager.getUcsBidsFromConf();
		for (int i = 0; i < ucsBidsFromConfig.length; i++) {
			currentGameUcsBids.add(new ArrayList<Double>());
		}
	}

	public void updateUcsConfig() throws IOException {
		double[] updatedUcsBids = new double[ucsBidsFromConfig.length];
		for (int i = 0; i < ucsBidsFromConfig.length; i++) {
			double bidValue;
			if (!currentGameUcsBids.get(i).isEmpty()) {
				bidValue = 0.8 * ucsBidsFromConfig[i] + 0.2 * listAvg(currentGameUcsBids.get(i));
			} else {
				bidValue = ucsBidsFromConfig[i];
			}
			updatedUcsBids[i] = bidValue;
		}
		ucsConfigManager.setUcsBidsInConf(updatedUcsBids);
	}

	private double listAvg(List<Double> bidsList) {
		double avg = 0;
		for (int i = 0; i < bidsList.size(); i++) {
			avg = avg + bidsList.get(i);
		}
		return avg / bidsList.size();
	}

}
