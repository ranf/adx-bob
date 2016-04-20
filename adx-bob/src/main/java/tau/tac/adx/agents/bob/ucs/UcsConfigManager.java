package tau.tac.adx.agents.bob.ucs;

import com.google.inject.Inject;
import tau.tac.adx.agents.bob.utils.PropertiesLoader;

import java.io.IOException;
import java.util.Properties;

/**
 * This class is used in order to store and load ucs bids and level from previous games.
 */
public class UcsConfigManager {

    private static final String UCS_CONF_PATH = "ucs.conf";
    private static final String LEVEL_PREFIX = "level";

    private PropertiesLoader propertiesLoader;

    @Inject
    public UcsConfigManager(PropertiesLoader propertiesLoader) {
        this.propertiesLoader = propertiesLoader;
    }

    public double[] getUcsBidsFromConf() throws IOException {
        double[] ucsBids = new double[8];
        Properties properties = new Properties();
        properties = propertiesLoader.getPropertiesFromResource(UCS_CONF_PATH);

        for (int i = 0; i < ucsBids.length; i++) {
            ucsBids[i] = Double.parseDouble(properties.getProperty(getLevelKey(i)));
        }
        return ucsBids;
    }

    public void setUcsBidsInConf(double[] ucsBids) throws IOException {
        Properties properties = new Properties();
        for (int i = 0; i < ucsBids.length; i++) {
            properties.setProperty(getLevelKey(i), String.valueOf(ucsBids[i]));
        }
        propertiesLoader.setPropertiesToResource(UCS_CONF_PATH, properties);
    }

    private String getLevelKey(int level) {
        return LEVEL_PREFIX + level;
    }
}
