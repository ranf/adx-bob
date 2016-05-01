package tau.tac.adx.agents.bob.learn;

public class CampaignBidBundleHistory {
    private long campaignImpressions; /* How many impressions we need to get */
    private long impressionsPerDay;  /* reach impressions / campaign length */
    private double marketSegmentRatio;
    private long campaignBid;
    private int day;
    private double budget;
    private int id;
    private BidResult bidResult;

    public CampaignBidBundleHistory() {
    }

    public CampaignBidBundleHistory(long campaignImpressions, long impressionsPerDay, double marketSegmentRatio,
                                    long campaignBid, int day,double budget, int id, BidResult bidResult) {
        this.campaignImpressions = campaignImpressions;
        this.impressionsPerDay = impressionsPerDay;
        this.marketSegmentRatio = marketSegmentRatio;
        this.campaignBid = campaignBid;
        this.day = day;
        this.budget = budget;
        this.id = id;
        this.bidResult = bidResult;
    }

    public long getCampaignImpressions() {
        return campaignImpressions;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id;  }

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

    public double getBudget() { return budget;  }

    public void setBudget(double budgetPerImpression) { this.budget = budget; }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "CampaignBidBundleHistory{" +
                "campaignImpressions=" + campaignImpressions +
                ", impressionsPerDay=" + impressionsPerDay +
                ", marketSegmentRatio=" + marketSegmentRatio +
                ", campaignBid=" + campaignBid +
                ", day=" + day +
                ", bidResult=" + bidResult +
                '}';
    }
}
