# Node Description Batch 9 of 10

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

- "repository_movementrepository_movementrepository_countbyaccountidandcurrentmonth": ".countByAccountIdAndCurrentMonth()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/MovementRepository.java:L34 | neighbors=[MovementRepository]
- "repository_movementrepository_movementrepository_findallbyaccountidandoccurredatbetween": ".findAllByAccountIdAndOccurredAtBetween()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/MovementRepository.java:L23 | neighbors=[MovementRepository]
- "repository_movementrepository_movementrepository_findallbyaccountidorderbyoccurredatdesc": ".findAllByAccountIdOrderByOccurredAtDesc()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/MovementRepository.java:L18 | neighbors=[MovementRepository]
- "repository_movementrepository_movementrepository_findallbycustomeridorderbyoccurredatdesc": ".findAllByCustomerIdOrderByOccurredAtDesc()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/MovementRepository.java:L29 | neighbors=[MovementRepository]
- "repository_movementrepository_movementrepository_findfirstbyaccountidorderbyoccurredatdesc": ".findFirstByAccountIdOrderByOccurredAtDesc()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/MovementRepository.java:L39 | neighbors=[MovementRepository]
- "rest_globalexceptionhandler": "GlobalExceptionHandler.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/GlobalExceptionHandler.java:L1 | neighbors=[GlobalExceptionHandler]
- "rest_globalexceptionhandler_globalexceptionhandler_handlegenericexception": ".handleGenericException()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/GlobalExceptionHandler.java:L68 | neighbors=[GlobalExceptionHandler]
- "rest_globalexceptionhandler_globalexceptionhandler_handlevalidationexception": ".handleValidationException()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/GlobalExceptionHandler.java:L46 | neighbors=[GlobalExceptionHandler]
- "runtimeexception": "RuntimeException" | kind=code-symbol | neighbors=[BusinessException]
- "service_accountresult_accountholderinfo": "AccountHolderInfo" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountResult.java:L55 | neighbors=[AccountResult.java]
- "service_accountresult_accountresult": "AccountResult" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountResult.java:L15 | neighbors=[AccountResult.java]
- "service_accountresult_accountsignerinfo": "AccountSignerInfo" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountResult.java:L66 | neighbors=[AccountResult.java]
- "service_accountservice_accountservice_findbycustomer": ".findByCustomer()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L144 | neighbors=[AccountService]
- "service_accountservice_accountservice_getcardviewrepository": ".getCardViewRepository()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L340 | neighbors=[AccountService]
- "service_accountservice_accountservice_getprofileviewrepository": ".getProfileViewRepository()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L336 | neighbors=[AccountService]
- "service_accountservice_accountservice_getriskviewrepository": ".getRiskViewRepository()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/AccountService.java:L344 | neighbors=[AccountService]
- "service_debitpaymentcommand": "DebitPaymentCommand.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/DebitPaymentCommand.java:L1 | neighbors=[DebitPaymentCommand]
- "service_debitpaymentcommand_debitpaymentcommand": "DebitPaymentCommand" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/DebitPaymentCommand.java:L14 | neighbors=[DebitPaymentCommand.java]
- "service_debitpaymentcommandtest": "DebitPaymentCommandTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/DebitPaymentCommandTest.java:L1 | neighbors=[DebitPaymentCommandTest]
- "service_debitpaymentcommandtest_debitpaymentcommandtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/DebitPaymentCommandTest.java:L11 | neighbors=[DebitPaymentCommandTest]
- "service_debitpaymentresult": "DebitPaymentResult.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/DebitPaymentResult.java:L1 | neighbors=[DebitPaymentResult]
- "service_debitpaymentresult_debitpaymentresult": "DebitPaymentResult" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/DebitPaymentResult.java:L12 | neighbors=[DebitPaymentResult.java]
- "service_depositcommand": "DepositCommand.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/DepositCommand.java:L1 | neighbors=[DepositCommand]
- "service_depositcommand_depositcommand": "DepositCommand" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/DepositCommand.java:L13 | neighbors=[DepositCommand.java]
- "service_depositcommandtest": "DepositCommandTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/DepositCommandTest.java:L1 | neighbors=[DepositCommandTest]
- "service_depositcommandtest_depositcommandtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/DepositCommandTest.java:L11 | neighbors=[DepositCommandTest]
- "service_movementresult": "MovementResult.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/MovementResult.java:L1 | neighbors=[MovementResult]
- "service_movementresult_movementresult": "MovementResult" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/MovementResult.java:L14 | neighbors=[MovementResult.java]
- "service_openaccountcommand_accountholdercommand": "AccountHolderCommand" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/OpenAccountCommand.java:L54 | neighbors=[OpenAccountCommand.java]
- "service_openaccountcommand_accountsignercommand": "AccountSignerCommand" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/OpenAccountCommand.java:L68 | neighbors=[OpenAccountCommand.java]
- "service_openaccountcommand_openaccountcommand": "OpenAccountCommand" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/OpenAccountCommand.java:L14 | neighbors=[OpenAccountCommand.java]
- "service_openaccountcommandtest": "OpenAccountCommandTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/OpenAccountCommandTest.java:L1 | neighbors=[OpenAccountCommandTest]
- "service_openaccountcommandtest_openaccountcommandtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/OpenAccountCommandTest.java:L11 | neighbors=[OpenAccountCommandTest]
- "service_transfercommand": "TransferCommand.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/TransferCommand.java:L1 | neighbors=[TransferCommand]
- "service_transfercommand_transfercommand": "TransferCommand" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/TransferCommand.java:L14 | neighbors=[TransferCommand.java]
- "service_transfercommandtest": "TransferCommandTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/TransferCommandTest.java:L1 | neighbors=[TransferCommandTest]
- "service_transfercommandtest_transfercommandtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/service/TransferCommandTest.java:L11 | neighbors=[TransferCommandTest]
- "service_transferresult": "TransferResult.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/TransferResult.java:L1 | neighbors=[TransferResult]
- "service_transferresult_transferresult": "TransferResult" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/TransferResult.java:L12 | neighbors=[TransferResult.java]
- "service_withdrawalcommand": "WithdrawalCommand.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/service/WithdrawalCommand.java:L1 | neighbors=[WithdrawalCommand]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\accounts-service\.graphify\description-instructions\batch-008.json

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
