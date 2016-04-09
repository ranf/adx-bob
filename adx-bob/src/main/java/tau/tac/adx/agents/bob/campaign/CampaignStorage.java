package tau.tac.adx.agents.bob.campaign;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.adn.MarketSegment;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CampaignStorage {

	private CampaignData pendingCampaign;
	private List<CampaignData> allKnownCampaigns;
	private List<CampaignData> myCampaigns;// TODO remove, add filter to
											// allKnown

	@Inject
	public CampaignStorage() {
		allKnownCampaigns = new ArrayList<CampaignData>();
		myCampaigns = new ArrayList<CampaignData>();
	}

	public void acknowledgeCampaign(CampaignData campaign) {
		allKnownCampaigns.add(campaign);
	}

	public void addMyCampaign(CampaignData campaign) {
		myCampaigns.add(campaign);
	}

	public void setCamapginStats(long campaignId, CampaignStats stats) {
		myCampaigns.stream().filter(c -> c.getId() == campaignId).forEach(c -> c.setStats(stats));
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
			double segmentsFactor = sharedSegments.isEmpty() ? 1 : 1.2;

			count += sharedDays * (campaign.getReachImpsPerDay() + otherCampaign.getReachImpsPerDay()) * segmentsFactor
					/ 2;
		}
		return count;
	}

	public long totalActiveCampaignsImpsCount(int effectiveDay) {
		return allKnownCampaigns.stream().filter(activeCampaignFilter(effectiveDay))
				.mapToLong(c -> c.getReachImpsPerDay()).sum();
	}

	public List<CampaignData> getMyActiveCampaigns(int effectiveDay) {
		List<CampaignData> result = myCampaigns.stream().filter(activeCampaignFilter(effectiveDay))
				// TODO add isActive property to campaign data
				.collect(Collectors.toList());
		System.out.println("my campaigns " + result.size());
		return result;
	}

	public List<CampaignData> getAllActiveCampaigns(int effectiveDay) {
		List<CampaignData> result = allKnownCampaigns.stream().filter(activeCampaignFilter(effectiveDay))
				// TODO add isActive property to campaign data
				.collect(Collectors.toList());
		System.out.println("all campaigns " + result.size());
		return result;
	}

	private static Predicate<CampaignData> activeCampaignFilter(int day) {
		return c -> c.getDayStart() <= day && c.getDayEnd() >= day;
	}

	public CampaignData getPendingCampaign() {
		return pendingCampaign;
	}

	public void setPendingCampaign(CampaignData pendingCampaign) {
		this.pendingCampaign = pendingCampaign;
	}
}
