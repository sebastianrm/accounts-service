# Node Description Batch 4 of 10

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

- "service_accountservice_accountservice_isvalidpersonalsavings": ".isValidPersonalSavings()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L400 | neighbors=[AccountService, .validateCustomerForAccountOpening()]
- "service_accountservice_accountservice_parseaccounttype": ".parseAccountType()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L933 | neighbors=[AccountService, .openAccount()]
- "service_accountservice_accountservice_publishaccountclosedevent": ".publishAccountClosedEvent()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L843 | neighbors=[AccountService, .closeAccount()]
- "service_accountservice_accountservice_publishdebitpaymentsettledevent": ".publishDebitPaymentSettledEvent()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L869 | neighbors=[AccountService, .settleDebitPayment()]
- "service_accountservice_accountservice_publishtransfersettledevent": ".publishTransferSettledEvent()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L880 | neighbors=[AccountService, .settleTransfer()]
- "service_accountservice_accountservice_tomovementresult": ".toMovementResult()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L920 | neighbors=[AccountService, .processMovement()]
- "service_accountservice_accountservice_validateriskfortransfer": ".validateRiskForTransfer()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L785 | neighbors=[AccountService, .validateAndTransfer()]
- "service_accountservice_accountservice_validatetermfixedmovement": ".validateTermFixedMovement()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L656 | neighbors=[AccountService, .validateAccountForMovement()]
- "service_accountservice_accountservice_withdrawal": ".withdrawal()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L188 | neighbors=[AccountService, .processMovement()]
- "service_debitpaymentcommandtest_debitpaymentcommandtest": "DebitPaymentCommandTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/DebitPaymentCommandTest.java:L9 | neighbors=[DebitPaymentCommandTest.java, .builder_setsAllFields()]
- "service_depositcommandtest_depositcommandtest": "DepositCommandTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/DepositCommandTest.java:L9 | neighbors=[DepositCommandTest.java, .builder_setsAllFields()]
- "service_openaccountcommandtest_openaccountcommandtest": "OpenAccountCommandTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/OpenAccountCommandTest.java:L9 | neighbors=[OpenAccountCommandTest.java, .builder_setsAllFields()]
- "service_transfercommandtest_transfercommandtest": "TransferCommandTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/TransferCommandTest.java:L9 | neighbors=[TransferCommandTest.java, .builder_setsAllFields()]
- "service_withdrawalcommandtest_withdrawalcommandtest": "WithdrawalCommandTest" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/WithdrawalCommandTest.java:L9 | neighbors=[WithdrawalCommandTest.java, .builder_setsAllFields()]
- "accounts_accountsserviceapplication": "AccountsServiceApplication.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/AccountsServiceApplication.java:L1 | neighbors=[AccountsServiceApplication]
- "accounts_accountsserviceapplication_accountsserviceapplication_main": ".main()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/AccountsServiceApplication.java:L23 | neighbors=[AccountsServiceApplication]
- "config_cacheproperties": "CacheProperties.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/config/CacheProperties.java:L1 | neighbors=[CacheProperties]
- "config_cacheproperties_cacheproperties": "CacheProperties" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/config/CacheProperties.java:L12 | neighbors=[CacheProperties.java]
- "config_cachepropertiestest": "CachePropertiesTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/infrastructure/config/CachePropertiesTest.java:L1 | neighbors=[CachePropertiesTest]
- "config_cachepropertiestest_cachepropertiestest_defaultvalues": ".defaultValues()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/infrastructure/config/CachePropertiesTest.java:L11 | neighbors=[CachePropertiesTest]
- "config_cachepropertiestest_cachepropertiestest_setters": ".setters()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/infrastructure/config/CachePropertiesTest.java:L19 | neighbors=[CachePropertiesTest]
- "config_openapiconfig": "OpenApiConfig.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/config/OpenApiConfig.java:L1 | neighbors=[OpenApiConfig]
- "config_openapiconfig_openapiconfig_customopenapi": ".customOpenAPI()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/config/OpenApiConfig.java:L18 | neighbors=[OpenApiConfig]
- "config_redisconfig": "RedisConfig.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/config/RedisConfig.java:L1 | neighbors=[RedisConfig]
- "config_redisconfig_redisconfig_objectmapper": ".objectMapper()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/config/RedisConfig.java:L20 | neighbors=[RedisConfig]
- "config_redisconfig_redisconfig_reactivestringredistemplate": ".reactiveStringRedisTemplate()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/config/RedisConfig.java:L15 | neighbors=[RedisConfig]
- "config_resilienceconfig": "ResilienceConfig.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/config/ResilienceConfig.java:L1 | neighbors=[ResilienceConfig]
- "config_resilienceconfig_resilienceconfig_globalcustomconfiguration": ".globalCustomConfiguration()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/config/ResilienceConfig.java:L23 | neighbors=[ResilienceConfig]
- "controller_accountcontroller": "AccountController.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/controller/AccountController.java:L1 | neighbors=[AccountController]
- "controller_accountcontroller_accountcontroller_closeaccount": ".closeAccount()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/controller/AccountController.java:L248 | neighbors=[AccountController]
- "controller_accountcontroller_accountcontroller_createaccount": ".createAccount()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/controller/AccountController.java:L59 | neighbors=[AccountController]
- "controller_accountcontroller_accountcontroller_deposit": ".deposit()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/controller/AccountController.java:L194 | neighbors=[AccountController]
- "controller_accountcontroller_accountcontroller_findall": ".findAll()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/controller/AccountController.java:L79 | neighbors=[AccountController]
- "controller_accountcontroller_accountcontroller_findbyid": ".findById()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/controller/AccountController.java:L126 | neighbors=[AccountController]
- "controller_accountcontroller_accountcontroller_getbalance": ".getBalance()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/controller/AccountController.java:L143 | neighbors=[AccountController]
- "controller_accountcontroller_accountcontroller_getmovements": ".getMovements()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/controller/AccountController.java:L165 | neighbors=[AccountController]
- "controller_accountcontroller_accountcontroller_withdrawal": ".withdrawal()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/controller/AccountController.java:L221 | neighbors=[AccountController]
- "customaccountrepository": "CustomAccountRepository" | kind=code-symbol | neighbors=[CustomAccountRepositoryImpl]
- "dto_accountrequest_accountholderrequest": "AccountHolderRequest" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/AccountRequest.java:L54 | neighbors=[AccountRequest.java]
- "dto_accountrequest_accountrequest_tocommand": ".toCommand()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/AccountRequest.java:L77 | neighbors=[AccountRequest]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\accounts-service\.graphify\description-instructions\batch-003.json

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
