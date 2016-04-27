package tau.tac.adx.agents.bob.publisher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.agents.bob.sim.GameData;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.props.PublisherCatalogEntry;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.publisher.AdxPublisherReport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class PublisherManager {

    private GameData gameData;

    @Inject
    public PublisherManager(GameData gameData) {
        this.gameData = gameData;
    }

    /**
     * Users and Publishers statistics: popularity and ad type orientation
     */
    public void handleAdxPublisherReport(AdxPublisherReport adxPublisherReport) {
        // TODO - not doing anything atm. find out what to do
        //TODO update market segment probabilities

//        System.out.println("Publishers Report: ");
//        for (PublisherCatalogEntry publisherKey : adxPublisherReport.keys()) {
//            AdxPublisherReportEntry entry = adxPublisherReport
//                    .getEntry(publisherKey);
//            System.out.println(entry.toString());
//        }
    }

    /**
     * Process the reported set of publishers
     *
     * @param publisherCatalog
     */
    public void handlePublisherCatalog(PublisherCatalog publisherCatalog) {
        gameData.setPublisherCatalog(publisherCatalog);
        generateAdxQuerySpace(publisherCatalog);
        getPublishersNames();
    }

    /**
     * A user visit to a publisher's web-site results in an impression
     * opportunity (a query) that is characterized by the the publisher, the
     * market segment the user may belongs to, the device used (mobile or
     * desktop) and the ad type (text or video).
     * <p>
     * An array of all possible queries is generated here, based on the
     * publisher names reported at game initialization in the publishers catalog
     * message
     */
    private void generateAdxQuerySpace(PublisherCatalog publisherCatalog) {
        if (publisherCatalog != null && gameData.queries == null) {
            Set<AdxQuery> querySet = new HashSet<>();

			/*
             * for each web site (publisher) we generate all possible variations
			 * of device type, ad type, and user market segment
			 */
            for (PublisherCatalogEntry publisherCatalogEntry : publisherCatalog) {
                String publishersName = publisherCatalogEntry
                        .getPublisherName();
                for (MarketSegment userSegment : MarketSegment.values()) {
                    Set<MarketSegment> singleMarketSegment = new HashSet<>();
                    singleMarketSegment.add(userSegment);

                    querySet.add(new AdxQuery(publishersName,
                            singleMarketSegment, Device.mobile, AdType.text));

                    querySet.add(new AdxQuery(publishersName,
                            singleMarketSegment, Device.pc, AdType.text));

                    querySet.add(new AdxQuery(publishersName,
                            singleMarketSegment, Device.mobile, AdType.video));

                    querySet.add(new AdxQuery(publishersName,
                            singleMarketSegment, Device.pc, AdType.video));

                }

                /**
                 * An empty segments set is used to indicate the "UNKNOWN"
                 * segment such queries are matched when the UCS fails to
                 * recover the user's segments.
                 */
                querySet.add(new AdxQuery(publishersName,
                        new HashSet<>(), Device.mobile,
                        AdType.video));
                querySet.add(new AdxQuery(publishersName,
                        new HashSet<>(), Device.mobile,
                        AdType.text));
                querySet.add(new AdxQuery(publishersName,
                        new HashSet<>(), Device.pc, AdType.video));
                querySet.add(new AdxQuery(publishersName,
                        new HashSet<>(), Device.pc, AdType.text));
            }
            gameData.queries = new AdxQuery[querySet.size()];
            querySet.toArray(gameData.queries);
        }
    }

    /*
     * genarates an array of the publishers names
     */
    private void getPublishersNames() {
        if (null == gameData.getPublisherNames() && gameData.getPublisherCatalog() != null) {
            ArrayList<String> names = new ArrayList<>();
            for (PublisherCatalogEntry pce : gameData.getPublisherCatalog()) {
                names.add(pce.getPublisherName());
            }

            gameData.setPublisherNames(new String[names.size()]);
            names.toArray(gameData.getPublisherNames());
        }
    }
}
