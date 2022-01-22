import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class Mysql {
    public static Connection conn;
    public static ArrayList<String> sqlQueue = new ArrayList<String>();

    Mysql() {
        try {
            conn = DriverManager.getConnection(Config.config.getMysqlServer(),
                    Config.config.getMysqlUsername(), Config.config.getMysqlPassword());
            Timer updateTimer = new Timer();
            updateTimer.scheduleAtFixedRate(new checkMysqlConnection(), 2000, 50000);
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
        System.out.println(sql);
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            addToQueue(sql);
        }
    }

    public static void insertOverwatchData(OverwatchPlayerItem item) {
        Calendar cal = Calendar.getInstance();
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO overwatch VALUES (?, NULL, ?, ?, ?, ?"
                    + ", '" + item.getCompGamesPlayed() + "', '" + item.getQuickTimePlayed() + "', '" + item.getCompTimePlayed() +
                    "', '" + item.getQuickGamesWon() + "', '" + item.getCompGamesWon() + "', ?)");

            stmt.setTimestamp(1, new Timestamp(cal.getTimeInMillis()));

            if (item.getTankComprank() == 0) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, item.getTankComprank());
            }
            if (item.getDamageComprank() == 0) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, item.getDamageComprank());
            }
            if (item.getSupportComprank() == 0) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, item.getSupportComprank());
            }
            if (item.getCompWinrate() == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, item.getCompWinrate());
            }
            stmt.setString(6, item.getPlayer());
            stmt.execute();
            stmt.close();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
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
