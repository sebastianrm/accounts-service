# Node Description Batch 1 of 10

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

- "service_accountservice_accountservice": "AccountService" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L65 | neighbors=[AccountService.java, .buildAccount(), .buildIdempotencyKey(), .buildMovement(), .buildMovementEvent(), .calculateCommission()]
- "messaging_accounteventconsumer_accounteventconsumer": "AccountEventConsumer" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L34 | neighbors=[AccountEventConsumer.java, .buildKey(), .createProcessedEvent(), .createProfileView(), .markViewsDeleted(), .onCardClosed()]
- "model_customerprofileviewtest_customerprofileviewtest": "CustomerProfileViewTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L7 | neighbors=[CustomerProfileViewTest.java, .canOperate_whenActive_returnsTrue(), .canOperate_whenBlocked_returnsFalse(), .canOperate_whenDeleted_returnsFalse(), .isEnterprise_whenEnterprise_returnsTru…, .isEnterprise_whenPersonal_returnsFalse…]
- "reactivemongorepository": "ReactiveMongoRepository" | kind=code-symbol | neighbors=[ProcessedEventRepository.java, ProcessedEventRepository, AccountRepository.java, AccountRepository, CustomerCardViewRepository.java, CustomerCardViewRepository]
- "service_accountservice_accountservice_processmovement": ".processMovement()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L587 | neighbors=[AccountService, .deposit(), .buildMovement(), .buildMovementEvent(), .calculateCommission(), .findById()]
- "model_accounttest_accounttest": "AccountTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountTest.java:L10 | neighbors=[AccountTest.java, .builder_setsAllFields(), .canOperate_whenBlocked_returnsFalse(), .canOperate_whenClosed_returnsFalse(), .canOperate_whenOpen_returnsTrue(), .hasExceededFreeMovements_whenExceeded_…]
- "messaging_accounteventconsumer_accounteventconsumer_processifnew": ".processIfNew()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L146 | neighbors=[AccountEventConsumer, .onCardClosed(), .onCardOpened(), .onCreditOverdue(), .onCustomerDeleted(), .onCustomerRegistered()]
- "repository_accountrepository_accountrepository": "AccountRepository" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/AccountRepository.java:L13 | neighbors=[AccountRepository.java, Account, ReactiveMongoRepository, .countByCustomerIdAndType(), .existsByCustomerIdAndType(), .findAllByCustomerId()]
- "controller_accountcontroller_accountcontroller": "AccountController" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/controller/AccountController.java:L50 | neighbors=[AccountController.java, .closeAccount(), .createAccount(), .deposit(), .findAll(), .findById()]
- "messaging_accounteventconsumer_accounteventconsumer_buildkey": ".buildKey()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L174 | neighbors=[AccountEventConsumer, .onCardClosed(), .onCardOpened(), .onCreditOverdue(), .onCustomerDeleted(), .onCustomerRegistered()]
- "messaging_accounteventconsumer_accounteventconsumer_tocompletable": ".toCompletable()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L138 | neighbors=[AccountEventConsumer, .createProfileView(), .markViewsDeleted(), .onDebitPaymentRequested(), .onTransferRequested(), .updateCardViewClosed()]
- "repository_movementrepository_movementrepository": "MovementRepository" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/MovementRepository.java:L13 | neighbors=[MovementRepository.java, Movement, ReactiveMongoRepository, .countByAccountIdAndCurrentMonth(), .findAllByAccountIdAndOccurredAtBetween…, .findAllByAccountIdOrderByOccurredAtDes…]
- "service_accountservice_accountservice_openaccount": ".openAccount()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L102 | neighbors=[AccountService, .checkAccountLimits(), .checkCreditCardRequirement(), .checkRiskView(), .createAndSaveAccount(), .getProfileViewOrError()]
- "service_accountservice_accountservice_getbalance": ".getBalance()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L154 | neighbors=[AccountService, .closeAccount(), .findById(), .processMovement(), .publishAccountOpenedEvent(), .toAccountResult()]
- "service_accountservice_accountservice_validateandtransfer": ".validateAndTransfer()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L735 | neighbors=[AccountService, .settleTransfer(), .buildMovement(), .findById(), .getBalance(), .invalidateCache()]
- "messaging_accounteventconsumer_accounteventconsumer_tosingle": ".toSingle()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L162 | neighbors=[AccountEventConsumer, .createProfileView(), .markViewsDeleted(), .updateCardViewClosed(), .updateCardViewOpened(), .updateProfileView()]
- "service_accountservice_accountservice_findbyid": ".findById()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L132 | neighbors=[AccountService, .checkAndMarkProcessed(), .closeAccount(), .getBalance(), .processMovement(), .validateAndDebitAccount()]
- "service_accountservice_accountservice_settledebitpayment": ".settleDebitPayment()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L247 | neighbors=[AccountService, .buildIdempotencyKey(), .buildMovementEvent(), .checkAndMarkProcessed(), .publishDebitPaymentSettledEvent(), .publishMovementEvent()]
- "service_accountservice_accountservice_settletransfer": ".settleTransfer()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L291 | neighbors=[AccountService, .buildIdempotencyKey(), .buildMovementEvent(), .checkAndMarkProcessed(), .publishMovementEvent(), .publishTransferSettledEvent()]
- "service_accountservice_accountservice_validateanddebitaccount": ".validateAndDebitAccount()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L706 | neighbors=[AccountService, .settleDebitPayment(), .buildMovement(), .findById(), .getBalance(), .invalidateCache()]
- "account": "Account" | kind=code-symbol | neighbors=[AccountMapper.java, CustomAccountRepository.java, CustomAccountRepositoryImpl.java, AccountRepository.java, AccountRepository, AccountService.java]
- "messaging_accounteventpublisher_accounteventpublisher": "AccountEventPublisher" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventPublisher.java:L21 | neighbors=[AccountEventPublisher.java, .publishClosed(), .publishDebitPaymentSettled(), .publishMovement(), .publishOpened(), .publishTransferSettled()]
- "model_customerprofileview_customerprofileview": "CustomerProfileView" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/CustomerProfileView.java:L21 | neighbors=[CustomerProfileView.java, .canOperate(), .isEnterprise(), .isPersonal(), .isPyme(), .isVip()]
- "movement": "Movement" | kind=code-symbol | neighbors=[AccountMapper.java, CustomAccountRepository.java, CustomAccountRepositoryImpl.java, MovementRepository.java, MovementRepository, AccountService.java]
- "persistence_customaccountrepositoryimpl_customaccountrepositoryimpl": "CustomAccountRepositoryImpl" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/CustomAccountRepositoryImpl.java:L21 | neighbors=[CustomAccountRepositoryImpl.java, CustomAccountRepository, .countMovementsInMonth(), .CustomAccountRepositoryImpl(), .findByCustomerAndTypeAndStatus(), .findMovementsByAccountAndDateRange()]
- "service_accountservice": "AccountService.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L1 | neighbors=[Account, CustomerProfileView, CustomerRiskView, Movement, ProcessedEvent, AccountService]
- "string": "String" | kind=code-symbol | neighbors=[ProcessedEventRepository, AccountRepository, CustomerCardViewRepository, CustomerProfileViewRepository, CustomerRiskViewRepository, MovementRepository]
- "persistence_processedeventrepository_processedeventrepository": "ProcessedEventRepository" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/ProcessedEventRepository.java:L9 | neighbors=[ProcessedEventRepository.java, .findById(), ProcessedEvent, ReactiveMongoRepository, String]
- "repository_customercardviewrepository_customercardviewrepository": "CustomerCardViewRepository" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/CustomerCardViewRepository.java:L10 | neighbors=[CustomerCardViewRepository.java, CustomerCardView, ReactiveMongoRepository, .findByCustomerId(), String]
- "repository_customerprofileviewrepository_customerprofileviewrepository": "CustomerProfileViewRepository" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/CustomerProfileViewRepository.java:L10 | neighbors=[CustomerProfileViewRepository.java, CustomerProfileView, ReactiveMongoRepository, .findByCustomerId(), String]
- "repository_customerriskviewrepository_customerriskviewrepository": "CustomerRiskViewRepository" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/CustomerRiskViewRepository.java:L10 | neighbors=[CustomerRiskViewRepository.java, CustomerRiskView, ReactiveMongoRepository, .findByCustomerId(), String]
- "rest_globalexceptionhandler_globalexceptionhandler": "GlobalExceptionHandler" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/GlobalExceptionHandler.java:L25 | neighbors=[GlobalExceptionHandler.java, .handleBusinessException(), .handleGenericException(), .handleValidationException(), .mapErrorCodeToStatus()]
- "service_accountservice_accountservice_buildmovement": ".buildMovement()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L683 | neighbors=[AccountService, .createAndSaveAccount(), .processMovement(), .validateAndDebitAccount(), .validateAndTransfer()]
- "service_accountservice_accountservice_checkandmarkprocessed": ".checkAndMarkProcessed()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L801 | neighbors=[AccountService, .createProcessedEvent(), .findById(), .settleDebitPayment(), .settleTransfer()]
- "service_accountservice_accountservice_createandsaveaccount": ".createAndSaveAccount()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L497 | neighbors=[AccountService, .buildAccount(), .buildMovement(), .toAccountResult(), .openAccount()]
- "service_accountservice_accountservice_validateaccountformovement": ".validateAccountForMovement()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L633 | neighbors=[AccountService, .processMovement(), .validateTermFixedMovement(), .validateAndDebitAccount(), .validateAndTransfer()]
- "dto_accountrequesttest_accountrequesttest": "AccountRequestTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/interfaces/rest/dto/AccountRequestTest.java:L9 | neighbors=[AccountRequestTest.java, .builder_setsAllFields(), .setters_work(), .toCommand_createsCommand()]
- "exception_businessexception_businessexception": "BusinessException" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/exception/BusinessException.java:L9 | neighbors=[BusinessException.java, .BusinessException(), .getCode(), RuntimeException]
- "messaging_accounteventconsumer_accounteventconsumer_createprofileview": ".createProfileView()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L191 | neighbors=[AccountEventConsumer, .toCompletable(), .toSingle(), .onCustomerRegistered()]
- "messaging_accounteventconsumer_accounteventconsumer_markviewsdeleted": ".markViewsDeleted()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L217 | neighbors=[AccountEventConsumer, .toCompletable(), .toSingle(), .onCustomerDeleted()]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\accounts-service\.graphify\description-instructions\batch-000.json

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
