package micro.service.order_service.dto;

import lombok.Data;

@Data
public class OrderRequestDTO {
    private Long productId;
    private Integer quantity;
}
