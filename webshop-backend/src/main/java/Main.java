public class Main {
    public static void main(String[] args) {


        //I need it for PayService class, don't delete please or write to me a text before :)
        //Thank you!

        MonetaryAmount balance = new MonetaryAmount(10000, "HUF");
        User user = new User(
                1,
                "Oscar",
                "BudaPest",
                30,
                Country.HUNGARY, null, balance);

        ShoppingCart cart = new ShoppingCart(user);

        PayService payService = new PayService();
        payService.pay(cart);
    }
}
