package co.com.bancolombia.model.transaction.gateways;

import co.com.bancolombia.model.transaction.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface TransactionRepository {
    Mono<Transaction> save(Transaction transaction);
    Flux<Transaction> findByBoxId(String boxId);
    Flux<Transaction> findByBoxIdAndDate(String boxId, LocalDate date);
}