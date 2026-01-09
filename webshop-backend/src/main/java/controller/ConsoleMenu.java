package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dao.JpaProductDao;
import dao.JpaUserDao;
import exceptions.FailedToDeleteException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.MonetaryAmount;
import model.MoneyCurrency;
import model.Product;
import model.User;
import service.*;
import utils.PrintUtils;
import utils.RuleUtils;
import validators.ProductValidator;
import validators.UserValidator;

public class ConsoleMenu {

    //TODO:Should modify!
    //- account registration!
    //- ergonomically!
    //- abandon cart tactic!
    //- change 1 to 6! Maybe point 1 will delete!
    //- offering bundles!
    //- Net promoter!
    //- behavior data!
    //- product details!
    //- offer in offer(we need your email for other bundles and present!)
    //- check user activation!
    //- sql data base for user data's!

    private final List<Product> products = new ArrayList<>();
    private final MonetaryAmount balance = new MonetaryAmount(10_000, MoneyCurrency.HUF);
    private final User user;
    private final ShoppingCart shoppingCart;
    private final PayService payService;
    private final ProductSearchService productSearchService = new ProductSearchService();
    private final ShopChatBot chatBot = new ShopChatBot();
    private final ChatContext chatContext;
    private final JpaUserDao jpaUserDao = new JpaUserDao();
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("webshop");
    private static final EntityManager entityManager = entityManagerFactory.createEntityManager();
    private static final JpaProductDao productDao = new JpaProductDao(entityManager);
    private static final ProductCRUDService productCRUDService = new ProductCRUDService(productDao);
    private static final JpaUserDao userDao = new JpaUserDao(entityManager);
    private static final UserCRUDService userCRUDService = new UserCRUDService(userDao);
    private static final AddProduct addProduct = new AddProduct(productCRUDService, new ProductValidator());
    private static final ListProduct listProduct = new ListProduct(productCRUDService);
    private static final DeleteProduct deleteProduct = new DeleteProduct(productCRUDService);
    private static final AddUser addUser = new AddUser(userCRUDService, new UserValidator());
    private static final FindUserById findUserById = new FindUserById(userCRUDService);
    private static final ListUsers listUser = new ListUsers(userCRUDService);
    private static final DeleteUser deleteUser = new DeleteUser(userCRUDService);
    // ===== Menu entry points =====

    public ConsoleMenu() {
        this.user = new User(1, "model.User", 30, null, null, balance);
        this.shoppingCart = new ShoppingCart();
        this.payService = new PayService();
        this.chatContext = new ChatContext(user, products, shoppingCart);
    }

    // ===== Menu actions (1-9) =====

