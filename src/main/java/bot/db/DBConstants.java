package bot.db;

import org.apache.commons.lang3.StringUtils;

public class DBConstants {

	public static final String SELECT_PLAYER_STMT = "select * from player;";
	public static final String SELECT_PLAYER_BY_NAME_STMT = "select * from player where player_name = ?;";
	public static final String SELECT_PLAYER_BY_DISCORD_ID_STMT = "select * from player where discord_user_id = ?;";
	public static final String SELECT_DISCORD_ID_BY_PLAYER_ID_STMT = "select discord_user_id from player where player_id = ?;";
	public static final String UPDATE_PLAYER_BY_PLAYER_ID_STMT = buildUpdateStatement("player","player_id", "player_id", "player_name", "player_avatar", "player_rank", "player_country_rank", "player_pp", "player_country", "discord_user_id", "player_history");
	public static final String DELETE_PLAYER_BY_ID_STMT = "delete from player where player_id = ?;";
	public static final String DELETE_PLAYER_BY_NAME_STMT = "delete from player where player_name = ?;";
	public static final String DELETE_PLAYER_BY_DISCORD_ID = "delete from player where discord_user_id = ?;";
	public static final String INSERT_PLAYER_STMT = buildInsertStatement("player", "player_id", "player_name", "player_avatar", "player_rank", "player_country_rank", "player_pp", "player_country", "discord_user_id", "player_history");
	public static final String SELECT_SKILLS_BY_DISCORD_ID = "select * from player_skills where discord_user_id = ?;";

	public static final String DB_HOST = System.getenv("db_host");
	public static final String DB_PORT = System.getenv("db_port");
	public static final String DB_DATABASE = System.getenv("db_database");
	public static final String DB_USERNAME = System.getenv("db_username");
	public static final String DB_PASSWORD = System.getenv("db_password");

	public static String buildInsertStatement(String tableName, String... values) {
		String prefix = "INSERT INTO " + tableName + " (" + String.join(", ", values) + ") VALUES (";
		String parameters = StringUtils.removeEnd(StringUtils.repeat("?,", values.length), ",");
		String suffix = ");";
		return prefix + parameters + suffix;
	}

	public static String buildUpdateStatement(String tableName, String whereValue, String... valuesToSet) {
		String params = "";
		for (int i = 0;i < valuesToSet.length;i++) {
			params += valuesToSet[i] + " = ?,";
		}
		params = StringUtils.removeEnd(params, ",");
		return "UPDATE " + tableName + " SET " + params + " WHERE " + whereValue + " = ?;";
	}

	public static String getUpdateSkillStatement(String skill) {
		return "update player_skills set " + skill + "=? where discord_user_id = ?;";
	}

	public static String getInsertSkillStatement(String skill) {
		return "insert into player_skills (" + skill + ", discord_user_id, player_name) values (?,?,?);";
	}
}
