package tau.tac.adx.agents.bob.ucs;

import com.google.inject.Inject;
import tau.tac.adx.agents.bob.utils.PropertiesLoader;

import java.io.IOException;
import java.util.Properties;

/**
 * This class is used in order to store and load ucs bids and level from previous games.
 */
public class UcsConfigManager {

    private static final String UCS_CONF_PATH = "config/ucs.conf";
    private static final String LEVEL_PREFIX = "level";

    private PropertiesLoader propertiesLoader;

    @Inject
    public UcsConfigManager(PropertiesLoader propertiesLoader) {
        this.propertiesLoader = propertiesLoader;
    }

    /*Load ucs data from ucs config*/
    public double[] getUcsBidsFromConf() throws IOException {
        double[] ucsBids = new double[8];
        Properties properties = new Properties();
        properties = propertiesLoader.getPropertiesFromFile(UCS_CONF_PATH);

        for (int i = 0; i < ucsBids.length; i++) {
            ucsBids[i] = Double.parseDouble(properties.getProperty(getLevelKey(i)));
        }
        return ucsBids;
    }

    /*Store ucs data from this simulation in ucs config*/
    public void setUcsBidsInConf(double[] ucsBids) throws IOException {
        Properties properties = new Properties();
        for (int i = 0; i < ucsBids.length; i++) {
            properties.setProperty(getLevelKey(i), String.valueOf(ucsBids[i]));
        }
        propertiesLoader.setPropertiesToFile(UCS_CONF_PATH, properties);
    }

    private String getLevelKey(int level) {
        return LEVEL_PREFIX + level;
    }
}
