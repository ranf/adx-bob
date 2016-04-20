package tau.tac.adx.agents.bob.learn;

public class CampaignBidBundleHistory {
    private long campaignImpressions;
    private long impressionsPerDay;
    private double marketSegmentRatio;
    private long campaignBid;
    private int day;
    private BidResult bidResult;

    public CampaignBidBundleHistory(long campaignImpressions, long impressionsPerDay, double marketSegmentRatio,
                                    long campaignBid, int day, BidResult bidResult) {
        this.campaignImpressions = campaignImpressions;
        this.impressionsPerDay = impressionsPerDay;
        this.marketSegmentRatio = marketSegmentRatio;
        this.campaignBid = campaignBid;
        this.day = day;
        this.bidResult = bidResult;
    }

    public long getCampaignImpressions() {
        return campaignImpressions;
    }

    public void setCampaignImpressions(long campaignImpressions) {
        this.campaignImpressions = campaignImpressions;
    }

    public double getImpressionsPerDay() {
        return impressionsPerDay;
    }

    public void setImpressionsPerDay(long impressionsPerDay) {
        this.impressionsPerDay = impressionsPerDay;
    }

    public double getMarketSegmentRatio() {
        return marketSegmentRatio;
    }

    public void setMarketSegmentRatio(double marketSegmentRatio) {
        this.marketSegmentRatio = marketSegmentRatio;
    }

    public long getCampaignBid() {
        return campaignBid;
    }

    public void setCampaignBid(long campaignBid) {
        this.campaignBid = campaignBid;
    }

    public BidResult getBidResults() {
        return bidResult;
    }

    public void setBidResults(BidResult bidResult) {
        this.bidResult = bidResult;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
