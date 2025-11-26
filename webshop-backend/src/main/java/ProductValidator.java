public class ProductValidator implements Validator<Product> {

    //TODO
    // product-name: only lowercase letters and spaces are allowed
    // product-price: must be positive
    // product-stock: must be a positive number

    //FIXME: null values must also be excluded

    @Override
    public boolean isValid(Product product) {
        if (!product.getName().matches("[a-z ]+")) {
            return false;
        }
        if (product.getPrice().getAmount() < 0) {
            return false;
        }
        if (product.getStock() < 0) {
            return false;
        }
        return true;
    }
}