package com.yanki.accounts.infrastructure.persistence.adapter;

import com.yanki.accounts.domain.model.CustomerCardView;
import com.yanki.accounts.domain.port.out.CustomerCardViewPort;
import com.yanki.accounts.infrastructure.persistence.document.CustomerCardViewDocument;
import com.yanki.accounts.infrastructure.persistence.repository.SpringDataCustomerCardViewRepository;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Adaptador de salida para la vista local de tarjetas del cliente sobre MongoDB.
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerCardViewMongoAdapter implements CustomerCardViewPort {

    private final SpringDataCustomerCardViewRepository repository;

    @Override
    public Maybe<CustomerCardView> findByCustomerId(String customerId) {
        log.debug("[MongoDB] Buscando customer_card_view: customerId={}", customerId);
        return Maybe.fromPublisher(
                repository.findByCustomerId(customerId).map(CustomerCardViewMongoAdapter::toDomain)
        ).doOnSuccess(view -> log.debug("[MongoDB] customer_card_view encontrada: customerId={}", customerId))
         .doOnError(throwable -> log.error("[MongoDB] Error buscando customer_card_view: customerId={} - {}",
                 customerId, throwable.getMessage()));
    }

    @Override
    public Single<CustomerCardView> save(CustomerCardView view) {
        log.debug("[MongoDB] Guardando customer_card_view: customerId={}", view.getCustomerId());
        return Single.fromPublisher(
                repository.save(toDocument(view)).map(CustomerCardViewMongoAdapter::toDomain)
        ).doOnSuccess(saved -> log.debug("[MongoDB] customer_card_view guardada: customerId={}", saved.getCustomerId()))
         .doOnError(throwable -> log.error("[MongoDB] Error guardando customer_card_view: customerId={} - {}",
                 view.getCustomerId(), throwable.getMessage()));
    }

    static CustomerCardViewDocument toDocument(CustomerCardView view) {
        return CustomerCardViewDocument.builder()
                .id(view.getId())
                .customerId(view.getCustomerId())
                .creditCardId(view.getCreditCardId())
                .hasCreditCard(view.getHasCreditCard())
                .updatedAt(view.getUpdatedAt())
                .build();
    }

    static CustomerCardView toDomain(CustomerCardViewDocument doc) {
        return CustomerCardView.builder()
                .id(doc.getId())
                .customerId(doc.getCustomerId())
                .creditCardId(doc.getCreditCardId())
                .hasCreditCard(doc.getHasCreditCard())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
