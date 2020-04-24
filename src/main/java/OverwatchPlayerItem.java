public class OverwatchPlayerItem {
    private String player;
    private int supportComprank;
    private int tankComprank;
    private int damageComprank;
    private double compWinrate;
    private int compGamesPlayed;
    private double quickTimePlayed;
    private double compTimePlayed;
    private int quickGamesWon;
    private int compGamesWon;

    public OverwatchPlayerItem(String player, int supportComprank, int tankComprank, int damageComprank, double compWinrate, int compGamesPlayed, double quickTimePlayed, double compTimePlayed, int quickGamesWon, int compGamesWon) {
        this.player = player;
        this.supportComprank = supportComprank;
        this.tankComprank = tankComprank;
        this.damageComprank = damageComprank;
        this.compWinrate = compWinrate;
        this.compGamesPlayed = compGamesPlayed;
        this.quickTimePlayed = quickTimePlayed;
        this.compTimePlayed = compTimePlayed;
        this.quickGamesWon = quickGamesWon;
        this.compGamesWon = compGamesWon;
    }


    public double getCompWinrate() {
        return compWinrate;
    }

    public int getCompGamesPlayed() {
        return compGamesPlayed;
    }

    public double getQuickTimePlayed() {
        return quickTimePlayed;
    }

    public double getCompTimePlayed() {
        return compTimePlayed;
    }

    public int getQuickGamesWon() {
        return quickGamesWon;
    }

    public int getCompGamesWon() {
        return compGamesWon;
    }

    public String getPlayer() {
        return player;
    }

    public int getSupportComprank() {
        return supportComprank;
    }

    public int getTankComprank() {
        return tankComprank;
    }

    public int getDamageComprank() {
        return damageComprank;
    }
}
