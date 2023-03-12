package bot.dto.beatleader;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response{

	@SerializedName("patreoned")
	private boolean patreoned;

	@SerializedName("clan")
	private Clan clan;

	@SerializedName("playlists")
	private Object playlists;

	@SerializedName("clanRequest")
	private List<Object> clanRequest;

	@SerializedName("bannedClans")
	private List<Object> bannedClans;

	@SerializedName("login")
	private Object login;

	@SerializedName("migrated")
	private boolean migrated;

	@SerializedName("ban")
	private Object ban;

	@SerializedName("friends")
	private List<FriendsItem> friends;

	@SerializedName("player")
	private Player player;

	public boolean isPatreoned(){
		return patreoned;
	}

	public Clan getClan(){
		return clan;
	}

	public Object getPlaylists(){
		return playlists;
	}

	public List<Object> getClanRequest(){
		return clanRequest;
	}

	public List<Object> getBannedClans(){
		return bannedClans;
	}

	public Object getLogin(){
		return login;
	}

	public boolean isMigrated(){
		return migrated;
	}

	public Object getBan(){
		return ban;
	}

	public List<FriendsItem> getFriends(){
		return friends;
	}

	public Player getPlayer(){
		return player;
	}
}