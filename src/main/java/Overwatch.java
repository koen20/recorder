import org.json.JSONArray;
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
        int compGamesLost = 0;

        String response = getData(player, false);

        JSONObject jsonObject = new JSONObject(response);
        JSONArray compStatsAllHeroes = jsonObject.getJSONObject("stats").getJSONObject("pc").getJSONObject("competitive").getJSONObject("career_stats").getJSONArray("all-heroes");
        JSONArray allHeroesStats = null;

        for (int i = 0; i < compStatsAllHeroes.length(); i++) {
            if (compStatsAllHeroes.getJSONObject(i).getString("category").equals("game")) {
                allHeroesStats = compStatsAllHeroes.getJSONObject(i).getJSONArray("stats");
            }
        }

        try {
            JSONObject summaryComp = jsonObject.getJSONObject("summary").getJSONObject("competitive").getJSONObject("pc");
            comprankTank = getCompRank(summaryComp.getJSONObject("tank"));
            comprankDps = getCompRank(summaryComp.getJSONObject("dps"));
            comprankSupport = getCompRank(summaryComp.getJSONObject("support"));
        } catch (Exception ignored) {
        }
        try {
            if (allHeroesStats != null) {
                compGamesPlayed = getStatFromList(allHeroesStats, "games_played");
                compGamesWon = getStatFromList(allHeroesStats, "games_won");
                compGamesLost = getStatFromList(allHeroesStats, "games_lost");
                compTimePlayed = getStatFromList(allHeroesStats, "time_played") / 60.0 / 60.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        win_rate = (compGamesWon / (compGamesPlayed - (compGamesPlayed - compGamesWon - compGamesLost) * 0.0)) * 100;
        item = new OverwatchPlayerItem(player, comprankSupport, comprankTank, comprankDps, win_rate, compGamesPlayed, quickTimePlayed, compTimePlayed, quickGamesWon, compGamesWon);

        return item;
    }

    private int getStatFromList(JSONArray jsonArray, String key) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            if (object.getString("key").equals(key)) {
                return object.getInt("value");
            }
        }
        return 0;
    }

    private int getCompRank(JSONObject item) {
        OverwatchDivision division = OverwatchDivision.valueOf(item.getString("division"));
        int compRank = 0;
        if (division.ordinal() > 0) {
            compRank += 1500 + (division.ordinal() - 1) * 500;
        }
        compRank += (5 - item.getInt("tier")) * 100;
        return compRank;
    }

    private String getData(String player, boolean summary) throws IOException {
        String url = "https://overfast-api.tekrop.fr/players/" + player;
        if (summary) {
            url = url + "/summary";
        }

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
