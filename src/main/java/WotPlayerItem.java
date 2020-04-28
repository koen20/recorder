public class WotPlayerItem {
    private String player;
    private int globalRating;
    private int battles;
    private int losses;
    private int wins;

    public WotPlayerItem(String player, int globalRating, int battles, int losses, int wins) {
        this.player = player;
        this.globalRating = globalRating;
        this.battles = battles;
        this.losses = losses;
        this.wins = wins;
    }

    public String getPlayer() {
        return player;
    }

    public int getGlobalRating() {
        return globalRating;
    }

    public int getBattles() {
        return battles;
    }

    public int getLosses() {
        return losses;
    }

    public int getWins() {
        return wins;
    }
}