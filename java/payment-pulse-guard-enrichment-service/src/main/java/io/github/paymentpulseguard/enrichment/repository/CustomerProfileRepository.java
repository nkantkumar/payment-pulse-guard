package io.github.paymentpulseguard.enrichment.repository;

import io.github.paymentpulseguard.enrichment.domain.CustomerProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfileEntity, String> {
}
