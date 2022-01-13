package bot.utils;

import bot.dto.player.Player;
import bot.listeners.EmbedButtonListener;
import bot.main.BotConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
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

    public static void sendMessage(String msg, MessageChannel channel) {
        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription(msg);
            builder.setColor(embedColor);
            channel.sendMessage(builder.build()).queue();
        } catch (Exception e) {
            System.out.println("Could not send message because of lacking permissions: " + e.getMessage());
        }
    }

    public static void sendMessageWithTitle(String msg, String title, String titleUrl, MessageChannel channel) {
        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription(msg);
            builder.setColor(embedColor);
            builder.setTitle(title, titleUrl);
            channel.sendMessage(builder.build()).queue();
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
        channel.sendMessage(builder.build()).queue();
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
        return channel.sendMessage(builder.build()).complete();
    }

    public static void sendImageEmbed(String imagePath, String title, MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(Format.bold(Format.underline(title)));
        builder.setColor(embedColor);
        builder.setImage(imagePath);
        channel.sendMessage(builder.build()).queue();
    }

    public static void sendImage(String imagePath, String title, TextChannel channel) {
        File imageFile = new File(title);
        try {
            FileUtils.copyURLToFile(new URL(imagePath), imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        channel.sendFile(imageFile, title + imagePath.substring(imagePath.length() - 4)).queue();
        imageFile.delete();
    }

    public static void sendImage(File image, String title, TextChannel channel) {
        channel.sendFile(image, title).queue();
        image.delete();
    }

    public static void sendPlainMessage(String message, TextChannel channel) {
        try {
            channel.sendMessage(message).queue();
        } catch (Exception e) {
            System.out.println("Could not send message because of lacking permissions.");
        }
    }

    public static void sendMilestoneMessage(Player player, TextChannel channel) {
        try {
            String roleName = BotConstants.topRolePrefix + ListValueUtils.findFoaaMilestoneForRank(player.getRank());
            List<Role> roles = channel.getGuild().getRolesByName(roleName, true);
            if (roles.size() <= 0) {
                System.out.println("Could not find role \"" + roleName + "\".");
                return;
            }

            String emoteLine = Emotes.getMessageWithMultipleEmotes();
            sendPlainMessage(emoteLine, channel);
            sendMessage(Format.bold(player.getName() + "'s milestone role was updated! " + roles.get(0).getAsMention()), channel);
            sendPlainMessage(emoteLine, channel);
            sendPlainMessage(Format.ping(String.valueOf(player.getDiscordUserId())), channel);
        } catch (Exception ignored) {

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
            channel.sendMessage(builder.build()).complete();
            return;
        }
        List<Button> initialButtons = new ArrayList<>();
        initialButtons.add(Button.secondary("nextPage", "Next Page"));

        Message reactionMessage = channel.sendMessage(builder.build())
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
        channel.editMessageById(messageId, builder.build()).queue();
    }

    public static void sendTempMessage(String msg, int deleteAfterSecs, MessageChannel channel) {
        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription(msg);
            builder.setColor(embedColor);
            Message message = channel.sendMessage(builder.build()).complete();

            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    message.delete().queue();
                }
            }, deleteAfterSecs * 1000L);
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

    public static void sendMessageWithImagesAndTexts(String msg, String title, String titleUrl, String imageUrl, String topImageUrl, String footerText, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(msg);
        builder.setColor(embedColor);
        builder.setAuthor(title, titleUrl, topImageUrl);
        builder.setImage(imageUrl);
        builder.setFooter(footerText);
        channel.sendMessage(builder.build()).queue();
    }

    public static void sendBsgRankMessage(String title, String desc, String titleUrl, Color color, String avatar, MessageChannel channel) {
        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(title, titleUrl);
            builder.setDescription(desc);
            builder.setColor(color);
            builder.setThumbnail(avatar);
            channel.sendMessage(builder.build()).queue();
        } catch (Exception e) {
            System.out.println("Could not send message because of lacking permissions: " + e.getMessage());
        }
    }
}
