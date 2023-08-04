package bot.dto.player;

import org.bson.Document;

public class PlayerSkills {

    private String playerName;
    private int accuracy;
    private int speed;
    private int stamina;
    private int reading;

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getReading() {
        return reading;
    }

    public void setReading(int reading) {
        this.reading = reading;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public static PlayerSkills fromDocument(Document document) {
        PlayerSkills skills = new PlayerSkills();
        skills.setPlayerName(document.getString("player_name"));
        Document skillDoc = (Document) document.get("skills");
        if (skillDoc != null) {
            skills.setAccuracy(skillDoc.getInteger("accuracy", 0));
            skills.setSpeed(skillDoc.getInteger("speed", 0));
            skills.setStamina(skillDoc.getInteger("stamina", 0));
            skills.setReading(skillDoc.getInteger("reading", 0));
        }
        return skills;
    }

    public Document toDocument() {
        Document skillDoc = new Document()
                .append("accuracy", accuracy)
                .append("speed", speed)
                .append("stamina", stamina)
                .append("reading", reading);
        return new Document()
                .append("player_name", playerName)
                .append("skills", skillDoc);
    }
}
