package tau.tac.adx.agents.bob;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.umich.eecs.tac.props.BankStatus;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import tau.tac.adx.agents.bob.bid.BidManager;
import tau.tac.adx.agents.bob.campaign.CampaignManager;
import tau.tac.adx.agents.bob.learn.LearnManager;
import tau.tac.adx.agents.bob.plumbing.AgentProxy;
import tau.tac.adx.agents.bob.publisher.PublisherManager;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.SimulationManager;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.props.ReservePriceInfo;
import tau.tac.adx.report.adn.AdNetworkKey;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.adn.AdNetworkReportEntry;
import tau.tac.adx.report.demand.*;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;
import tau.tac.adx.report.publisher.AdxPublisherReport;

import java.util.logging.Logger;

@Singleton
public class AgentBob {

    private final Logger log = Logger.getLogger(AgentBob.class.getName());

    private GameData gameData;
    private CampaignManager campaignManager;
    private PublisherManager publisherManager;
    private BidManager bidManager;
    private SimulationManager simulationManager;
    private LearnManager learnManager;

    @Inject
    AgentBob(GameData gameData, CampaignManager campaignManager, PublisherManager publisherManager,
             BidManager bidManager, SimulationManager simulationManager, LearnManager learnManager) {
        this.gameData = gameData;
        this.campaignManager = campaignManager;
        this.publisherManager = publisherManager;
        this.bidManager = bidManager;
        this.simulationManager = simulationManager;
        this.learnManager = learnManager;
    }

    public void messageReceived(Message message, AgentProxy proxy) {
//		try {
        Transportable content = message.getContent();
        // TODO - consider moving traffic logic back to here, and only
        // forward relevant data
        // TODO - chronological order
        // TODO point each message to its section on the specification
        if (content instanceof InitialCampaignMessage) {
            campaignManager.handleInitialCampaignMessage((InitialCampaignMessage) content);
        } else if (content instanceof CampaignOpportunityMessage) {
            AdNetBidMessage bid = campaignManager
                    .handleICampaignOpportunityMessage((CampaignOpportunityMessage) content);
            proxy.sendMessageToServer(gameData.getDemandAgentAddress(), bid);
        } else if (content instanceof CampaignReport) {
            campaignManager.handleCampaignReport((CampaignReport) content);
        } else if (content instanceof AdNetworkDailyNotification) {
            campaignManager.handleAdNetworkDailyNotification((AdNetworkDailyNotification) content);
        } else if (content instanceof AdxPublisherReport) {
            publisherManager.handleAdxPublisherReport((AdxPublisherReport) content);
        } else if (content instanceof SimulationStatus) {
            handleSimulationStatus((SimulationStatus) content, proxy);
        } else if (content instanceof PublisherCatalog) {
            publisherManager.handlePublisherCatalog((PublisherCatalog) content);
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
//		} catch (NullPointerException e) {
//			this.log.log(Level.SEVERE, "Exception thrown while trying to parse message." + e);
//			return;
//		}

    }

    public void simulationSetup(String agentName) {
        simulationManager.start();
        log.fine("AdNet " + agentName + " simulationSetup");

    }

    public void simulationFinished() {
        simulationManager.end();
    }

    /**
     * The SimulationStatus message received on day n indicates that the
     * calculation time is up and the agent is requested to send its bid bundle
     * to the AdX.
     */
    private void handleSimulationStatus(SimulationStatus simulationStatus, AgentProxy proxy) {
        int day = gameData.getDay();
        System.out.println("Day " + day + " : Simulation Status Received");
        AdxBidBundle bid = bidManager.BuildBidAndAds();
        System.out.println("Day " + day + " ended. Starting next day");
        day++;
        gameData.setDay(day);
        if (bid != null) {
            System.out.println("Day " + day + ": Sending BidBundle");
            proxy.sendMessageToServer(gameData.getAdxAgentAddress(), bid);
        }
    }

    private void handleAdNetworkReport(AdNetworkReport adnetReport) {

        bidManager.addAdnetReport(adnetReport);
        int reportDay = gameData.getDay();
        learnManager.storeEndingCampaigns(reportDay - 1);

        log.fine("Day " + reportDay + " : AdNetworkReport");
        for (AdNetworkKey adnetKey : adnetReport.keys()) {
            AdNetworkReportEntry entry = adnetReport.getAdNetworkReportEntry(adnetKey);
            log.fine(adnetKey + " " + entry);
        }


    }

    /**
     * Processes the start information.
     *
     * @param startInfo the start information.
     */
    private void handleStartInfo(StartInfo startInfo) {
        gameData.startInfo = startInfo;
    }

    private void handleBankStatus(BankStatus content) {
        log.info("Day " + gameData.getDay() + " :" + content.toString());
    }

}
