package micro.service.order_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmtpConfig {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String host;
    private int port;
    private String username;
    private String password;
    private String fromEmail;
    private boolean isActive;
    private boolean useTls;
    private boolean useSsl;
    // getters/setters
}

