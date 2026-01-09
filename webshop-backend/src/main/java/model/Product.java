package model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@ToString
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "product_name")
    private String name;
    @Column(name = "product_price")
    private MonetaryAmount price;
    @Column(name = "product_stock")
    private int stock;
}