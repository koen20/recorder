import spark.Spark;

import java.io.IOException;

public class recorder {
    public static void main(String args[]) throws IOException {
        if (args.length > 0 && args[0].equals("log")) Log.enableLog(true);
        new Config();
        new Mysql();
        new Temperature();
        if (Config.config.getOverwatchPlayers() != null) {
            if(Config.config.getOverwatchPlayers().size() != 0) {
                new Overwatch();
            }
        }
    }
}
