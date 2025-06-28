package micro.service.order_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order implements Comparable<Order>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String productName;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalPrice;

    private LocalDateTime orderDate;

	@Override
	public int compareTo(Order o) {
		return o.getOrderDate().compareTo(this.getOrderDate());
	}
}

