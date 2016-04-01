package tau.tac.adx.agents.bob.campaign;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tau.tac.adx.report.adn.MarketSegment;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CampaignStorage {

	@Inject
	public CampaignStorage() {
		allKnownCampaigns = new ArrayList<CampaignData>();
	}

	/**
	 * Information regarding the latest campaign opportunity announced
	 */
	public CampaignData pendingCampaign;

	private List<CampaignData> allKnownCampaigns;

	public void AcknowledgeCampaign(CampaignData campaign) {
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
			long sharedDays = Math.min(campaign.getDayEnd(),
					otherCampaign.getDayEnd())
					- Math.max(campaign.getDayStart(),
							otherCampaign.getDayStart());

			Set<MarketSegment> sharedSegments = Sets.intersection(
					otherCampaign.getTargetSegment(),
					campaign.getTargetSegment());

			if (sharedSegments.isEmpty() || sharedDays <= 0)
				continue;

			count += sharedDays
					* (campaign.getReachImpsPerDay() + otherCampaign
							.getReachImpsPerDay()) / 2;
		}
		return count;
	}
}
