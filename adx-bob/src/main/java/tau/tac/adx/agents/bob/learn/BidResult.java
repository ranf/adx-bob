package tau.tac.adx.agents.bob.learn;

import tau.tac.adx.report.adn.AdNetworkReportEntry;

public class BidResult {
    private double bid;
    private AdNetworkReportEntry report;

    public BidResult() {
    }

    public BidResult(double bid, AdNetworkReportEntry report) {
        this.bid = bid;
        this.report = report;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public AdNetworkReportEntry getReport() {
        return report;
    }

    public void setReport(AdNetworkReportEntry report) {
        this.report = report;
    }

    @Override
    public String toString() {
        return "BidResult{" +
                "bid=" + bid +
                ", report=" + report +
                '}';
    }
}
