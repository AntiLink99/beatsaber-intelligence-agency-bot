package bot.dto.supporters;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SupporterInfo {
    private long discordUserId;
    private String customSongsImage;
    private List<SupportType> supportTypes;

    public SupporterInfo(long discordUserId) {
        this.discordUserId = discordUserId;
        this.customSongsImage = "";
        this.supportTypes = new ArrayList<>();
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("discord_user_id", this.discordUserId)
                .append("custom_songs_image", this.customSongsImage)
                .append("support_types", supportTypes.stream().map(SupportType::name).collect(Collectors.toList()));
        return document;
    }

    public static SupporterInfo fromDocument(Document document) {
        List<String> supportTypesStr = document.getList("support_types", String.class);
        List<SupportType> supportTypes = supportTypesStr.stream().map(SupportType::valueOf).collect(Collectors.toList());

        SupporterInfo info = new SupporterInfo(document.getLong("discord_user_id"));
        info.setCustomSongsImage(document.getString("custom_songs_image"));
        info.setSupportTypes(supportTypes);
        return info;
    }

    public long getDiscordUserId() {
        return discordUserId;
    }

    public void setDiscordUserId(long discordUserId) {
        this.discordUserId = discordUserId;
    }

    public String getCustomSongsImage() {
        return customSongsImage;
    }

    public void setCustomSongsImage(String customSongsImage) {
        this.customSongsImage = customSongsImage;
    }

    public List<SupportType> getSupportTypes() {
        return supportTypes;
    }

    public void setSupportTypes(List<SupportType> supportTypes) {
        this.supportTypes = supportTypes;
    }
}
