package micro.service.order_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import micro.service.order_service.entity.SmtpConfig;

public interface SmtpConfigRepository extends JpaRepository<SmtpConfig, Long> {
    Optional<SmtpConfig> findByIsActiveTrue();
}