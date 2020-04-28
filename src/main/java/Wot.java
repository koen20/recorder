import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Wot {
    public Wot() {
        for (String player : Config.config.getWotPlayers()) {
            try {
                updatePlayerStats(player);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new update(), recorder.millisToNextDay(Calendar.getInstance()) + 3600000, 86400000);//1 day
    }

    public class update extends TimerTask {
        @Override
        public void run() {

            try {
                for (String player : Config.config.getWotPlayers()) {
                    updatePlayerStats(player);
                }
            } catch (Exception e) {
                Timer updateTimer = new Timer();
                updateTimer.schedule(new update(), 1800000);//30 minutes
                e.printStackTrace();
            }
        }
    }

    private void updatePlayerStats(String player) throws IOException {
        WotPlayerItem item = null;
        item = updateStats(player);
        insertDb(item);
    }

    private WotPlayerItem updateStats(String player) throws IOException {
        WotPlayerItem item = null;
        String nickname;
        int globalRating;
        int battles;
        int losses;
        int wins;

        String url = "https://api.worldoftanks.eu/wot/account/info/?application_id=" + Config.config.getWotApiKey() + "&account_id=" + player;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject jsonObject = new JSONObject(response.toString()).getJSONObject("data").getJSONObject(player);
        JSONObject statistics = jsonObject.getJSONObject("statistics").getJSONObject("all");

        nickname = jsonObject.getString("nickname");
        globalRating = jsonObject.getInt("global_rating");
        battles = statistics.getInt("battles");
        losses = statistics.getInt("losses");
        wins = statistics.getInt("wins");


        item = new WotPlayerItem(nickname, globalRating, battles, losses, wins);

        return item;
    }

    private void insertDb(WotPlayerItem item) {
        Calendar cal = Calendar.getInstance();

        double winrate = round((Double.parseDouble(item.getWins() + "") / Double.parseDouble(item.getBattles() + "")) * 100, 2);

        Mysql.insertData("INSERT INTO wot VALUES (DEFAULT, '" + Mysql.getMysqlDateString(cal.getTimeInMillis()) +
                "', '" + item.getGlobalRating() + "', '" + item.getBattles() + "', '" + item.getLosses() + "', '" + item.getWins()
                + "', '" + winrate + "', '" + item.getPlayer() + "')");
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
