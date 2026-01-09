package service;

import jakarta.persistence.*;
import lombok.*;
import model.MoneyCurrency;
import model.Product;
import model.User;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Embeddable
@ToString
public class ShoppingCart {
    @ManyToMany
    private List<Product> products = new ArrayList<>();

    private int totalPrice;
    @Transient
    private User owner;

    public ShoppingCart(User owner) {
        this.owner = owner;
    }

    public boolean addProduct(Product p) throws IOException, InterruptedException {
        if (p.getStock() > 0) {
            products.add(p);
            totalPrice += (int) exchangeCurrency(p.getPrice().getAmount(), p.getPrice().getCurrency(), MoneyCurrency.EUR);
            p.setStock(p.getStock() - 1);
            return true;
        } else {
            System.out.println("model.Product " + p.getName() + " is out of stock!");
            return false;
        }
    }

    public boolean removeProduct(Product p) {
        if (products.remove(p)) {
            totalPrice -= p.getPrice().getAmount();
            p.setStock(p.getStock() + 1);
            return true;
        } else {
            System.out.println("model.Product " + p.getName() + " is not in the cart!");
            return false;
        }
    }

    public double exchangeCurrency(double amount, MoneyCurrency fromCurrency, MoneyCurrency toCurrency) throws IOException, InterruptedException {

        if (fromCurrency == MoneyCurrency.EUR) {
            return amount;
        }
        String url = "https://latest.currency-api.pages.dev/v1/currencies/eur.json";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        String json = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Double> response = (Map<String, Double>) mapper.readValue(json, Map.class).get("eur");
        String to = toCurrency.toString().toLowerCase();
        String from = fromCurrency.toString().toLowerCase();
        if (fromCurrency == MoneyCurrency.EUR) {
            return amount * response.get(to);
        } else if (toCurrency == MoneyCurrency.EUR) {
            return amount / response.get(from);
        }
        throw new IllegalArgumentException("Unsupported currency conversion");
    }

    public void clear() {
        products.clear();
        totalPrice = 0;
    }
}