package bot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import bot.dto.Player;
import bot.dto.PlayerSkills;

public class DatabaseManager {
	private Connection con;

	public void connectToDatabase() {
		try {
			if (con == null || con.isClosed()) {
				Class.forName("com.mysql.cj.jdbc.Driver");
				String connectionUrl = "mysql://" + DBConstants.DB_HOST + ":" + DBConstants.DB_PORT + "/" + DBConstants.DB_DATABASE;
				String herokuUrl = System.getenv("JAWSDB_URL");
				if (herokuUrl != null) {
					connectionUrl = herokuUrl;
				}
				con = DriverManager.getConnection("jdbc:" + connectionUrl + "?autoReconnect=true&serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8", DBConstants.DB_USERNAME, DBConstants.DB_PASSWORD);
				System.out.println("*** Connected to database: "+con.getMetaData().getDatabaseProductName());
			}
		} catch (Exception e) {
			System.out.println(e);
			try {
				if (!con.isClosed()) {
					con.close();
				}
			} catch (SQLException e1) {
				System.out.println(e1);
			}
		}
	}

	public boolean savePlayer(Player player) {
		try {
			PreparedStatement stmt = con.prepareStatement(DBConstants.INSERT_PLAYER_STMT);
			stmt.setString(1, player.getPlayerId());
			stmt.setString(2, player.getPlayerName());
			stmt.setString(3, player.getAvatar());
			stmt.setInt(4, player.getRank());
			stmt.setInt(5, player.getCountryRank());
			stmt.setFloat(6, player.getPp());
			stmt.setString(7, player.getCountry());
			stmt.setLong(8, player.getDiscordUserId());
			stmt.setString(9, player.getHistory());
			return stmt.executeUpdate() == 1;
		} catch (SQLIntegrityConstraintViolationException e) {
			// ok
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean deletePlayer(Player player) {
		String stmtToUse = player.getPlayerId() == null ? DBConstants.DELETE_PLAYER_BY_NAME_STMT : DBConstants.DELETE_PLAYER_BY_ID_STMT;
		String playerNameOrId = player.getPlayerId() == null ? player.getPlayerName() : player.getPlayerId();
		try {
			PreparedStatement stmt = con.prepareStatement(stmtToUse);
			stmt.setString(1, playerNameOrId);
			return stmt.executeUpdate() == 1;
		} catch (SQLIntegrityConstraintViolationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean deletePlayerByDiscordUserId(long userId) {
		try {
			PreparedStatement stmt = con.prepareStatement(DBConstants.DELETE_PLAYER_BY_DISCORD_ID);
			stmt.setLong(1, userId);
			return stmt.executeUpdate() == 1;
		} catch (SQLIntegrityConstraintViolationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<Player> getAllStoredPlayers() {
		List<Player> players = new ArrayList<Player>();
		try {
			PreparedStatement stmt = con.prepareStatement(DBConstants.SELECT_PLAYER_STMT);
			ResultSet rs = stmt.executeQuery();
			while (rs != null && rs.next()) {
				Player player = new Player();
				player.setPlayerId(rs.getString("player_id"));
				player.setPlayerName(rs.getString("player_name"));
				player.setAvatar(rs.getString("player_avatar"));
				player.setRank(rs.getInt("player_rank"));
				player.setCountryRank(rs.getInt("player_rank"));
				player.setPp(rs.getFloat("player_pp"));
				player.setCountry(rs.getString("player_country"));
				player.setDiscordUserId(rs.getLong("discord_user_id"));
				player.setHistory(rs.getString("player_history"));
				if (player.getHistory() != null) {
					player.setHistoryValues(Arrays.asList(player.getHistory().split(",")).stream().map(val -> Integer.parseInt(val)).collect(Collectors.toList()));
				}
				players.add(player);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return players;
	}

	public Player getPlayerByName(String playerName) {
		try {
			PreparedStatement stmt = con.prepareStatement(DBConstants.SELECT_PLAYER_BY_NAME_STMT);
			stmt.setString(1, playerName);

			ResultSet rs = stmt.executeQuery();
			if (rs != null && rs.next()) {
				Player player = new Player();
				player.setPlayerId(rs.getString("player_id"));
				player.setPlayerName(rs.getString("player_name"));
				player.setAvatar(rs.getString("player_avatar"));
				player.setRank(rs.getInt("player_rank"));
				player.setCountryRank(rs.getInt("player_rank"));
				player.setPp(rs.getFloat("player_pp"));
				player.setCountry(rs.getString("player_country"));
				player.setDiscordUserId(rs.getLong("discord_user_id"));
				return player;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Player getPlayerByDiscordId(long discordUserId) {
		try {
			PreparedStatement stmt = con.prepareStatement(DBConstants.SELECT_PLAYER_BY_DISCORD_ID_STMT);
			stmt.setLong(1, discordUserId);

			ResultSet rs = stmt.executeQuery();
			if (rs != null && rs.next()) {
				Player player = new Player();
				player.setPlayerId(rs.getString("player_id"));
				player.setPlayerName(rs.getString("player_name"));
				player.setAvatar(rs.getString("player_avatar"));
				player.setRank(rs.getInt("player_rank"));
				player.setCountryRank(rs.getInt("player_rank"));
				player.setPp(rs.getFloat("player_pp"));
				player.setCountry(rs.getString("player_country"));
				player.setDiscordUserId(rs.getLong("discord_user_id"));
				return player;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getDiscordIdByPlayerId(String playerId) {
		try {
			PreparedStatement stmt = con.prepareStatement(DBConstants.SELECT_DISCORD_ID_BY_PLAYER_ID_STMT);
			stmt.setString(1, playerId);

			ResultSet rs = stmt.executeQuery();
			if (rs != null && rs.next()) {
				return rs.getLong("discord_user_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public PlayerSkills getPlayerSkillsByDiscordId(long discordId) {
		try {
			PreparedStatement stmt = con.prepareStatement(DBConstants.SELECT_SKILLS_BY_DISCORD_ID);
			stmt.setLong(1, discordId);

			ResultSet rs = stmt.executeQuery();
			if (rs != null && rs.next()) {
				PlayerSkills skills = new PlayerSkills();
				skills.setAccuracy(rs.getInt("accuracy"));
				skills.setSpeed(rs.getInt("speed"));
				skills.setStamina(rs.getInt("stamina"));
				skills.setReading(rs.getInt("reading"));
				skills.setPlayerName(rs.getString("player_name"));
				return skills;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int setSkill(long idLong, String skill, int newValue) {
		String stmtToUse = null;
		PreparedStatement selectStmt;
		try {
			selectStmt = con.prepareStatement(DBConstants.SELECT_SKILLS_BY_DISCORD_ID);
			selectStmt.setLong(1, idLong);
			ResultSet rs = selectStmt.executeQuery();
			stmtToUse = rs != null && rs.next() ? DBConstants.getUpdateSkillStatement(skill) : DBConstants.getInsertSkillStatement(skill);
		} catch (SQLException e) {
			return -1;
		}
		if (stmtToUse != null) {
			try {
				PreparedStatement stmt = con.prepareStatement(stmtToUse);
				stmt.setInt(1, newValue);
				stmt.setLong(2, idLong);
				if (stmtToUse.equals(DBConstants.getInsertSkillStatement(skill))) {

					stmt.setString(3, getPlayerByDiscordId(idLong).getPlayerName());
				}
				return stmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}
}
