package service;

import model.MonetaryAmount;
import model.User;
import utils.PrintUtils;

public class PayService {

    /**
     * Tries to pay the contents of the given shopping cart.
     * If the user's balance is enough:
     * - subtract the total price from the user's balance
     * - prints which user bought which products
     * - clears the cart
     * Otherwise :
     * - prints an error message about insufficient balance
     */

    public void pay(ShoppingCart cart) {

        //Owner Data's :

        User owner = cart.getOwner();
        MonetaryAmount balance = owner.getBalance();

        int totalPrice = cart.getTotalPrice();
        double balanceAmount = balance.getAmount();

        //Check : May you have enough money?

        if (balanceAmount >= totalPrice) {
            double newAmount = balanceAmount - totalPrice;
            MonetaryAmount newBalance = new MonetaryAmount(
                    newAmount,
                    balance.getCurrency()
            );

            owner.setBalance(newBalance);

            PrintUtils.success("Payment accepted!");
            PrintUtils.success(owner.getName() + " bought: " + cart.getProducts());
            PrintUtils.success("You are a Rich Kid" +
                    "(for now)");
            cart.clear();
        } else {
            double missing = totalPrice - balanceAmount;
            PrintUtils.error("Error: user " + owner.getName() + " has insufficient balance. Needed: " + totalPrice +
                    ", available: " + balanceAmount);
            PrintUtils.info("You are Poor Bro :D (missing: " + (int) missing + "HUF)");
            PrintUtils.info("Tip: use option 7 (Add balance) to become a RichKid");
        }
    }
}