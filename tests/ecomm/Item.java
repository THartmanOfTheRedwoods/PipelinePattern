package ecomm;

public class Item {
    private final String name;
    private long quantity;

    public Item(String name, long quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("""
                Item: %s, Quantity: %d
                """, this.name, this.quantity);
    }
}
