import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private static boolean enableLog = false;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-DD-mm HH:mm:ss - ");

    public static void enableLog(boolean enable) {
        enableLog = enable;
        d("Logging enabled");
    }

    public static void d(String message) {
        if (!enableLog) return;
        System.out.println(getTime() + message);
    }

    public static void e(String message) {
        System.err.println(getTime() + message);
    }

    private static String getTime() {
        return SIMPLE_DATE_FORMAT.format(new Date());
    }
}