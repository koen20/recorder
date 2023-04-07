import java.time.LocalDate;
import java.time.LocalTime;

public class OdooOrderItem {
    private int id;
    private int odooId;
    private LocalDate date;
    private LocalTime time;
    private double priceExcl;
    private double priceIncl;

    public OdooOrderItem(int id, int odooId, LocalDate date, LocalTime time, double priceExcl, double priceIncl) {
        this.id = id;
        this.odooId = odooId;
        this.date = date;
        this.time = time;
        this.priceExcl = priceExcl;
        this.priceIncl = priceIncl;
    }

    public int getId() {
        return id;
    }

    public int getOdooId() {
        return odooId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public double getPriceExcl() {
        return priceExcl;
    }

    public double getPriceIncl() {
        return priceIncl;
    }
}
