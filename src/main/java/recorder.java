import spark.Spark;

import java.io.IOException;

public class recorder {
    public static void main(String args[]) throws IOException {
        if (args.length > 0 && args[0].equals("log")) Log.enableLog(true);
        Config config = new Config();
        new Mysql();
        new Temperature();
        System.out.println(config.config.getMysqlServer());
        System.out.println("asdf");
        Spark.port(9999);
    }
}
