package io.github.paymentpulseguard.transaction.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "account_id")
    private String accountId;

    private BigDecimal amount;
    private String currency;

    @Column(name = "transaction_type")
    private String type;

    private String channel;

    @Column(name = "beneficiary_id")
    private String beneficiaryId;

    @Column(name = "beneficiary_country")
    private String beneficiaryCountry;

    @Column(name = "ip_address")
    private String ipAddress;

    private Instant timestamp;
}
