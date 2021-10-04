package bot.api;

import bot.dto.patreon.PatreonSupporter;
import bot.dto.patreon.SupportType;
import com.patreon.PatreonAPI;
import com.patreon.resources.Campaign;
import com.patreon.resources.Pledge;
import com.patreon.resources.User;
import com.patreon.resources.shared.SocialConnections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Patreon {

    public List<PatreonSupporter> fetchPatreonSupporters() {
        String creatorAccessToken = System.getenv("creator_access_token");

        List<PatreonSupporter> supporters = new ArrayList<>();
        try {
            PatreonAPI apiClient = new PatreonAPI(creatorAccessToken);
            List<Campaign> campaigns = apiClient.fetchCampaigns().get();
            Campaign campaign = campaigns.get(0);
            if (campaign == null) {
                return null;
            }
            String campaignId = campaign.getId();

            List<Pledge> pledges = apiClient.fetchAllPledges(campaignId);
            for (Pledge pledge : pledges) {
                User patron = pledge.getPatron();
                SocialConnections.UserIdObject patronDiscord = patron.getSocialConnections().getDiscord();
                if (patronDiscord != null) {
                    String discordId = patronDiscord.getUser_id();
                    SupportType type;
                    switch (pledge.getReward().getTitle().toLowerCase()) {
                        case "small support":
                            type = SupportType.SMALL;
                            break;
                        case "medium support":
                            type = SupportType.MEDIUM;
                            break;
                        case "chad support":
                            type = SupportType.CHAD;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + pledge.getType());
                    }
                    supporters.add(new PatreonSupporter(Long.parseLong(discordId), type));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return supporters;
    }

    public boolean isPatreonSupporter(long discordId) {
        List<PatreonSupporter> supporters = fetchPatreonSupporters();
        if (supporters != null && supporters.size() > 0) {
            return supporters
                    .stream()
                    .map(PatreonSupporter::getDiscordId)
                    .anyMatch(id -> id == discordId);
        }
        return false;
    }
}
