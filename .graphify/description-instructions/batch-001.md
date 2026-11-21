# Node Description Batch 2 of 10

Graphify is running in assistant/skill mode (no API key). You are the host
assistant (Claude Code / Codex / Gemini CLI). Read the prompt below and write
your JSON answer to the answer file.

## Prompt

You are documenting nodes in a knowledge graph.
For each entry below, write ONE concise factual plain-language sentence
describing what it is or does. Use only the provided context.
For a code symbol (kind=code-symbol — a function, class, or constant),
describe what the function/symbol does based on its name, source location
and neighbors — e.g. "Resolves the configured ontology profile from graphify.yaml.".
Write every description in English (en). Do not switch languages.
No marketing language.
Respond ONLY with a JSON object mapping each node id (as a string) to its
one-sentence description — no prose, no markdown fences.

- "messaging_accounteventconsumer_accounteventconsumer_oncardclosed": ".onCardClosed()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L86 | neighbors=[AccountEventConsumer, .buildKey(), .processIfNew(), .updateCardViewClosed()]
- "messaging_accounteventconsumer_accounteventconsumer_oncardopened": ".onCardOpened()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L76 | neighbors=[AccountEventConsumer, .buildKey(), .processIfNew(), .updateCardViewOpened()]
- "messaging_accounteventconsumer_accounteventconsumer_oncreditoverdue": ".onCreditOverdue()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L98 | neighbors=[AccountEventConsumer, .buildKey(), .processIfNew(), .updateRiskView()]
- "messaging_accounteventconsumer_accounteventconsumer_oncustomerdeleted": ".onCustomerDeleted()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L64 | neighbors=[AccountEventConsumer, .buildKey(), .markViewsDeleted(), .processIfNew()]
- "messaging_accounteventconsumer_accounteventconsumer_oncustomerregistered": ".onCustomerRegistered()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L44 | neighbors=[AccountEventConsumer, .buildKey(), .createProfileView(), .processIfNew()]
- "messaging_accounteventconsumer_accounteventconsumer_oncustomerupdated": ".onCustomerUpdated()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L54 | neighbors=[AccountEventConsumer, .buildKey(), .processIfNew(), .updateProfileView()]
- "messaging_accounteventconsumer_accounteventconsumer_ondebitpaymentrequested": ".onDebitPaymentRequested()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L110 | neighbors=[AccountEventConsumer, .buildKey(), .processIfNew(), .toCompletable()]
- "messaging_accounteventconsumer_accounteventconsumer_ontransferrequested": ".onTransferRequested()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L123 | neighbors=[AccountEventConsumer, .buildKey(), .processIfNew(), .toCompletable()]
- "messaging_accounteventconsumer_accounteventconsumer_updatecardviewclosed": ".updateCardViewClosed()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L255 | neighbors=[AccountEventConsumer, .onCardClosed(), .toCompletable(), .toSingle()]
- "messaging_accounteventconsumer_accounteventconsumer_updatecardviewopened": ".updateCardViewOpened()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L241 | neighbors=[AccountEventConsumer, .onCardOpened(), .toCompletable(), .toSingle()]
- "messaging_accounteventconsumer_accounteventconsumer_updateprofileview": ".updateProfileView()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L205 | neighbors=[AccountEventConsumer, .onCustomerUpdated(), .toCompletable(), .toSingle()]
- "messaging_accounteventconsumer_accounteventconsumer_updateriskview": ".updateRiskView()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L266 | neighbors=[AccountEventConsumer, .onCreditOverdue(), .toCompletable(), .toSingle()]
- "model_account_account": "Account" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/Account.java:L20 | neighbors=[Account.java, .canOperate(), .hasExceededFreeMovements(), .incrementMovementCount()]
- "persistence_accountmapper_accountmapper": "AccountMapper" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/AccountMapper.java:L19 | neighbors=[AccountMapper.java, .toAccount(), .toDocument(), .toMovement()]
- "persistence_customaccountrepository_customaccountrepository": "CustomAccountRepository" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/CustomAccountRepository.java:L14 | neighbors=[CustomAccountRepository.java, .countMovementsInMonth(), .findByCustomerAndTypeAndStatus(), .findMovementsByAccountAndDateRange()]
- "service_accountservice_accountservice_buildmovementevent": ".buildMovementEvent()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L851 | neighbors=[AccountService, .processMovement(), .settleDebitPayment(), .settleTransfer()]
- "service_accountservice_accountservice_closeaccount": ".closeAccount()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L200 | neighbors=[AccountService, .findById(), .getBalance(), .publishAccountClosedEvent()]
- "service_accountservice_accountservice_invalidatecache": ".invalidateCache()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L699 | neighbors=[AccountService, .processMovement(), .validateAndDebitAccount(), .validateAndTransfer()]
- "service_accountservice_accountservice_publishmovementevent": ".publishMovementEvent()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L865 | neighbors=[AccountService, .processMovement(), .settleDebitPayment(), .settleTransfer()]
- "service_accountservice_accountservice_validatecustomerforaccountopening": ".validateCustomerForAccountOpening()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L358 | neighbors=[AccountService, .openAccount(), .isValidPersonalCurrent(), .isValidPersonalSavings()]
- "config_cachepropertiestest_cachepropertiestest": "CachePropertiesTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/infrastructure/config/CachePropertiesTest.java:L9 | neighbors=[CachePropertiesTest.java, .defaultValues(), .setters()]
- "config_redisconfig_redisconfig": "RedisConfig" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/config/RedisConfig.java:L12 | neighbors=[RedisConfig.java, .objectMapper(), .reactiveStringRedisTemplate()]
- "customerprofileview": "CustomerProfileView" | kind=code-symbol | neighbors=[CustomerProfileViewRepository.java, CustomerProfileViewRepository, AccountService.java]
- "customerriskview": "CustomerRiskView" | kind=code-symbol | neighbors=[CustomerRiskViewRepository.java, CustomerRiskViewRepository, AccountService.java]
- "dto_accountrequest": "AccountRequest.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/AccountRequest.java:L1 | neighbors=[AccountHolderRequest, AccountRequest, AccountSignerRequest]
- "dto_accountresponse": "AccountResponse.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/AccountResponse.java:L1 | neighbors=[AccountHolderResponse, AccountResponse, AccountSignerResponse]
- "exception_businessexceptiontest_businessexceptiontest": "BusinessExceptionTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/exception/BusinessExceptionTest.java:L7 | neighbors=[BusinessExceptionTest.java, .constructor_setsCodeAndMessage(), .isRuntimeException()]
- "model_customerriskviewtest_customerriskviewtest": "CustomerRiskViewTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerRiskViewTest.java:L9 | neighbors=[CustomerRiskViewTest.java, .builder_noOverdueDebt(), .builder_setsAllFields()]
- "model_movementtest_movementtest": "MovementTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/MovementTest.java:L7 | neighbors=[MovementTest.java, .builder_setsAllFields(), .movementType_enumValues()]
- "persistence_accountmapper": "AccountMapper.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/AccountMapper.java:L1 | neighbors=[Account, Movement, AccountMapper]
- "persistence_customaccountrepository": "CustomAccountRepository.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/CustomAccountRepository.java:L1 | neighbors=[Account, Movement, CustomAccountRepository]
- "persistence_customaccountrepositoryimpl": "CustomAccountRepositoryImpl.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/CustomAccountRepositoryImpl.java:L1 | neighbors=[Account, Movement, CustomAccountRepositoryImpl]
- "processedevent": "ProcessedEvent" | kind=code-symbol | neighbors=[AccountEventConsumer.java, ProcessedEventRepository, AccountService.java]
- "repository_accountrepository": "AccountRepository.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/AccountRepository.java:L1 | neighbors=[Account, ReactiveMongoRepository, AccountRepository]
- "repository_customercardviewrepository": "CustomerCardViewRepository.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/CustomerCardViewRepository.java:L1 | neighbors=[CustomerCardView, ReactiveMongoRepository, CustomerCardViewRepository]
- "repository_customerprofileviewrepository": "CustomerProfileViewRepository.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/CustomerProfileViewRepository.java:L1 | neighbors=[CustomerProfileView, ReactiveMongoRepository, CustomerProfileViewRepository]
- "repository_customerriskviewrepository": "CustomerRiskViewRepository.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/CustomerRiskViewRepository.java:L1 | neighbors=[CustomerRiskView, ReactiveMongoRepository, CustomerRiskViewRepository]
- "repository_movementrepository": "MovementRepository.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/MovementRepository.java:L1 | neighbors=[Movement, ReactiveMongoRepository, MovementRepository]
- "service_accountresult": "AccountResult.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountResult.java:L1 | neighbors=[AccountHolderInfo, AccountResult, AccountSignerInfo]
- "service_accountservice_accountservice_buildaccount": ".buildAccount()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L516 | neighbors=[AccountService, .generateAccountNumber(), .createAndSaveAccount()]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\accounts-service\.graphify\description-instructions\batch-001.json

Keep each description factual and concise (one sentence). No markdown, no prose
outside the JSON object. It is acceptable to omit a node if context is
insufficient — but include every node you can ground confidently.

Example answer format:
```json
{
  "node_id_1": "Resolves the configured ontology profile from graphify.yaml.",
  "node_id_2": "Colonel James Barclay, an antagonist in The Crooked Man."
}
```
