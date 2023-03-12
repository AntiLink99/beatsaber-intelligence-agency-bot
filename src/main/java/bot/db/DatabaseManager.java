package bot.db;

import bot.dto.player.DataBasePlayer;
import bot.dto.player.PlayerSkills;
import bot.utils.DiscordLogger;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseManager {
    private Connection con;

    public void connectToDatabase() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String connectionUrl = "mysql://" + DBConstants.DB_HOST + ":" + DBConstants.DB_PORT + "/" + DBConstants.DB_DATABASE;
                System.out.println("*** " + connectionUrl);
                con = DriverManager.getConnection("jdbc:" + connectionUrl + "?autoReconnect=true&serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8", DBConstants.DB_USERNAME, DBConstants.DB_PASSWORD);
                System.out.println("*** Connected to database: " + con.getMetaData().getDatabaseProductName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (!con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public boolean savePlayer(DataBasePlayer player) {
        if (con == null) {
            return false;
        }
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DBConstants.INSERT_PLAYER_STMT);
            stmt.setString(1, player.getId());
            stmt.setString(2, player.getName());
            stmt.setString(3, player.getProfilePicture());
            stmt.setString(4, player.getBio());
            stmt.setInt(5, player.getRank());
            stmt.setInt(6, player.getCountryRank());
            stmt.setFloat(7, player.getPp());
            stmt.setString(8, player.getCountry());
            stmt.setLong(9, player.getDiscordUserId());
            stmt.setString(10, player.getHistories());
            boolean playerWasSaved = stmt.executeUpdate() == 1;
            stmt.close();
            return playerWasSaved;
        } catch (SQLIntegrityConstraintViolationException e) {
            try {
                if (stmt != null && !stmt.isClosed()) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            DiscordLogger.sendLogInChannel(ExceptionUtils.getStackTrace(e), DiscordLogger.DB);
            e.printStackTrace();
        }

        return false;
    }

    public boolean deletePlayerByDiscordUserId(long userId) {
        if (con == null) {
            return false;
        }
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DBConstants.DELETE_PLAYER_BY_DISCORD_ID);
            stmt.setLong(1, userId);
            boolean playerWasDeleted = stmt.executeUpdate() == 1;
            stmt.close();
            return playerWasDeleted;
        } catch (SQLException e) {
            handleSQLException(e, stmt);
        }
        return false;
    }

    public void updatePlayer(DataBasePlayer newPlayer) {
        if (con == null) {
            return;
        }
        String stmtToUse = DBConstants.UPDATE_PLAYER_BY_PLAYER_ID_STMT;
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(stmtToUse);
            stmt.setString(1, newPlayer.getId());
            stmt.setString(2, newPlayer.getName());
            stmt.setString(3, newPlayer.getProfilePicture());
            stmt.setString(4, newPlayer.getBio());
            stmt.setInt(5, newPlayer.getRank());
            stmt.setInt(6, newPlayer.getCountryRank());
            stmt.setFloat(7, newPlayer.getPp());
            stmt.setString(8, newPlayer.getCountry());
            stmt.setLong(9, newPlayer.getDiscordUserId());
            stmt.setString(10, newPlayer.getHistories());
            stmt.setString(11, newPlayer.getCustomAccGridImage());
            stmt.setString(12, newPlayer.getId()); // Always last!
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Failed for player with id: " + newPlayer.getId());
            handleSQLException(e, stmt);
        }
    }

    public List<DataBasePlayer> getAllStoredPlayers() {
        if (con == null) {
            return null;
        }
        List<DataBasePlayer> players = new ArrayList<>();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DBConstants.SELECT_PLAYER_STMT);
            ResultSet rs = stmt.executeQuery();
            while (rs != null && rs.next()) {
                DataBasePlayer player = new DataBasePlayer();
                player.setId(rs.getString("player_id"));
                player.setName(rs.getString("player_name"));
                player.setProfilePicture(rs.getString("player_avatar"));
                player.setBio(rs.getString("player_bio"));
                player.setRank(rs.getInt("player_rank"));
                player.setCountryRank(rs.getInt("player_country_rank"));
                player.setPp(rs.getFloat("player_pp"));
                player.setCountry(rs.getString("player_country"));
                player.setDiscordUserId(rs.getLong("discord_user_id"));
                player.setHistories(rs.getString("player_history"));
                if (player.getHistories() != null) {
                    player.setHistoryValues(Arrays.stream(player.getHistories().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
                }
                player.setCustomAccGridImage(rs.getString("user_customAccGridImage"));
                players.add(player);
            }
            stmt.close();
        } catch (SQLException e) {
            handleSQLException(e, stmt);
        }
        return players;
    }

    public DataBasePlayer getPlayerByName(String playerName) {
        if (con == null) {
            return null;
        }
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DBConstants.SELECT_PLAYER_BY_NAME_STMT);
            stmt.setString(1, playerName);

            ResultSet rs = stmt.executeQuery();
            if (rs != null && rs.next()) {
                DataBasePlayer player = new DataBasePlayer();
                player.setId(rs.getString("player_id"));
                player.setName(rs.getString("player_name"));
                player.setProfilePicture(rs.getString("player_avatar"));
                player.setBio(rs.getString("player_bio"));
                player.setRank(rs.getInt("player_rank"));
                player.setCountryRank(rs.getInt("player_country_rank"));
                player.setPp(rs.getFloat("player_pp"));
                player.setCountry(rs.getString("player_country"));
                player.setDiscordUserId(rs.getLong("discord_user_id"));
                player.setCustomAccGridImage(rs.getString("user_customAccGridImage"));
                stmt.close();
                return player;
            }
        } catch (SQLException e) {
            handleSQLException(e, stmt);
        }
        return null;
    }

    public DataBasePlayer getPlayerByDiscordId(long discordUserId) {
        if (con == null) {
            return null;
        }
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DBConstants.SELECT_PLAYER_BY_DISCORD_ID_STMT);
            stmt.setLong(1, discordUserId);

            ResultSet rs = stmt.executeQuery();
            if (rs != null && rs.next()) {
                DataBasePlayer player = new DataBasePlayer();
                player.setId(rs.getString("player_id"));
                player.setName(rs.getString("player_name"));
                player.setProfilePicture(rs.getString("player_avatar"));
                player.setBio(rs.getString("player_bio"));
                player.setRank(rs.getInt("player_rank"));
                player.setCountryRank(rs.getInt("player_country_rank"));
                player.setPp(rs.getFloat("player_pp"));
                player.setCountry(rs.getString("player_country"));
                player.setHistories(rs.getString("player_history"));
                if (player.getHistories() != null) {
                    player.setHistoryValues(Arrays.stream(player.getHistories().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
                }
                player.setDiscordUserId(rs.getLong("discord_user_id"));
                player.setCustomAccGridImage(rs.getString("user_customAccGridImage"));
                stmt.close();
                return player;
            }
        } catch (SQLException e) {
            handleSQLException(e, stmt);
        }
        return null;
    }

    public PlayerSkills getPlayerSkillsByDiscordId(long discordId) {
        if (con == null) {
            return null;
        }
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DBConstants.SELECT_SKILLS_BY_DISCORD_ID);
            stmt.setLong(1, discordId);

            ResultSet rs = stmt.executeQuery();
            if (rs != null && rs.next()) {
                PlayerSkills skills = new PlayerSkills();
                skills.setAccuracy(rs.getInt("accuracy"));
                skills.setSpeed(rs.getInt("speed"));
                skills.setStamina(rs.getInt("stamina"));
                skills.setReading(rs.getInt("reading"));
                skills.setPlayerName(rs.getString("player_name"));
                stmt.close();
                return skills;
            }
        } catch (SQLException e) {
            handleSQLException(e, stmt);
        }
        return null;
    }

    public int setSkill(long idLong, String skill, int newValue) {
        if (con == null) {
            return -1;
        }
        String stmtToUse;
        PreparedStatement selectStmt;
        try {
            selectStmt = con.prepareStatement(DBConstants.SELECT_SKILLS_BY_DISCORD_ID);
            selectStmt.setLong(1, idLong);
            ResultSet rs = selectStmt.executeQuery();
            stmtToUse = rs != null && rs.next() ? DBConstants.getUpdateSkillStatement(skill) : DBConstants.getInsertSkillStatement(skill);
            selectStmt.close();
        } catch (SQLException e) {
            return -1;
        }

        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(stmtToUse);
            stmt.setInt(1, newValue);
            stmt.setLong(2, idLong);
            if (stmtToUse.equals(DBConstants.getInsertSkillStatement(skill))) {
                stmt.setString(3, getPlayerByDiscordId(idLong).getName());
            }
            int updatedRows = stmt.executeUpdate();
            stmt.close();
            return updatedRows;
        } catch (SQLException e) {
            handleSQLException(e, stmt);
        }
        return -1;
    }

    private void handleSQLException(SQLException e, PreparedStatement stmt) {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        DiscordLogger.sendLogInChannel(ExceptionUtils.getStackTrace(e), DiscordLogger.DB);
        e.printStackTrace();
    }
}
