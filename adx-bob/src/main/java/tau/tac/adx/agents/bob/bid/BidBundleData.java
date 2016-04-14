package tau.tac.adx.agents.bob.bid;

import com.google.inject.Inject;

import java.util.Random;
import java.util.Set;

import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.campaign.CampaignData;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.agents.bob.sim.MarketSegmentProbability;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;

public class BidBundleData {
	private double avgPerImp;
	private double daysLeftFactor;
	private double campaignImpRatio;
	private double randomFactor;
	private double gameDayFactor;
	private double marketSegmentPopularityFactor;
	private double adInfofactor;

	public BidBundleData() {
	}

	public void setAvgPerImp(double avgPerImp) {
		this.avgPerImp = avgPerImp;
	}

	public double getAvgPerImp() {
		return this.avgPerImp;
	}

	public void setDaysLeftFactor(double factor) {
		this.daysLeftFactor = factor;
	}

	public double getDaysLeftFactor() {
		return this.daysLeftFactor;
	}

	public void setCampaignImpRatio(double campaignImpRatio) {
		this.campaignImpRatio = campaignImpRatio;
	}

	public double getCampaignImpRatio() {
		return this.campaignImpRatio;
	}

	public void setAdInfoFactor(double adInfofactor) {
		this.adInfofactor = adInfofactor;
	}

	public double getAdInfoFactor() {
		return this.adInfofactor;
	}

	// TODO- need to check the initialization in (segRatio > c)
	public void setMarketSegmentPopularity(double marketSegmentPopularityFactor) {
		this.marketSegmentPopularityFactor = marketSegmentPopularityFactor;
	}

	public double getMarketSegmentPopularity() {
		return this.marketSegmentPopularityFactor;
	}

	public void setRandomFactor(double randomFactor) {
		this.randomFactor = randomFactor;
	}

	public double getRandomFactor() {
		return this.randomFactor;
	}

	public void setGameDayFactor(int gameDayFactor) { // how many days passed since												// the beginning of the game
		this.gameDayFactor = gameDayFactor;
	}

	public double getGameDayFactor() {
		return this.gameDayFactor;
	}
}
