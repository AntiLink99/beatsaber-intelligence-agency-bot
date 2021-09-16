package bot.dto.rankedmaps;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.stream.Collectors;

public class BeatSaverRankedMap {

	@SerializedName("qualified")
	private boolean qualified;

	@SerializedName("metadata")
	private Metadata metadata;

	@SerializedName("lastPublishedAt")
	private String lastPublishedAt;

	@SerializedName("description")
	private String description;

	@SerializedName("createdAt")
	private String createdAt;

	@SerializedName("automapper")
	private boolean automapper;

	@SerializedName("stats")
	private Stats stats;

	@SerializedName("versions")
	private List<VersionsItem> versions;

	@SerializedName("uploader")
	private Uploader uploader;

	@SerializedName("name")
	private String name;

	@SerializedName("uploaded")
	private String uploaded;

	@SerializedName("ranked")
	private boolean ranked;

	@SerializedName("id")
	private String id;

	@SerializedName("updatedAt")
	private String updatedAt;

	public void setQualified(boolean qualified){
		this.qualified = qualified;
	}

	public boolean isQualified(){
		return qualified;
	}

	public void setMetadata(Metadata metadata){
		this.metadata = metadata;
	}

	public Metadata getMetadata(){
		return metadata;
	}

	public void setLastPublishedAt(String lastPublishedAt){
		this.lastPublishedAt = lastPublishedAt;
	}

	public String getLastPublishedAt(){
		return lastPublishedAt;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setAutomapper(boolean automapper){
		this.automapper = automapper;
	}

	public boolean isAutomapper(){
		return automapper;
	}

	public void setStats(Stats stats){
		this.stats = stats;
	}

	public Stats getStats(){
		return stats;
	}

	public void setVersions(List<VersionsItem> versions){
		this.versions = versions;
	}

	public List<VersionsItem> getVersions(){
		return versions;
	}

	public void setUploader(Uploader uploader){
		this.uploader = uploader;
	}

	public Uploader getUploader(){
		return uploader;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setUploaded(String uploaded){
		this.uploaded = uploaded;
	}

	public String getUploaded(){
		return uploaded;
	}

	public void setRanked(boolean ranked){
		this.ranked = ranked;
	}

	public boolean isRanked(){
		return ranked;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public VersionsItem getLatestVersion() {
		return versions == null ? null : versions.stream().sorted().findFirst().orElse(null);
	}

	public DiffsItem getDiffByNameForLatestVersion(String name) {
		return getLatestVersion().getDiffs().stream().filter(diff -> name.equalsIgnoreCase(diff.getDifficulty())).findFirst().orElse(null);
	}

	public List<Float> getStars() {
		return getLatestVersion().getDiffs().stream().map(diff -> (float) diff.getStars()).collect(Collectors.toList());
	}
}