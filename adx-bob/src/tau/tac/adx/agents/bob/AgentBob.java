package tau.tac.adx.agents.bob;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.umich.eecs.tac.props.BankStatus;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import tau.tac.adx.agents.bob.bid.BidManager;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.campaign.CampaignManager;
import tau.tac.adx.agents.bob.plumbing.AgentProxy;
import tau.tac.adx.agents.bob.publisher.PublisherManager;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.props.ReservePriceInfo;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.demand.AdNetBidMessage;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.InitialCampaignMessage;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;
import tau.tac.adx.report.publisher.AdxPublisherReport;

@Singleton
public class AgentBob {

	private final Logger log = Logger.getLogger(AgentBob.class.getName());

	private GameData gameData;

	private CampaignManager campaignManager;

	private PublisherManager publisherManager;

	private BidManager bidManager;

	private Random random;

	@Inject
	AgentBob(GameData gameData, CampaignManager campaignManager,
			PublisherManager publisherManager, BidManager bidManager,
			Random random) {
		this.gameData = gameData;
		this.campaignManager = campaignManager;
		this.publisherManager = publisherManager;
		this.bidManager = bidManager;
		this.random = random;
	}

	public void messageReceived(Message message, AgentProxy proxy) {
		try {
			Transportable content = message.getContent();
			// TODO - consider moving traffic logic back to here, and only
			// forward relevant data

			if (content instanceof InitialCampaignMessage) {
				campaignManager
						.handleInitialCampaignMessage((InitialCampaignMessage) content);
			} else if (content instanceof CampaignOpportunityMessage) {
				AdNetBidMessage bid = campaignManager
						.handleICampaignOpportunityMessage((CampaignOpportunityMessage) content);
				proxy.sendMessageToServer(gameData.demandAgentAddress, bid);
			} else if (content instanceof CampaignReport) {
				campaignManager.handleCampaignReport((CampaignReport) content);
			} else if (content instanceof AdNetworkDailyNotification) {
				campaignManager
						.handleAdNetworkDailyNotification((AdNetworkDailyNotification) content);
			} else if (content instanceof AdxPublisherReport) {
				publisherManager
						.handleAdxPublisherReport((AdxPublisherReport) content);
			} else if (content instanceof SimulationStatus) {
				handleSimulationStatus((SimulationStatus) content, proxy);
			} else if (content instanceof PublisherCatalog) {
				publisherManager
						.handlePublisherCatalog((PublisherCatalog) content);
			} else if (content instanceof AdNetworkReport) {
				handleAdNetworkReport((AdNetworkReport) content);
			} else if (content instanceof StartInfo) {
				handleStartInfo((StartInfo) content);
			} else if (content instanceof BankStatus) {
				handleBankStatus((BankStatus) content);
			} else if (content instanceof CampaignAuctionReport) {
				// obsolete - ignore
			} else if (content instanceof ReservePriceInfo) {
				// TODO - determine if it's interesting
				// ((ReservePriceInfo) content).getReservePriceType();
			} else {
				System.out.println("UNKNOWN Message Received: " + content);
			}
		} catch (NullPointerException e) {
			this.log.log(Level.SEVERE,
					"Exception thrown while trying to parse message." + e);
			return;
		}

	}

	public void simulationSetup(String agentName) {
		gameData.day = 0;
		gameData.bidBundle = new AdxBidBundle();
		/* initial bid between 0.1 and 0.2 */
		gameData.ucsBid = 0.1 + random.nextDouble() / 10.0;
		gameData.myCampaigns = new HashMap<Integer, CampaignData>();
		gameData.setQualityScore(1.0);
		log.fine("AdNet " + agentName + " simulationSetup");

	}

	public void simulationFinished() {
		// TODO reset all game data
		gameData.campaignReports.clear();
		gameData.bidBundle = null;
	}

	/**
	 * The SimulationStatus message received on day n indicates that the
	 * calculation time is up and the agent is requested to send its bid bundle
	 * to the AdX.
	 */
	private void handleSimulationStatus(SimulationStatus simulationStatus,
			AgentProxy proxy) {
		System.out.println("Day " + gameData.day
				+ " : Simulation Status Received");
		AdxBidBundle bid = bidManager.BuildBidAndAds();
		System.out.println("Day " + gameData.day + " ended. Starting next day");
		++gameData.day;// TODO - remove annoying day usage anywhere else
		if (bid != null) {
			System.out.println("Day " + gameData.day + ": Sending BidBundle");
			proxy.sendMessageToServer(gameData.adxAgentAddress, bid);
		}
	}

	/**
	 * 
	 * @param AdNetworkReport
	 */
	private void handleAdNetworkReport(AdNetworkReport adnetReport) {
		// TODO - find out if we need it
		System.out.println("Day " + gameData.day + " : AdNetworkReport");
		/*
		 * for (AdNetworkKey adnetKey : adnetReport.keys()) {
		 * 
		 * double rnd = Math.random(); if (rnd > 0.95) { AdNetworkReportEntry
		 * entry = adnetReport .getAdNetworkReportEntry(adnetKey);
		 * System.out.println(adnetKey + " " + entry); } }
		 */
	}

	/**
	 * Processes the start information.
	 * 
	 * @param startInfo
	 *            the start information.
	 */
	private void handleStartInfo(StartInfo startInfo) {
		gameData.startInfo = startInfo;
	}

	private void handleBankStatus(BankStatus content) {
		System.out.println("Day " + gameData.day + " :" + content.toString());
	}

}
