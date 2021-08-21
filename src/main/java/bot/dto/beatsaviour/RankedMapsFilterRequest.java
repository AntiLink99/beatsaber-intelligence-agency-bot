package bot.dto.beatsaviour;

public class RankedMapsFilterRequest {
    int page;
    int minStar;
    int maxStar;
    String sortBy;
    String search;

    public RankedMapsFilterRequest(int minStar, int maxStar, String sortBy, String search) {
        this.minStar = minStar;
        this.maxStar = maxStar;
        this.sortBy = sortBy;
        this.search = search;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getMinStar() {
        return minStar;
    }

    public void setMinStar(int minStar) {
        this.minStar = minStar;
    }

    public int getMaxStar() {
        return maxStar;
    }

    public void setMaxStar(int maxStar) {
        this.maxStar = maxStar;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
