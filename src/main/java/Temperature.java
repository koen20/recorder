import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Temperature {
    static double tempInside;
    static double tempOutside;

    Temperature() {
        getTempInside();
        getTempOutside();
        System.out.println(tempInside);
        System.out.println(tempOutside);
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new updateDb(), 5000, 900000);
    }

    private class updateDb extends TimerTask {
        @Override
        public void run() {
            getTempInside();
            getTempOutside();
            Calendar cal = Calendar.getInstance();
            if (tempInside != -999) {
                Mysql.insertData("INSERT INTO sensors.temperature VALUES (DEFAULT, '" + Mysql.getMysqlDateString(cal.getTimeInMillis()) + "', '" + tempInside + "', 'inside')");
            }
            if (tempOutside != -999) {
                Mysql.insertData("INSERT INTO sensors.temperature VALUES (DEFAULT, '" + Mysql.getMysqlDateString(cal.getTimeInMillis()) + "', '" + tempOutside + "', 'outside')");
            }
        }
    }

    private String getTemp(String sensor) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Config.config.getHomeAssistantServer() + "/api/states/" + sensor).newBuilder();

        String url = urlBuilder.build().toString();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + Config.config.getHomeAssistantToken())
                .header("Content-Type", "application/json")
                .build();
        okhttp3.Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String res = null;
        try {
            res = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private double getTempInside() {
        try {
            JSONObject jsonObject = new JSONObject(getTemp(Config.config.getTempSensorInside()));
            tempInside = jsonObject.getDouble("state");
        } catch (Exception e) {
            e.printStackTrace();
            tempInside = -999;
        }
        return tempInside;
    }

    private double getTempOutside() {
        try {
            JSONObject jsonObject = new JSONObject(getTemp(Config.config.getTempSensorOutside()));
            tempOutside = jsonObject.getDouble("state");
        } catch (Exception e) {
            e.printStackTrace();
            tempOutside = -999;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", tempOutside);
            String url = "https://api.opensensemap.org/boxes/5c41fb5b1b7ca8001989ddc8/5c41fb5b1b7ca8001989ddc9";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            String urlParameters = "\n" + jsonObject.toString();
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempOutside;
    }
}
