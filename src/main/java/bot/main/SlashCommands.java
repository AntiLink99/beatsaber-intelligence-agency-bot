package bot.main;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public class SlashCommands {

    public static List<CommandData> getCurrentCommands()  {

        final CommandData helpCommand = new CommandData("help", "List all currently available commands.");

        final CommandData inviteCommand = new CommandData("invite", "Shares the invite link for this bot. Feel free to invite it to other servers!");

        final CommandData chartCommand = new CommandData("chart", "Displays a chart with your rank change over the last couple of days.")
                .addOption(OptionType.USER, "member", "An other user than yourself.", false);

        final CommandData chartAllCommand = new CommandData("chartall", "Displays a chart with the rank changes of all players over the last couple of days.")
                .addOption(OptionType.USER, "member", "An other user than yourself.", false);

        final CommandData standCommand = new CommandData("stand", "Displays a radar chart of the skills the player set for himself.")
                .addOption(OptionType.USER, "member", "An other user than yourself.", false);

        final CommandData setSkillCommand = new CommandData("setskill", "Sets the skill value for the radar chart displayed with \"bs stand\".")
                .addOption(OptionType.STRING, "skill", "Skill name: accuracy, speed, reading, stamina", true)
                .addOption(OptionType.INTEGER, "value", "Your judgement value (1-10)", true);

        final CommandData improvementCommand = new CommandData("improvement", "Lists the rank difference between the last seven days for all players.");

        final CommandData playlistCommand = new CommandData("playlist", "Automatically creates a playlist with the given map keys and name.")
                .addOption(OptionType.STRING, "filename", "Playlist file name", false)
                .addOption(OptionType.STRING, "map_keys", "Map keys seperated by spaces", false);

        final CommandData playlistEmbedCommand = new CommandData("playlist_embed", "Creates a playlist just like \"playlist\" but shows an embed with maps after.")
                .addOption(OptionType.STRING, "filename", "Playlist file name", false)
                .addOption(OptionType.STRING, "map_keys", "Map keys seperated by spaces", false);

        final CommandData qualifiedCommand = new CommandData("qualified", "Automatically creates a playlist with the current qualified maps from ScoreSaber.");

        final CommandData rankedByLatestCommand = new CommandData("ranked_by_latest", "Automatically creates a playlist with the x latest ranked maps.")
                .addOption(OptionType.INTEGER, "amount", "Ranked maps amount", true);

        final CommandData rankedByStarsCommand = new CommandData("ranked_by_stars", "Automatically creates a playlist of ranked maps in the specified star range.")
                .addOption(OptionType.STRING, "min_stars", "Minimum star amount", true)
                .addOption(OptionType.STRING, "max_stars", "Maximum star amount", true);

        final CommandData registerCommand = new CommandData("register", "Registers a player that will be tracked and updated by the bot.")
                .addOption(OptionType.STRING, "scoresaber_url", "Your ScoreSaber URL", true);

        final CommandData unregisterCommand = new CommandData("unregister", "Removes a player from the database so that the account is not being updated anymore.");

        final CommandData recentSongCommand = new CommandData("recentsong", "Displays a player card for the recent score set on ScoreSaber.")
                .addOption(OptionType.INTEGER, "score_count", "The x-th recent score you set.", false)
                .addOption(OptionType.USER, "member", "An other user than yourself.", false);

        final CommandData recentSongsCommand = new CommandData("recentsongs", "Displays the recently set scores of a player.")
                .addOption(OptionType.INTEGER, "page_id", "The x-th recent page.", false)
                .addOption(OptionType.USER, "other_user", "An other user than yourself.", false);

        final CommandData topSongsCommand = new CommandData("topsongs", "Displays the best scores of a player.")
                .addOption(OptionType.INTEGER, "page_id", "The x-th recent page.", false)
                .addOption(OptionType.USER, "member", "An other user than yourself.", false);


        final CommandData globalRankCommand = new CommandData("globalrank", "Shows your global rank in comparison to the two players above and below you on ScoreSaber.")
                .addOption(OptionType.USER, "member", "An other user than yourself.", false);

        final CommandData localRankCommand = new CommandData("localrank", "Shows your global rank in comparison to the two players above and below you on ScoreSaber.")
                .addOption(OptionType.USER, "member", "An other user than yourself.", false);

        final CommandData dachRankCommand = new CommandData("dachrank", "Shows your DACH-rank in comparison to the two players above and below you on ScoreSaber.")
                .addOption(OptionType.USER, "member", "An other user than yourself.", false);

        final CommandData setGridImageCommand = new CommandData("setgridimage", "Sets a background image for the acc grid.");

        final CommandData randomMemeCommand = new CommandData("randommeme", "Displays a random meme.");

        final CommandData sealCommand = new CommandData("seal", "Cute seal.");

       return List.of(
               helpCommand,
               inviteCommand,
               chartCommand,
               chartAllCommand,
               improvementCommand,
               playlistCommand,
               playlistEmbedCommand,
               qualifiedCommand,
               rankedByLatestCommand,
               rankedByStarsCommand,
               registerCommand,
               unregisterCommand,
               recentSongCommand,
               recentSongsCommand,
               topSongsCommand,
               setGridImageCommand,
               standCommand,
               setSkillCommand,
               globalRankCommand,
               localRankCommand,
               dachRankCommand,
               randomMemeCommand,
               sealCommand
       );
    }
}
