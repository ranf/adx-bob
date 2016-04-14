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
		setGameDayFactor(gameData.getDay());
		setAdInfoFactor(campaign, query);
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

	public void setAdInfoFactor(CampaignData currCamp, AdxQuery currAdXQuery) {
		if (currAdXQuery.getDevice() == Device.pc) {
			if (currAdXQuery.getAdType() == AdType.text) {
				this.adInfofactor = 1.0D;
			} else if (currAdXQuery.getAdType() == AdType.video) {
				this.adInfofactor = currCamp.getVideoCoef();
			}
		} else if (currAdXQuery.getDevice() == Device.mobile) {
			if (currAdXQuery.getAdType() == AdType.text) {
				this.adInfofactor = currCamp.getMobileCoef();
			} else if (currAdXQuery.getAdType() == AdType.video) {
				this.adInfofactor = (currCamp.getMobileCoef() * currCamp.getVideoCoef());
			}
		}
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

	public void setGameDayFactor(int daysPassed) { // how many days passed since
													// the beginning of the game
		this.gameDayFactor = daysPassed;
	}

	public double getGameDayFactor() {
		return this.gameDayFactor;
	}

	public static double randDouble(double min, double max) {
		double random = new Random().nextDouble();
		double result = min + random * (max - min);

		return result;
	}
}
