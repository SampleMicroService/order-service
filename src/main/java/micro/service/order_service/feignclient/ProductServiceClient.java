package micro.service.order_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import micro.service.order_service.dto.ProductDTO;

@FeignClient(name = "product_service",url = "${product.service.url}")
public interface ProductServiceClient {
	
	@GetMapping
	ProductDTO getProductById(@PathVariable("id") Long id);
}
