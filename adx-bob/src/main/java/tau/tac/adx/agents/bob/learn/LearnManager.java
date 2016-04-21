package tau.tac.adx.agents.bob.learn;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import tau.tac.adx.agents.bob.utils.FileSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LearnManager {

    private final Logger log = Logger.getLogger(LearnManager.class.getName());

    private final String BID_HISTORY_FILE = "history.json";

    private LearnStorage learnStorage;
    private FileSerializer fileSerializer;

    @Inject
    public LearnManager(LearnStorage learnStorage, FileSerializer fileSerializer) {
        this.learnStorage = learnStorage;
        this.fileSerializer = fileSerializer;
    }

    public void loadStorage() {
        try {
            Type listType = new TypeToken<List<CampaignBidBundleHistory>>() {
            }.getType();
            List<CampaignBidBundleHistory> previous = fileSerializer.deserialize(BID_HISTORY_FILE, listType);
            learnStorage.setCampaignBidBundleHistories(previous);
        } catch (Exception e) {
            log.severe("could not load history:" + e.getMessage());
            e.printStackTrace();
            learnStorage.setCampaignBidBundleHistories(new ArrayList<>());
        }
    }

    public void saveStorage() {
        List<CampaignBidBundleHistory> campaignBidBundleHistories = learnStorage.getCampaignBidBundleHistories();
        try {
            fileSerializer.serialize(campaignBidBundleHistories, BID_HISTORY_FILE);
        } catch (IOException e) {
            log.severe("could not save history:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
