import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Mysql {
    public static Connection conn;
    public static ArrayList<String> sqlQueue = new ArrayList<String>();

    Mysql() {
        try {
            conn = DriverManager.getConnection(Config.config.getMysqlServer(),
                    Config.config.getMysqlUsername(), Config.config.getMysqlPassword());
            Timer updateTimer = new Timer();
            updateTimer.scheduleAtFixedRate(new checkMysqlConnection(), 2000, 10000);
            Timer updateTimer2 = new Timer();
            updateTimer2.scheduleAtFixedRate(new process(), 300000, 300000);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class process extends TimerTask {
        @Override
        public void run() {
            proccessQueue();
        }
    }

    private class checkMysqlConnection extends TimerTask {
        @Override
        public void run() {
            try {
                if (!conn.isValid(2700)) {
                    conn.close();
                    conn = DriverManager.getConnection(Config.config.getMysqlServer(),
                            Config.config.getMysqlUsername(), Config.config.getMysqlPassword());
                    proccessQueue();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insertData(Connection conn, String sql) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            sqlQueue.add(sql);
        }
    }

    private void proccessQueue() {
        for (String sql : sqlQueue) {
            try {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
                sqlQueue.remove(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
