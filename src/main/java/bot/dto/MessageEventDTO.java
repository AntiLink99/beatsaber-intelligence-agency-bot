package bot.dto;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageEventDTO {

    private TextChannel channel;
    private Guild guild;
    private Message message;
    private long id;
    private Member author;
    private JDA jda;
    SlashCommandEvent slashCommandIfExists;
    MessageEventType type;

    public MessageEventDTO(MessageReceivedEvent event) {
        this.channel = event.getTextChannel();
        this.guild = event.getGuild();
        this.message = event.getMessage();
        this.author = event.getMember();
        this.id = event.getMessageIdLong();
        this.jda = event.getJDA();
        this.type = MessageEventType.TEXT;
    }

    public MessageEventDTO(SlashCommandEvent event) {
        this.channel = event.getTextChannel();
        this.guild = event.getGuild();
        this.id = event.getIdLong();
        this.author = event.getMember();
        this.jda = event.getJDA();
        this.slashCommandIfExists = event;
        this.type = MessageEventType.SLASH;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public void setChannel(TextChannel channel) {
        this.channel = channel;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Member getAuthor() {
        return author;
    }

    public void setAuthor(Member author) {
        this.author = author;
    }

    public JDA getJDA() {
        return jda;
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SlashCommandEvent getSlashCommandIfExists() {
        return slashCommandIfExists;
    }

    public void setSlashCommandIfExists(SlashCommandEvent slashCommandIfExists) {
        this.slashCommandIfExists = slashCommandIfExists;
    }

    public MessageEventType getType() {
        return type;
    }

    public void setType(MessageEventType type) {
        this.type = type;
    }
}
