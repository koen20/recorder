import java.io.IOException;
import java.util.Calendar;

public class recorder {
    public static void main(String args[]) {
        new Config();
        new Mysql();
        new Temperature();
        if (Config.config.getOverwatchPlayers() != null) {
            if (Config.config.getOverwatchPlayers().size() != 0) {
                new Overwatch();
            }
        }
        if (Config.config.getWotPlayers() != null && Config.config.getWotApiKey() != null) {
            if (Config.config.getWotPlayers().size() != 0) {
                new Wot();
            }
        }
    }

    public static long millisToNextDay(Calendar calendar) {
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);
        int hoursToNextDay = 23 - hours;
        int minutesToNextHour = 60 - minutes;
        int secondsToNextHour = 60 - seconds;
        int millisToNextHour = 1000 - millis;
        return hoursToNextDay * 60 * 60 * 1000 + minutesToNextHour * 60 * 1000 + secondsToNextHour * 1000 + millisToNextHour;
    }
}
