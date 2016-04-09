package tau.tac.adx.agents.bob.campaign;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.report.adn.MarketSegment;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CampaignStorage {

	@Inject
	public CampaignStorage(GameData gameData/* TODO-remove dependency */) {
		this.gameData = gameData;
		allKnownCampaigns = new ArrayList<CampaignData>();
	}

	/**
	 * Information regarding the latest campaign opportunity announced
	 */
	public CampaignData pendingCampaign;

	private List<CampaignData> allKnownCampaigns;

	private GameData gameData;

	public void acknowledgeCampaign(CampaignData campaign) {
		allKnownCampaigns.add(campaign);
	}

	public long getOverlappingImps(CampaignData campaign) {
		// TODO - check if we can use java 8
		long count = 0;
		for (CampaignData otherCampaign : allKnownCampaigns) {
			if (otherCampaign.getId() == campaign.getId())
				continue;
			// TODO - check other campaign wasn't completed
			// TODO - extract method, check both inclusive
			long sharedDays = Math.max(0, Math.min(campaign.getDayEnd(), otherCampaign.getDayEnd())
					- Math.max(campaign.getDayStart(), otherCampaign.getDayStart()));

			Set<MarketSegment> sharedSegments = Sets.intersection(otherCampaign.getTargetSegment(),
					campaign.getTargetSegment());
			double segmentsFctor = sharedSegments.isEmpty() ? 1 : 1.2;

			count += sharedDays * (campaign.getReachImpsPerDay() + otherCampaign.getReachImpsPerDay()) * segmentsFctor
					/ 2;
		}
		return count;
	}

	public long totalActiveCampaignsImpsCount() {
		long count = 0;
		for (CampaignData campaign : allKnownCampaigns) {
			if (gameData.getMyCampaigns().containsKey(campaign.getId()))
				continue;
			if (campaign.getDayStart() >= gameData.getDay() && campaign.getDayEnd() <= gameData.getDay()) {
				// TODO add isActive property to CampaignData
				count++;
			}
		}
		return count;
	}

	public List<CampaignData> getMyActiveCampaigns() {
		List<CampaignData> result = new ArrayList<CampaignData>();
		for (CampaignData campaign : gameData.getMyCampaigns().values()) {
			if (campaign.getDayStart() <= gameData.getDay() && campaign.getDayEnd() >= gameData.getDay()) {
				// TODO add isActive property to CampaignData
				result.add(campaign);
			}
		}
		System.out.println("my campaigns " + result.size());
		return result;
	}

	public long getOtherAgentsActiveCampaigns() {
		List<CampaignData> result = new ArrayList<CampaignData>();
		for (CampaignData campaign : allKnownCampaigns) {
			if (campaign.getDayStart() <= gameData.getDay() && campaign.getDayEnd() >= gameData.getDay()) {
				// TODO add isActive property to CampaignData
				result.add(campaign);
			}
		}
		System.out.println("other campaigns " + result.size());
		return result.size();
	}
}
