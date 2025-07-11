package co.com.bancolombia.model.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private String id;
    private String boxId;
    private TransactionType type; // INCOME, EXPENSE
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
}