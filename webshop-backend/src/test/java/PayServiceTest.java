import model.MonetaryAmount;
import model.MoneyCurrency;
import model.Product;
import model.User;
import org.junit.jupiter.api.Test;
import service.PayService;
import service.ShoppingCart;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PayServiceTest {

    // TODO: if the user has enough money to pay for the cart then:
    //  the cart content decreases
    //  the cart is emptied
    //  totalPrice is reset to zero
    @Test
    public void shouldSuccessfulPayment() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1)
                .name("Sanyi")
                .balance(new MonetaryAmount(1520, MoneyCurrency.EUR))
                .build();
        ShoppingCart cart = new ShoppingCart(user);
        Product milk = new Product(20L, "milk",
                new MonetaryAmount(2, MoneyCurrency.EUR), 10);
        cart.addProduct(milk);

        PayService payService = new PayService();
        payService.pay(cart);

        assertEquals(1518, user.getBalance().getAmount(), 0.001);
        assertTrue(cart.getProducts().isEmpty());
        assertEquals(0, cart.getTotalPrice());
    }
}