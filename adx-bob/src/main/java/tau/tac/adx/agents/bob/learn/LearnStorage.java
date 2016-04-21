package tau.tac.adx.agents.bob.learn;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.sim.GameConsts;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

@Singleton
public class LearnStorage {

    private final Logger log = Logger.getLogger(LearnStorage.class.getName());

    private List<CampaignBidBundleHistory> campaignBidBundleHistories;
    private AdxBidBundle[] sentBundles;//on index i the bid for day i+
    private Map<Integer, Long> campaignOpportunityBids; //(campaign,bid)

    @Inject
    public LearnStorage() {
        this.sentBundles = new AdxBidBundle[GameConsts.GAME_LENGTH + 1];
        this.campaignOpportunityBids = new HashMap<Integer, Long>();
    }

    public void saveBundle(AdxBidBundle bundle, int dayBiddingFor) {
        sentBundles[dayBiddingFor - 1] = bundle;
    }

    public double getBaseBidFromBundle(int day, long campaignId) {
        AdxBidBundle bundle = sentBundles[day - 1];
        Optional<Double> bid = bundle.keys().stream()
                .filter(isBaseQuery())
                .filter(q -> bundle.getCampaignId(q) == campaignId)
                .map(bundle::getBid)
                .findFirst();
        if (!bid.isPresent()) {
            log.warning("could not find sent bid for campaign " + campaignId);
        }
        return bid.isPresent() ? bid.get() : 0;
    }

    private Predicate<AdxQuery> isBaseQuery() {
        return q -> q.getAdType() == AdType.text && q.getDevice() == Device.pc;
    }

    public void saveCampaignBid(int campaignId, long cmpBidMillis) {
        campaignOpportunityBids.put(campaignId, cmpBidMillis);
    }

    public long getCampaignBid(int campaignId) {
        return campaignOpportunityBids.get(campaignId);
    }

    public void addBidHistory(CampaignBidBundleHistory history) {
        campaignBidBundleHistories.add(history);
    }

    public List<CampaignBidBundleHistory> getCampaignBidBundleHistories() {
        return campaignBidBundleHistories;
    }

    public void setCampaignBidBundleHistories(List<CampaignBidBundleHistory> campaignBidBundleHistories) {
        this.campaignBidBundleHistories = campaignBidBundleHistories;
    }
}
