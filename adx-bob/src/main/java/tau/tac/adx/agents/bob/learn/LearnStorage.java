package tau.tac.adx.agents.bob.learn;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.sim.GameConsts;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class LearnStorage {
    private List<CampaignBidBundleHistory> campaignBidBundleHistories;
    private AdxBidBundle[] sentBundles;//on index i the bid for day i+
    private Map<Integer, Long> campaignOpportunityBids; //(campaign,bid)

    @Inject
    public LearnStorage() {
        this.campaignBidBundleHistories = new ArrayList<CampaignBidBundleHistory>();
        this.sentBundles = new AdxBidBundle[GameConsts.GAME_LENGTH];
        this.campaignOpportunityBids = new HashMap<Integer, Long>();
    }

    public void saveBundle(AdxBidBundle bundle, int dayBiddingFor) {
        sentBundles[dayBiddingFor - 1] = bundle;
    }

    public double getBaseBidFromBundle(int day, long campaignId) {
        AdxBidBundle bundle = sentBundles[day - 1];
        return bundle.keys().stream()
                .filter(q -> q.getAdType() == AdType.text && q.getDevice() == Device.pc && bundle.getCampaignId(q) ==
                        campaignId)
                .map(q -> bundle.getBid(q))
                .findFirst().get();
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
}
