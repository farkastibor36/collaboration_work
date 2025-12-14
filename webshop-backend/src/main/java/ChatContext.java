import java.util.List;

public class ChatContext {

    private final User user;
    private final List<Product> products;
    private final ShoppingCart cart;

    public ChatContext(User user, List<Product> products, ShoppingCart cart) {
        this.user = user;
        this.products = products;
        this.cart = cart;
    }

    public User getUser() {
        return user;
    }

    public List<Product> getProducts() {
        return products;
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public double getBalanceAmount() {
        return user.getBalance().getAmount();
    }

    public boolean hasItemsInCart() {
        return !cart.getProducts().isEmpty();
    }
}
