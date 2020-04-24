import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
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
            updateTimer2.scheduleAtFixedRate(new process(), 3000, 300000);
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

    public static void insertData(String sql) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            addToQueue(sql);
        }
    }

    static void addToQueue(String sql) {
        sqlQueue = readFromFile();
        sqlQueue.add(sql);
        saveToFile(sqlQueue);
    }

    private void proccessQueue() {
        sqlQueue = readFromFile();
        Iterator<String> d = sqlQueue.iterator();
        while (d.hasNext()) {
            String sql = d.next();
            try {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
                d.remove();
                saveToFile(sqlQueue);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static void saveToFile(ArrayList<String> list) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("queue"));
            outputStream.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static ArrayList<String> readFromFile() {
        ArrayList<String> res = new ArrayList<String>();
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("queue"));
            res = (ArrayList<String>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    static String getMysqlDateString(long milliseconds) {
        java.util.Date dt = new java.util.Date(milliseconds);

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(dt);
    }
}
