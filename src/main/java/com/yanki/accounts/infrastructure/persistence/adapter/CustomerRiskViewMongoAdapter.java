package com.yanki.accounts.infrastructure.persistence.adapter;

import com.yanki.accounts.domain.model.CustomerRiskView;
import com.yanki.accounts.domain.port.out.CustomerRiskViewPort;
import com.yanki.accounts.infrastructure.persistence.document.CustomerRiskViewDocument;
import com.yanki.accounts.infrastructure.persistence.repository.SpringDataCustomerRiskViewRepository;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Adaptador de salida para la vista local de riesgo del cliente sobre MongoDB.
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerRiskViewMongoAdapter implements CustomerRiskViewPort {

    private final SpringDataCustomerRiskViewRepository repository;

    @Override
    public Maybe<CustomerRiskView> findByCustomerId(String customerId) {
        log.debug("[MongoDB] Buscando customer_risk_view: customerId={}", customerId);
        return Maybe.fromPublisher(
                repository.findByCustomerId(customerId).map(CustomerRiskViewMongoAdapter::toDomain)
        ).doOnSuccess(view -> log.debug("[MongoDB] customer_risk_view encontrada: customerId={}, hasOverdueDebt={}",
                customerId, view.getHasOverdueDebt()))
         .doOnError(throwable -> log.error("[MongoDB] Error buscando customer_risk_view: customerId={} - {}",
                 customerId, throwable.getMessage()));
    }

    @Override
    public Single<CustomerRiskView> save(CustomerRiskView view) {
        log.debug("[MongoDB] Guardando customer_risk_view: customerId={}, hasOverdueDebt={}",
                view.getCustomerId(), view.getHasOverdueDebt());
        return Single.fromPublisher(
                repository.save(toDocument(view)).map(CustomerRiskViewMongoAdapter::toDomain)
        ).doOnSuccess(saved -> log.debug("[MongoDB] customer_risk_view guardada: customerId={}", saved.getCustomerId()))
         .doOnError(throwable -> log.error("[MongoDB] Error guardando customer_risk_view: customerId={} - {}",
                 view.getCustomerId(), throwable.getMessage()));
    }

    static CustomerRiskViewDocument toDocument(CustomerRiskView view) {
        return CustomerRiskViewDocument.builder()
                .id(view.getId())
                .customerId(view.getCustomerId())
                .hasOverdueDebt(view.getHasOverdueDebt())
                .overdueAmount(view.getOverdueAmount())
                .creditId(view.getCreditId())
                .daysOverdue(view.getDaysOverdue())
                .updatedAt(view.getUpdatedAt())
                .build();
    }

    static CustomerRiskView toDomain(CustomerRiskViewDocument doc) {
        return CustomerRiskView.builder()
                .id(doc.getId())
                .customerId(doc.getCustomerId())
                .hasOverdueDebt(doc.getHasOverdueDebt())
                .overdueAmount(doc.getOverdueAmount())
                .creditId(doc.getCreditId())
                .daysOverdue(doc.getDaysOverdue())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
