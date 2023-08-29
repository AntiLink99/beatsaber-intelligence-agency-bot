package bot.dto.supporters;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SupporterInfo {
    private String discordUserId;
    private String customSongsImage;
    private List<String> supportTypes;

    public SupporterInfo(String discordUserId) {
        this.discordUserId = discordUserId;
        this.customSongsImage = "";
        this.supportTypes = new ArrayList<>();
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("discord_user_id", this.discordUserId)
                .append("custom_songs_image", this.customSongsImage)
                .append("support_types", supportTypes.stream().collect(Collectors.toList()));
        return document;
    }

    public static SupporterInfo fromDocument(Document document) {
        List<String> supportTypes = document.getList("support_types", String.class);
        SupporterInfo info = new SupporterInfo(document.getString("discord_user_id"));
        info.setCustomSongsImage(document.getString("custom_songs_image"));
        info.setSupportTypes(supportTypes);
        return info;
    }

    public String getDiscordUserId() {
        return discordUserId;
    }

    public void setDiscordUserId(String discordUserId) {
        this.discordUserId = discordUserId;
    }

    public String getCustomSongsImage() {
        return customSongsImage;
    }

    public void setCustomSongsImage(String customSongsImage) {
        this.customSongsImage = customSongsImage;
    }

    public List<String> getSupportTypes() {
        return supportTypes;
    }

    public void setSupportTypes(List<String> supportTypes) {
        this.supportTypes = supportTypes;
    }
}
