import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;

public class Odoo {
    String database;
    String username;
    String apiKey;

    public Odoo() {
        this.database = Config.config.getOdooDatabase();
        this.username = Config.config.getOdooUsername();
        this.apiKey = Config.config.getOdooApiKey();
        updateData();
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new update(), 300000, 300000);// 5 minutes
    }

    public void updateData() {
        ArrayList<OdooOrderItem> list = getData(Mysql.odooGetLatestItem());
        if (list.size() > 0) {
            System.out.println("Inserting " + list.size());
        }
        for (OdooOrderItem odooOrderItem : list) {
            if (!Mysql.insertOdooItem(odooOrderItem)) {
                return;
            }
        }
    }

    public ArrayList<OdooOrderItem> getData(String startDate) {
        final XmlRpcClient client = new XmlRpcClient();

        final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
        try {
            common_config.setServerURL(new URL(String.format("%s/xmlrpc/2/common", Config.config.getOdooHost())));
            int uid = (int) client.execute(common_config, "authenticate", asList(database, username, apiKey, emptyMap()));
            final XmlRpcClient models = new XmlRpcClient() {{
                setConfig(new XmlRpcClientConfigImpl() {{
                    setServerURL(new URL(String.format("%s/xmlrpc/2/object", Config.config.getOdooHost())));
                }});
            }};
            List<Object> list = asList((Object[]) models.execute("execute_kw", asList(
                    database, uid, apiKey,
                    "pos.order", "search_read",
                    asList(asList(
                            asList("date_order", ">", startDate))),
                    new HashMap() {{
                        put("fields", asList("date_order", "amount_total", "amount_tax"));
                    }}
            )));
            ArrayList<OdooOrderItem> odooOrderItems = new ArrayList<>();
            list.forEach((it) -> {
                HashMap hashMap = (HashMap) it;
                String dateString = (String) hashMap.get("date_order");
                double amountTotal = (double) hashMap.get("amount_total");
                double amountTax = (double) hashMap.get("amount_tax");
                int id = (int) hashMap.get("id");

                String[] dateTimeSplit = dateString.split(" ");
                double priceExcl = amountTotal - amountTax;
                OdooOrderItem item = new OdooOrderItem(0, id, LocalDate.parse(dateTimeSplit[0]), LocalTime.parse(dateTimeSplit[1]), priceExcl, amountTotal);
                odooOrderItems.add(item);
            });
            System.out.println(odooOrderItems.size());
            return odooOrderItems;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class update extends TimerTask {
        @Override
        public void run() {
            try {
                updateData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
