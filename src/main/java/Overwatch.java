import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Overwatch {
    public Overwatch() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new update(), recorder.millisToNextDay(Calendar.getInstance()), 86400000);//1 day
        for (String player : Config.config.getOverwatchPlayers()) {
            try {
                Mysql.insertOverwatchData(updateStats(player));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class update extends TimerTask {
        @Override
        public void run() {
            try {
                for (String player : Config.config.getOverwatchPlayers()) {
                    Mysql.insertOverwatchData(updateStats(player));
                }
            } catch (Exception e) {
                Timer updateTimer = new Timer();
                updateTimer.schedule(new update(), 1800000);//30 minutes
                e.printStackTrace();
            }
        }
    }

    private OverwatchPlayerItem updateStats(String player) throws IOException {
        OverwatchPlayerItem item;
        double win_rate = 0;
        int compGamesPlayed = 0;
        double quickTimePlayed = 0;
        double compTimePlayed = 0;
        int quickGamesWon = 0;
        int compGamesWon = 0;
        int comprankTank = 0;
        int comprankDps = 0;
        int comprankSupport = 0;
        int compGamesTied = 0;

        String response = getData(player);

        JSONObject jsonObject = new JSONObject(response);
        JSONObject compOverallstats = jsonObject.getJSONObject("competitive");
        JSONObject compGameStats = jsonObject.getJSONObject("games").getJSONObject("competitive");
        JSONObject quickGameStats = jsonObject.getJSONObject("games").getJSONObject("quickplay");
        JSONObject playtime = jsonObject.getJSONObject("playtime");

        try {
            if (!compOverallstats.getJSONObject("tank").isNull("rank")) {
                comprankTank = compOverallstats.getJSONObject("tank").getInt("rank");
            }
            if (!compOverallstats.getJSONObject("support").isNull("rank")) {
                comprankSupport = compOverallstats.getJSONObject("support").getInt("rank");
            }
            if (!compOverallstats.getJSONObject("damage").isNull("rank")) {
                comprankDps = compOverallstats.getJSONObject("damage").getInt("rank");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            win_rate = compGameStats.getDouble("win_rate");
        } catch (Exception ignored) {
        }
        try {
            compGamesPlayed = compGameStats.getInt("played");
            if (!compGameStats.isNull("won")) {
                compGamesWon = compGameStats.getInt("won");
            }
            compGamesTied = compGameStats.getInt("draw");

            quickGamesWon = quickGameStats.getInt("won");

            quickTimePlayed = parseTimeToHours(playtime.getString("quickplay"));
            compTimePlayed = parseTimeToHours(playtime.getString("competitive"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (compGamesWon == 0) {
                compGamesWon = compGamesPlayed - compGamesTied - compGameStats.getInt("lost");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (win_rate == 0) {
            win_rate = (compGamesWon / (compGamesPlayed - compGamesTied * 0.0)) * 100;
        }
        item = new OverwatchPlayerItem(player, comprankSupport, comprankTank, comprankDps, win_rate, compGamesPlayed, quickTimePlayed, compTimePlayed, quickGamesWon, compGamesWon);

        return item;
    }

    private String getData(String player) throws IOException {
        String url = "https://owapi.io/profile/pc/eu/" + player;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "recorder Github koen20");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public static double parseTimeToHours(String hourFormat) {

        double hours = 0;
        String[] split = hourFormat.split(":");

        try {

            hours += Double.parseDouble(split[0]);
            hours += Double.parseDouble(split[1]) / 60;
            hours += (Double.parseDouble(split[2]) / 60) / 60;
            return hours;

        } catch (Exception e) {
            return -1;
        }

    }
}
