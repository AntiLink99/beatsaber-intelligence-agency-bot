package bot.db;

import org.apache.commons.lang3.StringUtils;

public class DBConstants {
	
	public static final String SELECT_PLAYER_STMT = "select * from player;";
	public static final String SELECT_PLAYER_BY_NAME_STMT = "select * from player where player_name = ?;";
	public static final String SELECT_PLAYER_ID_BY_DISCORD_ID_STMT = "select player_id from player where discord_user_id = ?;";
	public static final String SELECT_DISCORD_ID_BY_PLAYER_ID_STMT = "select discord_user_id from player where player_id = ?;";;
	public static final String DELETE_PLAYER_BY_ID_STMT = "delete from player where player_id = ?;";
	public static final String DELETE_PLAYER_BY_NAME_STMT = "delete from player where player_name = ?;";
	public static final String DELETE_PLAYER_BY_DISCORD_ID = "delete from player where discord_user_id = ?;";
	public static final String INSERT_PLAYER_STMT = buildInsertStatement("player","player_id","player_name","player_avatar","player_rank","player_country_rank","player_pp","player_country","discord_user_id","player_history");

	public static final String DB_HOST = System.getenv("db_hostname");
	public static final String DB_USERNAME = System.getenv("db_username");
	public static final String DB_PASSWORD = System.getenv("db_password");
	public static final String DB_NAME = System.getenv("db_name");
	
	
    public static String buildInsertStatement(String tableName, String... values) {
        String prefix = "INSERT INTO " + tableName + " (" + String.join(", ", values) + ") VALUES (";
        String parameters = StringUtils.removeEnd(StringUtils.repeat("?,", values.length), ",");
        String suffix = ");";
        return prefix + parameters + suffix;
    }
}
