package tau.tac.adx.agents.bob.sim;

import com.google.inject.Singleton;
import se.sics.tasim.props.StartInfo;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalog;

@Singleton
public class GameData {
    /*
     * Basic simulation information. An agent should receive the {@link
     * StartInfo} at the beginning of the game or during recovery.
     */
    public StartInfo startInfo;
    /*
     * we maintain a list of queries - each characterized by the web site (the
     * publisher), the device type, the ad type, and the user market segment
     */
    public AdxQuery[] queries;
    /*
     * The current bid level for the user classification service
     */
    private double ucsBid;
    /*
     * The targeted service level for the user classification service
     */
    public double ucsTargetLevel;
    private PublisherCatalog publisherCatalog;
    /*
     * The addresses of server entities to which the agent should send the daily
     * bids data
     */
    private String demandAgentAddress;//campaign+messages
    private String adxAgentAddress;//bid bundle
    /*
     * current day of simulation
     */
    private int day;

    private String[] publisherNames;
    private double qualityScore;

    public GameData() {
    }

    public double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(double qualityScore) {
        this.qualityScore = qualityScore;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDemandAgentAddress() {
        return demandAgentAddress;
    }

    public void setDemandAgentAddress(String demandAgentAddress) {
        this.demandAgentAddress = demandAgentAddress;
    }

    public String getAdxAgentAddress() {
        return adxAgentAddress;
    }

    public void setAdxAgentAddress(String adxAgentAddress) {
        this.adxAgentAddress = adxAgentAddress;
    }

    public String[] getPublisherNames() {
        return publisherNames;
    }

    public void setPublisherNames(String[] publisherNames) {
        this.publisherNames = publisherNames;
    }

    public PublisherCatalog getPublisherCatalog() {
        return publisherCatalog;
    }

    public void setPublisherCatalog(PublisherCatalog publisherCatalog) {
        this.publisherCatalog = publisherCatalog;
    }

    public double getUcsBid() {
        return ucsBid;
    }

    public void setUcsBid(double ucsBid) {
        this.ucsBid = ucsBid;
    }
}
