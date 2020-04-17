public class ConfigItem {
    private String mysqlUsername;
    private String mysqlPassword;
    private String mysqlServer;
    private String mqttUsername;
    private String mqttPassword;
    private String mqttHost;
    private String homeAssistantToken;
    private String homeAssistantServer;

    public ConfigItem(String mysqlUsername, String mysqlPassword, String mysqlServer, String mqttUsername, String mqttPassword, String mqttHost, String homeAssistantToken, String homeAssistantServer) {
        this.mysqlUsername = mysqlUsername;
        this.mysqlPassword = mysqlPassword;
        this.mysqlServer = mysqlServer;
        this.mqttUsername = mqttUsername;
        this.mqttPassword = mqttPassword;
        this.mqttHost = mqttHost;
        this.homeAssistantToken = homeAssistantToken;
        this.homeAssistantServer = homeAssistantServer;
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
}
