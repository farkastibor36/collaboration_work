import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class User {
    private int id;
    private String name;
    private String city;
    private int age;
    private Country country;
    private ShoppingCart shoppingCart;
    private MonetaryAmount balance;
}
