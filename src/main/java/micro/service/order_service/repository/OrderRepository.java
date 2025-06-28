package micro.service.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import micro.service.order_service.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
