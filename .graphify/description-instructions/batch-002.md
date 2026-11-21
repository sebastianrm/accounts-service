# Node Description Batch 3 of 10

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

- "service_accountservice_accountservice_buildidempotencykey": ".buildIdempotencyKey()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L824 | neighbors=[AccountService, .settleDebitPayment(), .settleTransfer()]
- "service_accountservice_accountservice_publishaccountopenedevent": ".publishAccountOpenedEvent()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L831 | neighbors=[AccountService, .openAccount(), .getBalance()]
- "service_accountservice_accountservice_toaccountresult": ".toAccountResult()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L892 | neighbors=[AccountService, .createAndSaveAccount(), .getBalance()]
- "service_openaccountcommand": "OpenAccountCommand.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/OpenAccountCommand.java:L1 | neighbors=[AccountHolderCommand, AccountSignerCommand, OpenAccountCommand]
- "accounts_accountsserviceapplication_accountsserviceapplication": "AccountsServiceApplication" | kind=code-symbol | source=src/main/java/com/yanki/accounts/AccountsServiceApplication.java:L15 | neighbors=[AccountsServiceApplication.java, .main()]
- "config_openapiconfig_openapiconfig": "OpenApiConfig" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/config/OpenApiConfig.java:L15 | neighbors=[OpenApiConfig.java, .customOpenAPI()]
- "config_resilienceconfig_resilienceconfig": "ResilienceConfig" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/config/ResilienceConfig.java:L19 | neighbors=[ResilienceConfig.java, .globalCustomConfiguration()]
- "customercardview": "CustomerCardView" | kind=code-symbol | neighbors=[CustomerCardViewRepository.java, CustomerCardViewRepository]
- "dto_accountrequest_accountrequest": "AccountRequest" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/AccountRequest.java:L20 | neighbors=[AccountRequest.java, .toCommand()]
- "dto_accountresponse_accountresponse": "AccountResponse" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/AccountResponse.java:L16 | neighbors=[AccountResponse.java, .from()]
- "dto_accountresponsetest_accountresponsetest": "AccountResponseTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/interfaces/rest/dto/AccountResponseTest.java:L11 | neighbors=[AccountResponseTest.java, .from_convertsAccountResultToResponse()]
- "dto_movementresponse_movementresponse": "MovementResponse" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/MovementResponse.java:L15 | neighbors=[MovementResponse.java, .from()]
- "dto_movementresponsetest_movementresponsetest": "MovementResponseTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/interfaces/rest/dto/MovementResponseTest.java:L10 | neighbors=[MovementResponseTest.java, .builder_setsAllFields()]
- "event_accountclosedeventtest_accountclosedeventtest": "AccountClosedEventTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/AccountClosedEventTest.java:L9 | neighbors=[AccountClosedEventTest.java, .builder_setsAllFields()]
- "event_accountmovementregisteredeventtest_accountmovementregisteredeventtest": "AccountMovementRegisteredEventTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/AccountMovementRegisteredEventTest.java:L10 | neighbors=[AccountMovementRegisteredEventTest.java, .builder_setsAllFields()]
- "event_accountopenedeventtest_accountopenedeventtest": "AccountOpenedEventTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/AccountOpenedEventTest.java:L10 | neighbors=[AccountOpenedEventTest.java, .builder_setsAllFields()]
- "event_cardclosedeventtest_cardclosedeventtest": "CardClosedEventTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/CardClosedEventTest.java:L9 | neighbors=[CardClosedEventTest.java, .builder_setsAllFields()]
- "event_debitpaymentsettledeventtest_debitpaymentsettledeventtest": "DebitPaymentSettledEventTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/DebitPaymentSettledEventTest.java:L9 | neighbors=[DebitPaymentSettledEventTest.java, .builder_setsAllFields()]
- "event_topics_topics": "Topics" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/Topics.java:L6 | neighbors=[Topics.java, .Topics()]
- "event_topicstest_topicstest": "TopicsTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/TopicsTest.java:L7 | neighbors=[TopicsTest.java, .allTopicsAreDefined()]
- "event_transfersettledeventtest_transfersettledeventtest": "TransferSettledEventTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/TransferSettledEventTest.java:L9 | neighbors=[TransferSettledEventTest.java, .builder_setsAllFields()]
- "exception_errorcodes_errorcodes": "ErrorCodes" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/exception/ErrorCodes.java:L9 | neighbors=[ErrorCodes.java, .ErrorCodes()]
- "exception_errorcodestest_errorcodestest": "ErrorCodesTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/exception/ErrorCodesTest.java:L7 | neighbors=[ErrorCodesTest.java, .allCodesAreNotNull()]
- "messaging_accounteventconsumer": "AccountEventConsumer.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L1 | neighbors=[AccountEventConsumer, ProcessedEvent]
- "messaging_accounteventconsumer_accounteventconsumer_createprocessedevent": ".createProcessedEvent()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L178 | neighbors=[AccountEventConsumer, .processIfNew()]
- "model_accountholdertest_accountholdertest": "AccountHolderTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountHolderTest.java:L7 | neighbors=[AccountHolderTest.java, .builder_setsAllFields()]
- "model_accountsignertest_accountsignertest": "AccountSignerTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountSignerTest.java:L7 | neighbors=[AccountSignerTest.java, .builder_setsAllFields()]
- "model_customercardviewtest_customercardviewtest": "CustomerCardViewTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerCardViewTest.java:L7 | neighbors=[CustomerCardViewTest.java, .builder_setsAllFields()]
- "persistence_processedeventrepository": "ProcessedEventRepository.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/ProcessedEventRepository.java:L1 | neighbors=[ProcessedEventRepository, ReactiveMongoRepository]
- "rest_globalexceptionhandler_globalexceptionhandler_handlebusinessexception": ".handleBusinessException()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/GlobalExceptionHandler.java:L29 | neighbors=[GlobalExceptionHandler, .mapErrorCodeToStatus()]
- "rest_globalexceptionhandler_globalexceptionhandler_maperrorcodetostatus": ".mapErrorCodeToStatus()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/GlobalExceptionHandler.java:L87 | neighbors=[GlobalExceptionHandler, .handleBusinessException()]
- "service_accountservice_accountservice_calculatecommission": ".calculateCommission()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L673 | neighbors=[AccountService, .processMovement()]
- "service_accountservice_accountservice_checkaccountlimits": ".checkAccountLimits()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L455 | neighbors=[AccountService, .openAccount()]
- "service_accountservice_accountservice_checkcreditcardrequirement": ".checkCreditCardRequirement()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L419 | neighbors=[AccountService, .openAccount()]
- "service_accountservice_accountservice_checkriskview": ".checkRiskView()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L410 | neighbors=[AccountService, .openAccount()]
- "service_accountservice_accountservice_createprocessedevent": ".createProcessedEvent()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L813 | neighbors=[AccountService, .checkAndMarkProcessed()]
- "service_accountservice_accountservice_deposit": ".deposit()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L176 | neighbors=[AccountService, .processMovement()]
- "service_accountservice_accountservice_generateaccountnumber": ".generateAccountNumber()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L581 | neighbors=[AccountService, .buildAccount()]
- "service_accountservice_accountservice_getprofilevieworerror": ".getProfileViewOrError()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L350 | neighbors=[AccountService, .openAccount()]
- "service_accountservice_accountservice_isvalidpersonalcurrent": ".isValidPersonalCurrent()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L405 | neighbors=[AccountService, .validateCustomerForAccountOpening()]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\accounts-service\.graphify\description-instructions\batch-002.json

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
