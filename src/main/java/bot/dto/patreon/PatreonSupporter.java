package bot.dto.patreon;

public class PatreonSupporter {

    long discordId;
    SupportType type;

    public PatreonSupporter(long discordId, SupportType type) {
        this.discordId = discordId;
        this.type = type;
    }

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
    }

    public SupportType getType() {
        return type;
    }

    public void setType(SupportType type) {
        this.type = type;
    }

}
