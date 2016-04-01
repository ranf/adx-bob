package tau.tac.adx.agents.bob.ucs;

import java.util.Random;

import tau.tac.adx.agents.bob.sim.GameData;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UcsManager {

	private GameData gameData;
	private Random random;

	@Inject
	public UcsManager(GameData gameData, Random random){
		this.gameData = gameData;
		this.random = random;
	}
	
	public double generateUcsBid(){
		/*
		 * Adjust ucs bid s.t. target level is achieved. Note: The bid for the
		 * user classification service is piggybacked
		 */		
		if (gameData.adNetworkDailyNotification != null) {
			double ucsLevel = gameData.adNetworkDailyNotification
					.getServiceLevel();
			gameData.ucsBid = 0.1 + random.nextDouble() / 10.0;
			System.out.println("Day " + gameData.day + ": ucs level reported: "
					+ ucsLevel);
		} else {
			System.out.println("Day " + gameData.day + ": Initial ucs bid is "
					+ gameData.ucsBid);
		}
		return gameData.ucsBid;
	}
}