    public void start() throws FailedToDeleteException, IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);

        while (true) {
            printMenu();
            int input = Integer.parseInt(sc.nextLine());
            switch (input) {
                case 1 -> addUser.execute();
                case 2 -> findUserById.execute();
                case 3 -> listUser.execute();
                case 4 -> deleteUser.execute();
                case 5 -> addProduct.execute(sc);
                case 6 -> listProduct.getAllProducts();
                case 7 -> deleteProduct.removeProduct();
                case 8 -> viewCart(sc);
                case 9 -> pay(sc);
                case 10 -> removeFromCart(sc);
                case 11 -> searchMenu(sc);
                case 12 -> addBalance(sc);
                case 13 -> getStatistics(jpaUserDao.findAll());
                case 14 -> chatBot.startChat(sc, chatContext);
                case 15 -> {
                    sc.close();
                    System.exit(0);
                }
                default -> PrintUtils.error("Invalid number, please try again.");
            }
        }
    }

    public static void printMenu() {
        PrintUtils.line();
        PrintUtils.title("=== WebShop ===");
        PrintUtils.line();
        System.out.println("1. Add New User ");
        System.out.println("2. Find user by id ");
        System.out.println("3. User List ");
        System.out.println("4. Delete User ");
        System.out.println("5. Add New Product ");
        System.out.println("6. Product List");
        System.out.println("7. Delete Product");
        System.out.println("8. View Cart");
        System.out.println("9. Pay");
        System.out.println("10. Remove");
        System.out.println("11. Search Product");
        System.out.println("12. Add Balance");
        System.out.println("13. Statistics");
        System.out.println("14. I need help please (ChatBot)");
        System.out.println("15. Exit");
        System.out.println();
    }

    private void viewCart(Scanner sc) {
        PrintUtils.info("=== Your Cart ===");

        ShoppingCart cart = new ShoppingCart(user);

        if (cart.getProducts().isEmpty()) {
            PrintUtils.info("Cart is empty.");
            pressEnterToContinue(sc);
            return;
        }

        for (Product p : shoppingCart.getProducts()) {
            System.out.println(
                    p.getId() + ". " + p.getName() + " - " +
                            p.getPrice().getAmount() + " " +
                            p.getPrice().getCurrency());
        }

        System.out.println("Total: " + shoppingCart.getTotalPrice());
        pressEnterToContinue(sc);
    }

    private void pay(Scanner sc) {
        payService.pay(shoppingCart);
        pressEnterToContinue(sc);
    }

    private void removeFromCart(Scanner sc) {
        System.out.println("=== Remove From Cart ===");

        if (shoppingCart.getProducts().isEmpty()) {
            System.out.println("Cart is empty ");
            return;
        }

        printProductList(shoppingCart.getProducts());
        System.out.println("Enter product ID to remove");
        long id = Long.parseLong(sc.nextLine());

        Product toRemove = findProductById(id);

        if (toRemove != null && shoppingCart.removeProduct(toRemove)) {
            System.out.println("Removed from cart: " + toRemove.getName());
        } else {
            System.out.println("Product not found in cart. ");
        }
        pressEnterToContinue(sc);
    }

    private void searchMenu(Scanner sc) {
        System.out.println("=== Search Products ===");
        System.out.println("1. Search by name");
        System.out.println("2. Search by price range");
        System.out.println("3. Only available products");
        System.out.println("4. Back");
        System.out.print("Choose option: ");

        int choice = Integer.parseInt(sc.nextLine());

        switch (choice) {
            case 1 -> searchByName(sc);
            case 2 -> searchByPriceRange(sc);
            case 3 -> searchByAvailability(sc);
            case 4 -> printMenu();
            default -> System.out.println("Invalid option. Please choose again.");
        }
    }

    private void addBalance(Scanner sc) {
        System.out.println("=== Add Balance ===");
        System.out.println("Current balance: " +
                user.getBalance().getAmount() + " " +
                user.getBalance().getCurrency());

        System.out.println("Enter amount to (0 = cancel): ");
        double amountToAdd = Double.parseDouble(sc.nextLine());

        if (amountToAdd <= 0) {
            PrintUtils.info("Top up cancelled.");
            return;
        }

        MonetaryAmount current = user.getBalance();
        double newAmount = current.getAmount() + amountToAdd;

        MonetaryAmount newBalance = new MonetaryAmount(
                newAmount,
                current.getCurrency()
        );

        user.setBalance(newBalance);
        System.out.println("New Balance: " +
                newBalance.getAmount() +
                " " + newBalance.getCurrency());
        pressEnterToContinue(sc);
    }

    // --- Search helpers ---

    private void searchByName(Scanner sc) {
        System.out.println("Enter text to search in product name: ");
        String text = sc.nextLine();

        List<Product> result = productSearchService.searchByName(products, text);
        System.out.println("=== Search result (By Name) ===");
        printProductList(result);
        pressEnterToContinue(sc);
    }

    private void searchByPriceRange(Scanner sc) {
        System.out.println("Enter the minimum price: ");
        double min = sc.nextDouble();
        System.out.println("Enter the maximum price: ");
        double max = sc.nextDouble();
        sc.nextLine();

        if (!RuleUtils.isPositive(min) || !RuleUtils.isPositive(max) || min > max) {
            PrintUtils.error("Invalid price range!");
            pressEnterToContinue(sc);
            return;
        }

        List<Product> result = productSearchService.searchByPriceRange(products, min, max);
        System.out.println("=== Search result (by price) ===");
        printProductList(result);
        pressEnterToContinue(sc);
    }

    private void searchByAvailability(Scanner sc) {
        List<Product> result = productSearchService.searchByAvailability(products);
        PrintUtils.info("=== Available products (stock > 0) ===");
        printProductList(result);
        pressEnterToContinue(sc);
    }

    // --- Helpers / utils inside controller.ConsoleMenu ---

    private Product findProductById(long id) {
        for (Product p : products) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public void printProductList(List<Product> list) {
        if (list.isEmpty()) {
            System.out.println("No products. ");
            return;
        }

        for (Product p : list) {
            System.out.println(
                    p.getId() + ". " +
                            p.getName() + " - " +
                            p.getPrice().getAmount() + " " +
                            p.getPrice().getCurrency() + " | stock: " +
                            p.getStock()
            );
        }
    }

    public void getStatistics(List<User> users) {
        OrderReportService reportService = new OrderReportService(users);
        System.out.println("=== Statistics ===");
        System.out.println("Total users: " + users.size());
        System.out.println("Total sales per product:");
        reportService.getTotalSalesProduct().forEach((product, amount) -> System.out.println(product + ": " + amount));
        System.out.println();
        System.out.println("Total revenue:");
        System.out.println(reportService.getTotalRevenue() + "Euro");
        System.out.println();
        System.out.println("Orders per user");
        reportService.getOrderCountPerUser().forEach((user, amount) -> System.out.println(user + ": " + amount));
        pressEnterToContinue(new Scanner(System.in));
    }

    private void pressEnterToContinue(Scanner sc) {
        System.out.println("\nPress ENTER to return to the menu...");
        sc.nextLine();
    }
}