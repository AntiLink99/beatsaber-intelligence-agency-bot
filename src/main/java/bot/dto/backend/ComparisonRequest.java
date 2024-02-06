package bot.dto.backend;

public class ComparisonRequest {
    private ComparisonPlayerData player1;
    private ComparisonPlayerData player2;

    public ComparisonRequest() {}

    public ComparisonRequest(ComparisonPlayerData player1Data, ComparisonPlayerData player2Data) {
        this.player1 = player1Data;
        this.player2 = player2Data;
    }

    public ComparisonPlayerData getPlayer1() {
        return player1;
    }

    public void setPlayer1(ComparisonPlayerData player1) {
        this.player1 = player1;
    }

    public ComparisonPlayerData getPlayer2() {
        return player2;
    }

    public void setPlayer2(ComparisonPlayerData player2) {
        this.player2 = player2;
    }

    @Override
    public String toString() {
        return "ComparisonRequest{" +
                "player1Data=" + player1 +
                ", player2Data=" + player2 +
                '}';
    }
}
