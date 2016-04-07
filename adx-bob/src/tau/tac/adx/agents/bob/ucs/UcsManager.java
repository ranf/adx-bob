package tau.tac.adx.agents.bob.ucs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.plumbing.PropertiesLoader;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.agents.bob.utils.Utils;;

@Singleton
public class UcsManager {

	private GameData gameData;
	private MarketSegmentProbability marketSegmentProbability;
	private PropertiesLoader propertiesLoader;
	private double[] ucsBidsFromConfig;
	private ArrayList<ArrayList<Double>> currentGameUcsBids = new ArrayList<ArrayList<Double>>();
	private final String UCS_CONF_PATH="ucs.conf";

	@Inject
	public UcsManager(GameData gameData, Random random, MarketSegmentProbability marketSegmentProbability,
			PropertiesLoader propertiesLoader) {
		this.gameData = gameData;
		this.marketSegmentProbability = marketSegmentProbability;
		this.propertiesLoader = propertiesLoader;
		ucsBidsFromConfig = getUcsBidsFromConf();
		for(int i=0; i<8;i++){
			currentGameUcsBids.add(new ArrayList<Double>());
		}
	}

	public double generateUcsBid() {
		double ucs_level = 0;
		int dayInGame = gameData.day;
		int totalNumberOfRemainingImpression = getTotalNumberOfRemainingImpression(dayInGame); 
		boolean isMarketSegmentPercentageLow = isMarketSegmentPercentageLow();
		if (!(totalNumberOfRemainingImpression == 0)) {
			ucs_level= 0.8;
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
		System.out.println("Day " + gameData.day + ": ucs level reported: " + ucs_level);
		System.out.println("Day " + gameData.day + ": Initial ucs bid is " + gameData.ucsBid);
		return gameData.ucsBid;
	}

	// TODO need to check that we are always able to convert i to int
	public void updateCurrentGameUcsBids(double level, double bid) {
		int i = (int) (Utils.logb(level, 0.9)+0.01);
		currentGameUcsBids.get(i).add(bid);
	}

	private int getTotalNumberOfRemainingImpression(int day) {
			int impCount = 0;
			for (CampaignData campaignData : gameData.myCampaigns.values()) {
				if ((campaignData.getDayStart() <= day + 1 ) && (campaignData.getDayEnd() > day)) {
					impCount += campaignData.impsTogo();
				}
			}
			return impCount;
		}
	

	private boolean isMarketSegmentPercentageLow() {
		for (CampaignData campaignData : gameData.myCampaigns.values()) {
			if (marketSegmentProbability.getMarketSegmentsRatio(campaignData.getTargetSegment()) <= 0.2) {
				return true;
			}
		}
		return false;
	}

	private double calculateBidFromLevel(double ucs_level) {
		double avg = 0;
		if (0.95 <= ucs_level) {
			avg = getOptimumWeightedAvgFromAllGames(0);
		}
		if (0.9 < ucs_level && ucs_level < 0.95) {
			avg = getOptimumWeightedAvgFromAllGames(1);
		}
		if (0.85 < ucs_level && ucs_level <= 0.9) {
			avg = getOptimumWeightedAvgFromAllGames(2);
		}
		if (0.80 < ucs_level && ucs_level <= 0.85) {
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
			for (int i = 0; i < size - 1 ; i++) {
				avg = avg + bidsList.get(i);
			}
			avg = avg / (size - 1);
			avg = (avg + bidsList.get(size-1)) / 2;
		} else {
			avg = bidsList.get(size-1);
		}

		return avg;

	}

	private double[] getUcsBidsFromConf() {
		double[] ucs_bids = new double[8];
		Properties properties = new Properties();
		try {
			properties = propertiesLoader.getPropertiesFromResource(UCS_CONF_PATH);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i<ucs_bids.length; i++) {
			ucs_bids[i] = Double.parseDouble(properties.getProperty("level" + i));
		}
		return ucs_bids;
	}

	public void setUcsBidsInConf() throws IOException {
		Properties properties = new Properties();		
		double bidValue;
		for (int i = 0; i < ucsBidsFromConfig.length; i++) {
			if(!currentGameUcsBids.get(i).isEmpty()){
				bidValue = 0.8*ucsBidsFromConfig[i] +  0.2*listAvg(currentGameUcsBids.get(i));
			}
			else{
				bidValue =ucsBidsFromConfig[i];
			}
			properties.setProperty("level" + i, String.valueOf(bidValue));
		}
		propertiesLoader.setPropertiesToResource(UCS_CONF_PATH, properties);
		// .setPropertiesToResource(ucsConfPath, properties);
	}
	
	private double listAvg(List<Double> bidsList){
		double avg = 0;
		for (int i = 0; i < bidsList.size() ; i++) {
			avg = avg + bidsList.get(i);
		}
		return avg/ bidsList.size();
	}

}
