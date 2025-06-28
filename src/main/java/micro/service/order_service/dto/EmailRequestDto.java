package micro.service.order_service.dto;

import java.io.File;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDto {
	
	private String mailCode;
	
	private String toMail;
	
	private Map<String,String> placeHolder;
	
	private File file;
}
