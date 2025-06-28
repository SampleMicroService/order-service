package micro.service.order_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailTemplate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @Lob
    private String subjectTemplate;
    @Lob
    private String bodyTemplate;
    private boolean isHtml;
    private boolean isActive;
    // getters/setters
}
