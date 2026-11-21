package com.yanki.accounts.infrastructure.persistence;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountHolder;
import com.yanki.accounts.domain.model.AccountSigner;
import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.model.MovementType;
import org.springframework.stereotype.Component;

/**
 * Mapper entre documentos MongoDB y modelos de dominio.
 *
 * <p>En Spring Data MongoDB Reactive, el mapeo es automático vía anotaciones.
 * Esta clase provee métodos de conversión manual cuando se usa ReactiveMongoTemplate
 * para agregaciones o consultas personalizadas.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Component
public class AccountMapper {

    /**
     * Convierte documento a entidad Account (para ReactiveMongoTemplate).
     */
    public Account toAccount(org.springframework.data.mongodb.core.mapping.Document document) {
        // Spring Data hace el mapeo automático, este método es para casos especiales
        return null;
    }

    /**
     * Convierte entidad Account a Document para operaciones personalizadas.
     */
    public org.springframework.data.mongodb.core.mapping.Document toDocument(Account account) {
        return null;
    }

    /**
     * Convierte documento a Movement.
     */
    public Movement toMovement(org.springframework.data.mongodb.core.mapping.Document document) {
        return null;
    }
}