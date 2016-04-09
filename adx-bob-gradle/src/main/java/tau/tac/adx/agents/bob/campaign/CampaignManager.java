package tau.tac.adx.agents.bob.campaign;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.ucs.UcsManager;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.AdNetBidMessage;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.CampaignReportKey;
import tau.tac.adx.report.demand.InitialCampaignMessage;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CampaignManager {

	private final Logger log = Logger.getLogger(CampaignManager.class.getName());
	
	private GameData gameData;
	private UcsManager ucsManager;
	private CampaignBidManager campaignBidManager;
	private CampaignStorage campaignStorage;

	@Inject
	public CampaignManager(GameData gameData, UcsManager ucsManager, CampaignBidManager campaignBidManager,
			CampaignStorage campaignStorage) {
		this.gameData = gameData;
		this.ucsManager = ucsManager;
		this.campaignBidManager = campaignBidManager;
		this.campaignStorage = campaignStorage;
	}

	/**
	 * On day 0, a campaign (the "initial campaign") is allocated to each
	 * competing agent. The campaign starts on day 1. The address of the
	 * server's AdxAgent (to which bid bundles are sent) and DemandAgent (to
	 * which bids regarding campaign opportunities may be sent in subsequent
	 * days) are also reported in the initial campaign message
	 */
	public void handleInitialCampaignMessage(InitialCampaignMessage campaignMessage) {
		log.info(campaignMessage.toString());

		gameData.setDemandAgentAddress(campaignMessage.getDemandAgentAddress());
		gameData.setAdxAgentAddress(campaignMessage.getAdxAgentAddress());

		CampaignData campaignData = new CampaignData(campaignMessage);
		campaignStorage.acknowledgeCampaign(campaignData);

		campaignData.setBudget(campaignMessage.getBudgetMillis() / 1000.0);
		genCampaignQueries(campaignData);

		/*
		 * The initial campaign is already allocated to our agent so we add it
		 * to our allocated-campaigns list.
		 */
		gameData.getMyCampaigns().put(campaignMessage.getId(), campaignData);
		
		log.info("Day " + gameData.getDay() + ": Allocated campaign - " + campaignData);
	}

	/**
	 * On day n ( > 0) a campaign opportunity is announced to the competing
	 * agents. The campaign starts on day n + 2 or later and the agents may send
	 * (on day n) related bids (attempting to win the campaign). The allocation
	 * (the winner) is announced to the competing agents during day n + 1.
	 */
	public AdNetBidMessage handleICampaignOpportunityMessage(CampaignOpportunityMessage com) {

		CampaignData pendingCampaign = new CampaignData(com);
		campaignStorage.acknowledgeCampaign(pendingCampaign);
		campaignStorage.pendingCampaign = pendingCampaign;
		System.out.println("Day " + gameData.getDay() + ": Campaign opportunity - " + pendingCampaign);

		long cmpBidMillis = campaignBidManager.generateCampaignBid(com);

		System.out.println("Day " + gameData.getDay() + ": Campaign total budget bid (millis): " + cmpBidMillis);

		double ucsBid = ucsManager.generateUcsBid();
		System.out.println("ucs bid is "+ ucsBid);

		/* Note: Campaign bid is in millis */
		return new AdNetBidMessage(ucsBid, pendingCampaign.getId(), cmpBidMillis);
	}

	/**
	 * Campaigns performance w.r.t. each allocated campaign
	 */
	public void handleCampaignReport(CampaignReport campaignReport) {

		gameData.campaignReports.add(campaignReport);

		/*
		 * for each campaign, the accumulated statistics from day 1 up to day
		 * n-1 are reported
		 */
		for (CampaignReportKey campaignKey : campaignReport.keys()) {
			int cmpId = campaignKey.getCampaignId();
			CampaignStats cstats = campaignReport.getCampaignReportEntry(campaignKey).getCampaignStats();
			gameData.getMyCampaigns().get(cmpId).setStats(cstats);

			System.out.println(
					"Day " + gameData.getDay() + ": Updating campaign " + cmpId + " stats: " + cstats.getTargetedImps()
							+ " tgtImps " + cstats.getOtherImps() + " nonTgtImps. Cost of imps is " + cstats.getCost());
		}
	}

	/**
	 * On day n ( > 0), the result of the UserClassificationService and Campaign
	 * auctions (for which the competing agents sent bids during day n -1) are
	 * reported. The reported Campaign starts in day n+1 or later and the user
	 * classification service level is applicable starting from day n+1.
	 */
	public void handleAdNetworkDailyNotification(AdNetworkDailyNotification notificationMessage) {

		gameData.adNetworkDailyNotification = notificationMessage;

		System.out.println(
				"Day " + gameData.getDay() + ": Daily notification for campaign " + notificationMessage.getCampaignId());

		String campaignAllocatedTo = " allocated to " + notificationMessage.getWinner();

		if ((campaignStorage.pendingCampaign.getId() == notificationMessage.getCampaignId())
				&& (notificationMessage.getCostMillis() != 0)) {

			/* add campaign to list of won campaigns */
			// TODO - fix duplicate with initial campaign creation
			campaignStorage.pendingCampaign.setBudget(notificationMessage.getCostMillis() / 1000.0);
			genCampaignQueries(campaignStorage.pendingCampaign);
			gameData.getMyCampaigns().put(campaignStorage.pendingCampaign.getId(), campaignStorage.pendingCampaign);

			campaignAllocatedTo = " WON at cost (Millis)" + notificationMessage.getCostMillis();
		}

		gameData.setQualityScore(notificationMessage.getQualityScore());

		System.out.println("Day " + gameData.getDay() + ": " + campaignAllocatedTo + ". UCS Level set to "
				+ notificationMessage.getServiceLevel() + " at price " + notificationMessage.getPrice()
				+ " Quality Score is: " + notificationMessage.getQualityScore());
		ucsManager.updateCurrentGameUcsBids(notificationMessage.getServiceLevel(),gameData.ucsBid);
	}

	private void genCampaignQueries(CampaignData campaignData) {
		Set<AdxQuery> campaignQueriesSet = new HashSet<AdxQuery>();
		for (String PublisherName : gameData.getPublisherNames()) {
			Set<MarketSegment> targetSegment = campaignData.getTargetSegment();
			campaignQueriesSet.add(new AdxQuery(PublisherName, targetSegment, Device.mobile, AdType.text));
			campaignQueriesSet.add(new AdxQuery(PublisherName, targetSegment, Device.mobile, AdType.video));
			campaignQueriesSet.add(new AdxQuery(PublisherName, targetSegment, Device.pc, AdType.text));
			campaignQueriesSet.add(new AdxQuery(PublisherName, targetSegment, Device.pc, AdType.video));
		}

		campaignData.setCampaignQueries(campaignQueriesSet.toArray(new AdxQuery[campaignQueriesSet.size()]));

	}
}