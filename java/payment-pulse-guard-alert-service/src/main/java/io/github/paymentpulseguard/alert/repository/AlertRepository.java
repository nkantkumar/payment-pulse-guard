package io.github.paymentpulseguard.alert.repository;

import io.github.paymentpulseguard.alert.domain.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<AlertEntity, UUID> {
}
