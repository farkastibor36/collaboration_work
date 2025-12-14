
import utils.ConsoleColors;
import utils.PrintUtils;

import java.util.Scanner;

public class ShopChatBot {

    public void startChat(Scanner sc, ChatContext ctx) {
        String userName = ctx.getUser().getName();

        System.out.println();
        System.out.println("=== ChatBot ===");
        System.out.println("Hi " + userName + ", how can I help you today?");
        System.out.println("(type 'exit' to go back to the main menu)");
        System.out.println();

        while (true) {
            System.out.print(ConsoleColors.WHITE + "You: " + ConsoleColors.RESET);
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("ChatBot: Okay, see you in the WebShop menu! :)");
                break;
            }

            String reply = respond(input, ctx);
            PrintUtils.chatBot(reply);
        }
    }

    private String respond(String input, ChatContext ctx) {
        String lower = input.toLowerCase();

        //1. Explicit AI / knowledge triggers

        if (lower.trim().equals("hello there")) {
            return "GENERAL KENOBI! :D";
        }

        if (lower.contains("hello")
                || lower.contains("hi")) {
            return "Hello there! I can help you with products, balance or payment.";
        }

        if (lower.contains("thank you")
                || lower.equals("thanks")
                || lower.equals("thx")
                || lower.equals("ty")
                || lower.equals("appreciate")) {
            return "You're welcome! :)";
        }

        if (lower.contains("Chatgpt") ||
                lower.contains("explain") ||
                lower.contains("world war")) {
            return "Bro...I'm just a simple chatbot :D ASk the ChatGPT please :D";
        }

        // Balance / money
        if (lower.contains("balance") || lower.contains("money")) {
            return "Your current balance is " + ctx.getBalanceAmount() +
                    " " + ctx.getUser().getBalance().getCurrency() +
                    ". You can add balance with option 7 (Add Balance).";
        }

        // Cart
        if (lower.contains("cart")) {
            if (ctx.hasItemsInCart()) {
                return "You already have items in your cart. Try option 3 (View Cart).";
            } else {
                return "Your cart is empty right now. Use option 2 (Product List) to add items.";
            }
        }

        if (lower.contains("pay") || lower.contains("buy") || lower.contains("purchase")) {
            return "To buy something, first add it to your cart (2), then use Pay (4).";
        }

        if (lower.contains("product") || lower.contains("laptop") || lower.contains("mouse")) {
            return "Check the Product List with option 2. Green lines mean you can afford them. 😉";
        }

        return "I'm just a simple console chatbot... Try words like 'product', 'balance', 'pay', 'cart' or 'hello'.";
    }
}