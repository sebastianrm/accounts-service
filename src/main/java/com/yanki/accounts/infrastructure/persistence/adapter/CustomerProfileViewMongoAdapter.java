package com.yanki.accounts.infrastructure.persistence.adapter;

import com.yanki.accounts.domain.model.CustomerProfileView;
import com.yanki.accounts.domain.port.out.CustomerProfileViewPort;
import com.yanki.accounts.infrastructure.persistence.document.CustomerProfileViewDocument;
import com.yanki.accounts.infrastructure.persistence.repository.SpringDataCustomerProfileViewRepository;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Adaptador de salida para la vista local del perfil del cliente sobre MongoDB.
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerProfileViewMongoAdapter implements CustomerProfileViewPort {

    private final SpringDataCustomerProfileViewRepository repository;

    @Override
    public Maybe<CustomerProfileView> findByCustomerId(String customerId) {
        log.debug("[MongoDB] Buscando customer_profile_view: customerId={}", customerId);
        return Maybe.fromPublisher(
                repository.findByCustomerId(customerId).map(CustomerProfileViewMongoAdapter::toDomain)
        ).doOnSuccess(view -> log.debug("[MongoDB] customer_profile_view encontrada: customerId={}", customerId))
         .doOnError(throwable -> log.error("[MongoDB] Error buscando customer_profile_view: customerId={} - {}",
                 customerId, throwable.getMessage()));
    }

    @Override
    public Single<CustomerProfileView> save(CustomerProfileView view) {
        log.debug("[MongoDB] Guardando customer_profile_view: customerId={}", view.getCustomerId());
        return Single.fromPublisher(
                repository.save(toDocument(view)).map(CustomerProfileViewMongoAdapter::toDomain)
        ).doOnSuccess(saved -> log.debug("[MongoDB] customer_profile_view guardada: customerId={}", saved.getCustomerId()))
         .doOnError(throwable -> log.error("[MongoDB] Error guardando customer_profile_view: customerId={} - {}",
                 view.getCustomerId(), throwable.getMessage()));
    }

    static CustomerProfileViewDocument toDocument(CustomerProfileView view) {
        return CustomerProfileViewDocument.builder()
                .id(view.getId())
                .customerId(view.getCustomerId())
                .customerType(view.getCustomerType())
                .profile(view.getProfile())
                .status(view.getStatus())
                .documentType(view.getDocumentType())
                .documentNumber(view.getDocumentNumber())
                .email(view.getEmail())
                .updatedAt(view.getUpdatedAt())
                .build();
    }

    static CustomerProfileView toDomain(CustomerProfileViewDocument doc) {
        return CustomerProfileView.builder()
                .id(doc.getId())
                .customerId(doc.getCustomerId())
                .customerType(doc.getCustomerType())
                .profile(doc.getProfile())
                .status(doc.getStatus())
                .documentType(doc.getDocumentType())
                .documentNumber(doc.getDocumentNumber())
                .email(doc.getEmail())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
