package micro.service.order_service.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import micro.service.order_service.annotation.LogTimming;
import micro.service.order_service.dto.OrderRequestDTO;
import micro.service.order_service.dto.ProductDTO;
import micro.service.order_service.entity.Order;
import micro.service.order_service.feignclient.ProductServiceClient;
import micro.service.order_service.repository.OrderRepository;
import micro.service.order_service.service.InvoiceService;
import micro.service.order_service.service.OrderService;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderService service;
    private final InvoiceService invoiceService;
    private final ProductServiceClient productClient;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequestDTO orderRequest) {
        ProductDTO product = productClient.getProductById(orderRequest.getProductId());
//        		webClientBuilder.build()
//                .get()
//                .uri("http://localhost:8081/products/" + orderRequest.getProductId())
//                .retrieve()
//                .bodyToMono(ProductDTO.class)
//                .block(); // blocking for simplicity

        if (product == null || product.getQuantity() < orderRequest.getQuantity()) {
            return ResponseEntity.badRequest().body("Product not available or insufficient stock");
        }

        Order order = Order.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .quantity(orderRequest.getQuantity())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(orderRequest.getQuantity())))
                .orderDate(LocalDateTime.now())
                .build();
        
        Order data = orderRepository.save(order);
        
        try {
        	invoiceService.generateInvoice(order,"Nithese");
			service.sendOrderConfirmationEmail(data, "Nithese", "nithesekannaa@gmail.com");
		} catch (Exception e) {
			e.printStackTrace();
		}

        return ResponseEntity.ok(data);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    @GetMapping("/page")
    @LogTimming("Print the exicution timming")
    public Page<Order> getAllOrdersPage(
    		@RequestParam(name = "page",defaultValue="0") int page,
    		@RequestParam(name = "size",defaultValue="5") int size,
    		@RequestParam(name = "sorting",defaultValue="id") String sorting,
    		@RequestParam(name = "accending",defaultValue="false") boolean accending
    		) {
    	Sort sort = accending ? Sort.by(sorting).ascending() : Sort.by(sorting).descending();
    	
    	Pageable pageable = PageRequest.of(page, size, sort);
    	
        Page<Order> all = orderRepository.findAll(pageable);
		return all;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<Order>> getRecentOrder(){
    	return ResponseEntity.ok(service.getRecentOrders());
    }
    
    @GetMapping("/group")
    public ResponseEntity<Map<String, List<Order>>> getGroupOrders(){
    	return ResponseEntity.ok(service.getGroupOrders());
    }
}

