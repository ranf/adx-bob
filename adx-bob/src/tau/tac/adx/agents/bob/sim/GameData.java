package tau.tac.adx.agents.bob.sim;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.google.inject.Singleton;

import se.sics.tasim.props.StartInfo;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.InitialCampaignMessage;

@Singleton
public class GameData {
	//TODO - change all public fields to properties and extract some to separate classes
	//TODO - remove uninteresting state data only used locally
	
	/*
	 * Basic simulation information. An agent should receive the {@link
	 * StartInfo} at the beginning of the game or during recovery.
	 */
	public StartInfo startInfo;

	/**
	 * Messages received:
	 * 
	 * We keep all the {@link CampaignReport campaign reports} delivered to the
	 * agent. We also keep the initialization messages {@link PublisherCatalog}
	 * and {@link InitialCampaignMessage} and the most recent messages and
	 * reports {@link CampaignOpportunityMessage}, {@link CampaignReport}, and
	 * {@link AdNetworkDailyNotification}.
	 */
	public final Queue<CampaignReport> campaignReports;
	public PublisherCatalog publisherCatalog;
	public InitialCampaignMessage initialCampaignMessage;
	public AdNetworkDailyNotification adNetworkDailyNotification;

	/*
	 * The addresses of server entities to which the agent should send the daily
	 * bids data
	 */
	public String demandAgentAddress;
	public String adxAgentAddress;

	/*
	 * we maintain a list of queries - each characterized by the web site (the
	 * publisher), the device type, the ad type, and the user market segment
	 */
	public AdxQuery[] queries;

	/**
	 * Information regarding the latest campaign opportunity announced
	 */
	public CampaignData pendingCampaign;

	/**
	 * We maintain a collection (mapped by the campaign id) of the campaigns won
	 * by our agent.
	 */
	public Map<Integer, CampaignData> myCampaigns;

	/*
	 * the bidBundle to be sent daily to the AdX
	 */
	public AdxBidBundle bidBundle;

	/*
	 * The current bid level for the user classification service
	 */
	public double ucsBid;

	/*
	 * The targeted service level for the user classification service
	 */
	public double ucsTargetLevel;

	/*
	 * current day of simulation
	 */
	public int day;
	public String[] publisherNames;
	private CampaignData currCampaign;
	
	public GameData(){
		campaignReports = new LinkedList<CampaignReport>();
	}

	public CampaignData getCurrCampaign() {
		return currCampaign;
	}

	public void setCurrCampaign(CampaignData currCampaign) {
		this.currCampaign = currCampaign;
	}
}
