package bot.scraping;

import bot.dto.leaderboards.LeaderboardEntry;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardScraper {

    public List<LeaderboardEntry> getLeaderboardEntriesFromDocument(Document doc) {
        List<LeaderboardEntry> entries = new ArrayList<>();
        Element listElement = doc.getElementsByTag("tbody").get(0);
        Elements listEntries = listElement.getElementsByTag("tr");
        for (Element listEntry : listEntries) {
            Elements playerInfoElements = listEntry.getElementsByTag("td");

            //Profile image
            Element pictureElem = playerInfoElements.get(0);
            String imageUrl = pictureElem.getElementsByTag("img").get(0).attr("src");

            //Player leaderboard rank
            Element rankElem = playerInfoElements.get(1);
            int rank = Integer.parseInt(rankElem.text().replaceAll("#*,*", ""));

            //Country & Player name
            Element playerInfoElem = playerInfoElements.get(2);
            String flagImageUrl = playerInfoElem.getElementsByTag("img").get(0).attr("src");
            String countryCode = flagImageUrl.replace("/imports/images/flags/","").replace(".png", "");
            String playerName = playerInfoElem.getElementsByTag("span").get(0).text();
            String playerUrl = playerInfoElem.getElementsByTag("a").get(0).attr("href");
            long playerId = Long.parseLong(playerUrl.replace("/u/",""));

            //PP
            Element ppElem = playerInfoElements.get(3);
            String ppValueText = ppElem.getElementsByClass("scoreTop ppValue").get(0).text();
            float ppValue =  Float.parseFloat(ppValueText.replace(",",""));

            //Weekly Change
            Element weeklyChangeElem = playerInfoElements.get(4);
            String weeklyChangeText = weeklyChangeElem.text();
            int weeklyChange = Integer.parseInt(weeklyChangeText.replaceAll("\\+*,*", ""));

            LeaderboardEntry currentEntry = new LeaderboardEntry(imageUrl, countryCode, playerName, playerId, playerUrl, rank, ppValue, weeklyChange);
            entries.add(currentEntry);
        }
        return entries;
    }
}
