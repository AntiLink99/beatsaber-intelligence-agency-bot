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

import bot.foaa.dto.Player;

public class DatabaseManager {
	private Connection con;

	public void connectToDatabase(String database) {

		if (con == null) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DBConstants.DB_NAME + "?autoReconnect=true&serverTimezone=UTC&useUnicode=yes&characterEncoding=UTF-8", DBConstants.DB_USERNAME, DBConstants.DB_PASSWORD);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e);

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
		String stmtToUse = player.getPlayerId() == null ? DBConstants.DELETE_PLAYER_STMT_BY_NAME : DBConstants.DELETE_PLAYER_STMT_BY_ID;
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
}
