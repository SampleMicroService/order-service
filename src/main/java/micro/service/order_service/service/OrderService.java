package micro.service.order_service.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import micro.service.order_service.dto.EmailRequestDto;
import micro.service.order_service.entity.Order;
import micro.service.order_service.repository.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private DynamicEmailService emailService;
	
	@Value("${invoice.path.file}")
	private String invoicePath;
	
	@Autowired
	private OrderRepository repo;
	
	@Autowired
    private  WebClient.Builder webClientBuilder;


	public void sendOrderConfirmationEmail(Order order,String name,String mail) throws Exception {
	    Map<String, String> placeholders = new HashMap<>();
	    placeholders.put("customerName", name);
	    placeholders.put("orderId", order.getId().toString());
	    placeholders.put("totalAmount", order.getTotalPrice().toString());

	    File invoiceFile = new File(invoicePath +"invoice_"+ order.getId() + ".pdf");
	    
//	    ExecutorService executor = Executors.newSingleThreadExecutor();
//	    
//	    Runnable task = ()->{
//	    	try {
//				emailService.sendEmail("ORDER_CONFIRMATION", mail, placeholders, invoiceFile);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//	    };
//	    
//	    executor.execute(task);
	    
	    EmailRequestDto request = new EmailRequestDto();
	    request.setFile(invoiceFile);
	    request.setMailCode("ORDER_CONFIRMATION");
	    request.setToMail(mail);
	    request.setPlaceHolder(placeholders);
	    
	    try {
			String message = webClientBuilder.build()
					.post()
					.uri("http://localhost:8083/mail/send")
					.bodyValue(request)
					.retrieve()
					.bodyToMono(String.class)
					.block();
			
			System.out.println(message);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	    
	}
	
	public List<Order> getRecentOrders() {
		LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
		List<Order> all = repo.findAll();
		List<Order> collect = all.stream().filter(o -> o.getOrderDate().isAfter(yesterday))
				.sorted()
				.collect(Collectors.toList());
		return collect;
	}
	
	public Map<String, List<Order>> getGroupOrders() {
		
		List<Order> all = repo.findAll();
		Map<String, List<Order>> collect = all.stream()
				.collect(Collectors.groupingBy(Order::getProductName));
		
		ConcurrentHashMap<String, List<Order>> concurrent = (ConcurrentHashMap<String, List<Order>>) collect;
		
		for(Map.Entry<String,List<Order>> map :concurrent.entrySet()) {
			if(map.getKey().equalsIgnoreCase("Tripod")) {
				concurrent.remove(map.getKey());
			}
		}
		return collect;
	}
}
