package model;

import jakarta.persistence.*;
import lombok.*;
import service.ShoppingCart;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
@ToString
public class User {

    public User(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "user_name")
    private String name;
    @Column(name = "age")
    private int age;
    @Embedded
    private Address address;
    @Embedded
    private ShoppingCart shoppingCart;
    @Embedded
    private MonetaryAmount balance;
}