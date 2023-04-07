import java.util.ArrayList;

public class ConfigItem {
    private String mysqlUsername;
    private String mysqlPassword;
    private String mysqlServer;
    private String mqttUsername;
    private String mqttPassword;
    private String mqttHost;
    private String homeAssistantToken;
    private String homeAssistantServer;
    private String tempSensorInside;
    private String tempSensorOutside;
    private ArrayList<String> overwatchPlayers;
    private String wotApiKey;
    private ArrayList<String> wotPlayers;
    private String odooHost;
    private String odooDatabase;
    private String odooUsername;
    private String odooApiKey;

    public ConfigItem(String mysqlUsername, String mysqlPassword, String mysqlServer, String mqttUsername, String mqttPassword, String mqttHost, String homeAssistantToken, String homeAssistantServer, String tempSensorInside, String tempSensorOutside, ArrayList<String> overwatchPlayers, String wotApiKey, ArrayList<String> wotPlayers, String odooHost, String odooDatabase, String odooUsername, String odooApiKey) {
        this.mysqlUsername = mysqlUsername;
        this.mysqlPassword = mysqlPassword;
        this.mysqlServer = mysqlServer;
        this.mqttUsername = mqttUsername;
        this.mqttPassword = mqttPassword;
        this.mqttHost = mqttHost;
        this.homeAssistantToken = homeAssistantToken;
        this.homeAssistantServer = homeAssistantServer;
        this.tempSensorInside = tempSensorInside;
        this.tempSensorOutside = tempSensorOutside;
        this.overwatchPlayers = overwatchPlayers;
        this.wotApiKey = wotApiKey;
        this.wotPlayers = wotPlayers;
        this.odooHost = odooHost;
        this.odooDatabase = odooDatabase;
        this.odooUsername = odooUsername;
        this.odooApiKey = odooApiKey;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public String getMysqlServer() {
        return mysqlServer;
    }

    public String getMqttUsername() {
        return mqttUsername;
    }

    public String getMqttPassword() {
        return mqttPassword;
    }

    public String getMqttHost() {
        return mqttHost;
    }

    public String getHomeAssistantToken() {
        return homeAssistantToken;
    }

    public String getHomeAssistantServer() {
        return homeAssistantServer;
    }

    public ArrayList<String> getOverwatchPlayers() {
        return overwatchPlayers;
    }

    public String getTempSensorInside() {
        return tempSensorInside;
    }

    public String getTempSensorOutside() {
        return tempSensorOutside;
    }

    public String getWotApiKey() {
        return wotApiKey;
    }

    public ArrayList<String> getWotPlayers() {
        return wotPlayers;
    }

    public String getOdooHost() {
        return odooHost;
    }

    public String getOdooDatabase() {
        return odooDatabase;
    }

    public String getOdooUsername() {
        return odooUsername;
    }

    public String getOdooApiKey() {
        return odooApiKey;
    }
}
