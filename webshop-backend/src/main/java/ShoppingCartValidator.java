import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShoppingCartValidator implements Validator<ShoppingCart> {

    // TODO
    //  owner: cannot be null, must be valid by UserValidator
    //  products list: cannot be null, can be empty, must be real Product
    //  products list: cannot have null or duplicate items
    //  totalPrice: cannot be negative, must equal sum of product prices

    private final UserValidator userValidator = new UserValidator();
    private final ProductValidator productValidator = new ProductValidator();

    @Override
    public boolean isValid(ShoppingCart shoppingCart) {
        if (shoppingCart == null) return false;

        if (shoppingCart.getOwner() == null || !userValidator.isValid(shoppingCart.getOwner())) {
            return false;
        }

        List<Product> products = shoppingCart.getProducts();
        if (products == null) return false;


        Set<Product> uniqueProducts = new HashSet<>();
        for (Product product : products) {
            if (product == null || !productValidator.isValid(product)) return false;
            if (!uniqueProducts.add(product)) return false;
        }

        if (shoppingCart.getTotalPrice() < 0) return false;


        double sum = products.stream()
                .mapToDouble(p -> p.getPrice().getAmount())
                .sum();
        if (Double.compare(shoppingCart.getTotalPrice(), sum) != 0) return false;

        return true;
    }
}