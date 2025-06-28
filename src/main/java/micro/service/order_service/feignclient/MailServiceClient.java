package micro.service.order_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import micro.service.order_service.dto.EmailRequestDto;


@FeignClient(name = "mail-service", url = "http://localhost:8083/mail")
public interface MailServiceClient {
	
	@PostMapping("/send")
	String sendMail(@RequestBody EmailRequestDto request);

}
