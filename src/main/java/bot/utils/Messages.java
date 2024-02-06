package bot.utils;

import bot.dto.LeaderboardService;
import bot.dto.MessageEventDTO;
import bot.dto.MessageEventType;
import bot.dto.RecentSongData;
import bot.listeners.EmbedButtonListener;
import bot.main.BotConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Messages {

    public static final Color embedColor = Color.CYAN;

    public static void sendMessage(String msg, MessageEventDTO event) {
        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription(msg);
            builder.setColor(embedColor);

            if (event.getType() == MessageEventType.SLASH) {
                event.getSlashCommandIfExists().replyEmbeds(builder.build()).setEphemeral(true).queue();
            } else {
                event.getChannel().sendMessageEmbeds(builder.build()).queue();
            }

        } catch (Exception e) {
            System.out.println("Could not send message because of lacking permissions: " + e.getMessage());
        }
    }

    public static void sendMessageWithTitle(String msg, String title, String titleUrl, MessageEventDTO event) {
        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription(msg);
            builder.setColor(embedColor);
            builder.setTitle(title, titleUrl);

            if (event.getType() == MessageEventType.SLASH) {
                event.getSlashCommandIfExists().replyEmbeds(builder.build()).setEphemeral(true).queue();
            } else {
                event.getChannel().sendMessageEmbeds(builder.build()).queue();
            }

        } catch (Exception e) {
            System.out.println("Could not send message because of lacking permissions: " + e.getMessage());
        }
    }

    public static void sendMessageStringMapWithTitle(Map<String, String> values, String title, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        for (String key : values.keySet()) {
            builder.addField(key, values.get(key), false);
        }
        builder.setColor(embedColor);
        builder.setTitle(title);
        channel.sendMessageEmbeds(builder.build()).queue();
    }

    public static Message sendMessageStringMap(Map<String, String> values, String title, MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        for (String key : values.keySet()) {
            builder.addField(key, values.get(key), false);
        }
        builder.setColor(embedColor);
        if (title != null) {
            builder.setTitle(title);
        }
        return channel.sendMessageEmbeds(builder.build()).complete();
    }

    public static void sendImageEmbed(String imagePath, String title, MessageEventDTO event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(Format.bold(Format.underline(title)));
        builder.setColor(embedColor);
        builder.setImage(imagePath);
        if (event.getType() == MessageEventType.SLASH) {
            event.getSlashCommandIfExists().replyEmbeds(builder.build()).setEphemeral(true).queue();
        } else {
            event.getChannel().sendMessageEmbeds(builder.build()).queue();
        }
    }

    public static Message sendMemeEmbed(String imagePath, String title, String url, int upVotes, String subreddit, MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(Format.bold(Format.underline(title)), url);
        builder.setColor(embedColor);
        builder.setImage(imagePath);
        builder.setFooter(upVotes + " Upvotes");
        builder.setAuthor(subreddit, "https://reddit.com/r/" + subreddit);
        return channel.sendMessageEmbeds(builder.build()).complete();
    }

    public static void sendImage(String imagePath, String title, TextChannel channel) {
        File imageFile = new File(title);
        try {
            FileUtils.copyURLToFile(new URL(imagePath), imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        channel.sendFile(imageFile, title + imagePath.substring(imagePath.length() - 4)).complete();
        imageFile.delete();
    }

    public static void sendImage(File image, String title, MessageEventDTO event) {
        if (event.getType() == MessageEventType.SLASH) {
            event.getSlashCommandIfExists().reply("Image generated successfully! ヾ(⌐■_■)ノ♪").queue();
        }
        event.getChannel().sendFile(image, title).complete();
        image.delete();
    }

    public static void sendSongsImage(File image, String title, MessageEventDTO event) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.of(ButtonStyle.LINK, "https://anti.link/features", "Set Background Image 🎨"));

        event.getChannel().sendFile(image, title).setActionRows(ActionRow.of(buttons)).complete();
        image.delete();
    }

    public static void sendPlainMessage(String message, TextChannel channel) {
        try {
            channel.sendMessage(message).queue();
        } catch (Exception e) {
            System.out.println("Could not send message because of lacking permissions.");
        }
    }

    public static void sendMultiPageMessage(Map<String, String> values, String title, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        for (String key : values.keySet().stream().limit(BotConstants.entriesPerReactionPage).collect(Collectors.toCollection(LinkedHashSet::new))) {
            builder.addField(key, values.get(key), false);
        }
        builder.setColor(embedColor);
        builder.setTitle(title);
        builder.setFooter("Page 1");

        if (values.size() <= BotConstants.entriesPerReactionPage) {
            channel.sendMessageEmbeds(builder.build()).complete();
            return;
        }
        List<Button> initialButtons = new ArrayList<>();
        initialButtons.add(Button.secondary("nextPage", "Next Page"));

        Message reactionMessage = channel.sendMessageEmbeds(builder.build())
                .setActionRows(ActionRow.of(initialButtons))
                .complete();

        channel.getJDA().addEventListener(new EmbedButtonListener(reactionMessage, title, values));
    }

    public static void editMessageStringMap(long messageId, Map<String, String> values, String title, String footer, boolean isInline, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        for (String key : values.keySet()) {
            builder.addField(key, values.get(key), isInline);
        }
        builder.setColor(embedColor);
        builder.setTitle(title);
        builder.setFooter(footer);
        channel.editMessageEmbedsById(messageId, builder.build()).queue();
    }

    public static void sendTempMessage(String msg, int deleteAfterSecs, MessageChannel channel) {
        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription(msg);
            builder.setColor(embedColor);
            Message message = channel.sendMessageEmbeds(builder.build()).complete();

            if (message != null) {
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        message.delete().queue();
                    }
                }, deleteAfterSecs * 1000L);
            }
        } catch (Exception ignored) {

        }
    }

    public static void sendFile(File file, String fileName, MessageChannel channel) {
        channel.sendFile(file, fileName).queue();
    }

    public static void sendPrivateMessage(String msg, Member member) {
        PrivateChannel channel = member.getUser().openPrivateChannel().complete();
        channel.sendMessage(msg).queue();
    }

    public static void sendRecentSongMessage(RecentSongData data, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(data.getSongInfo());
        builder.setColor(embedColor);
        builder.setAuthor(data.getSongName(), data.getSongUrl(), data.getDiffImageUrl());
        builder.setImage(data.getCoverUrl());
        builder.setFooter(data.getFooterText());

        if (data.getOptionalReplayUrl() != null) {
            List<Button> buttons = new ArrayList<>();
            buttons.add(Button.of(ButtonStyle.LINK, data.getOptionalReplayUrl(), "🎬 Watch Replay"));
            channel.sendMessageEmbeds(builder.build()).setActionRows(ActionRow.of(buttons)).queue();
            return;
        }
        channel.sendMessageEmbeds(builder.build()).queue();
    }

    public static void sendBsgRankMessage(String title, String desc, String titleUrl, Color color, String avatar, MessageChannel channel) {
        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(title, titleUrl);
            builder.setDescription(desc);
            builder.setColor(color);
            builder.setThumbnail(avatar);
            channel.sendMessageEmbeds(builder.build()).queue();
        } catch (Exception e) {
            System.out.println("Could not send message because of lacking permissions: " + e.getMessage());
        }
    }

    public static long sendAskServiceMessage(MessageEventDTO event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Which service do you want to use?");
        builder.setColor(embedColor);

        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.of(ButtonStyle.PRIMARY, LeaderboardService.BEATLEADER.name(), "BeatLeader"));
        buttons.add(Button.of(ButtonStyle.DANGER, LeaderboardService.SCORESABER.name(), "ScoreSaber"));
        buttons.add(Button.of(ButtonStyle.SUCCESS, LeaderboardService.ACCSABER.name(), "AccSaber"));

        if (event.getType() == MessageEventType.SLASH) {
             return event.getSlashCommandIfExists().replyEmbeds(builder.build())
                     .setEphemeral(true)
                    .addActionRows(ActionRow.of(buttons))
                    .complete().getInteraction().getIdLong();
        }
        return event.getMessage().replyEmbeds(builder.build())
                    .setActionRows(ActionRow.of(buttons))
                    .complete().getIdLong();
    }

    public static void sendReminderMessage(MessageChannel channel) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setColor(new Color(255, 223, 186));
        embed.setTitle("Fuel the Code & Caffeine Machine! ☕");
        embed.setDescription("Like the bot? 🤖\nIt's actually powered by a blend of code and coffee. By supporting, you not only fuel my coffee addiction but also unlock exclusive features!\n\n" +
                "Lend a hand on [Ko-fi](https://ko-fi.com/antilink) ☕ or [Patreon](https://www.patreon.com/antilink) 🎁.");
        embed.setImage("https://anti.link/img/reminder/" + RandomUtils.getRandomItem(BotConstants.DONATE_REMINDERS));

        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.of(ButtonStyle.LINK, "https://ko-fi.com/antilink", "Support on Ko-fi ☕"));
        buttons.add(Button.of(ButtonStyle.LINK, "https://www.patreon.com/antilink", "Become a Patron 🎁"));

        channel.sendMessageEmbeds(embed.build()).setActionRows(ActionRow.of(buttons)).queue();

    }

}
