package article.legacy;

/**
 * The "before" picture from the article: business rules buried in if-statements.
 * Kept here only so the article's anti-pattern snippet is compiled too.
 */
public class OrderService {

    public interface Order {
        String getStatus();
    }

    public interface ErpAdapter {
        void sendOrder(Order order);
    }

    private final ErpAdapter erpAdapter;

    public OrderService(ErpAdapter erpAdapter) {
        this.erpAdapter = erpAdapter;
    }

    public ErpAdapter getErpAdapter() {
        return erpAdapter;
    }

    public void complete(Order order) {
        if (order.getStatus().equals("success")) {
            this.getErpAdapter().sendOrder(order);
        }
    }
}
