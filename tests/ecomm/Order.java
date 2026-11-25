package ecomm;

import java.util.List;

public class Order {
    private final long orderId;
    private final List<Item> items;
    private final String origin;
    private boolean isProcessed;
    private boolean isPaid;

    public Order(long orderId, List<Item> items) {
        this(orderId, items, "United States");
    }

    public Order(long orderId, List<Item> items, String origin) {
        this.orderId = orderId;
        this.items = items;
        this.origin = origin;
        this.isProcessed = false;
        this.isPaid = false;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public List<Item> getItems() {
        return items;
    }

    public void removeItem(Item item) {
        this.items.remove(item);
    }

    public long getOrderId() {
        return orderId;
    }

    public String getOrigin() {
        return origin;
    }

    @Override
    public String toString() {
        return String.format("""
                Order: %d
                  Origin: %s
                  Items: %s
                  Processed: %b
                  Paid: %b
                """, this.orderId, this.origin, this.items.toString(), this.isProcessed, this.isPaid);
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}
