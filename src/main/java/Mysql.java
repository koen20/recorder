import java.io.*;
import java.sql.*;
import java.util.*;

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
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO sensors.overwatch VALUES (?, NULL, ?, ?, ?, ?"
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
            if (Double.isNaN(item.getCompWinrate())) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setDouble(5, item.getCompWinrate());
            }
            stmt.setString(6, item.getPlayer());
            stmt.execute();
            stmt.close();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static boolean insertOdooItem(OdooOrderItem item) {
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO bloemenwinkel.bonRegels VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            stmt.setInt(1, item.getOdooId());
            stmt.setDate(2, java.sql.Date.valueOf(item.getDate()));
            stmt.setNull(3, Types.INTEGER);
            stmt.setDouble(4, (double) Math.round(item.getPriceExcl() * 100) / 100);
            stmt.setDouble(5, (double) Math.round(item.getPriceIncl() * 100) / 100);
            stmt.setNull(6, Types.VARCHAR);
            stmt.setNull(7, Types.VARCHAR);
            stmt.setNull(8, Types.VARCHAR);
            stmt.setTime(9, Time.valueOf(item.getTime()));

            stmt.execute();
            stmt.close();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String odooGetLatestItem() {
        String dateTime = null;
        try {
            Statement stmt = conn.createStatement();
            {
                String strSelect = "SELECT CONCAT(datum,' ',tijd) AS date_time FROM bloemenwinkel.bonRegels WHERE tijd IN(SELECT MAX(tijd) FROM bloemenwinkel.bonRegels WHERE datum IN(SELECT MAX(datum) FROM bloemenwinkel.bonRegels))";

                ResultSet rset = stmt.executeQuery(strSelect);
                while (rset.next()) {
                    dateTime = rset.getString(1);
                }
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return dateTime;
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
