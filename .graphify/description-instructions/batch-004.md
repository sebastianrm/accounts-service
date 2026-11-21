# Node Description Batch 5 of 10

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

- "dto_accountrequest_accountsignerrequest": "AccountSignerRequest" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/AccountRequest.java:L64 | neighbors=[AccountRequest.java]
- "dto_accountrequesttest": "AccountRequestTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/interfaces/rest/dto/AccountRequestTest.java:L1 | neighbors=[AccountRequestTest]
- "dto_accountrequesttest_accountrequesttest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/interfaces/rest/dto/AccountRequestTest.java:L11 | neighbors=[AccountRequestTest]
- "dto_accountrequesttest_accountrequesttest_setters_work": ".setters_work()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/interfaces/rest/dto/AccountRequestTest.java:L26 | neighbors=[AccountRequestTest]
- "dto_accountrequesttest_accountrequesttest_tocommand_createscommand": ".toCommand_createsCommand()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/interfaces/rest/dto/AccountRequestTest.java:L38 | neighbors=[AccountRequestTest]
- "dto_accountresponse_accountholderresponse": "AccountHolderResponse" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/AccountResponse.java:L64 | neighbors=[AccountResponse.java]
- "dto_accountresponse_accountresponse_from": ".from()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/AccountResponse.java:L37 | neighbors=[AccountResponse]
- "dto_accountresponse_accountsignerresponse": "AccountSignerResponse" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/AccountResponse.java:L74 | neighbors=[AccountResponse.java]
- "dto_accountresponsetest": "AccountResponseTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/interfaces/rest/dto/AccountResponseTest.java:L1 | neighbors=[AccountResponseTest]
- "dto_accountresponsetest_accountresponsetest_from_convertsaccountresulttoresponse": ".from_convertsAccountResultToResponse()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/interfaces/rest/dto/AccountResponseTest.java:L13 | neighbors=[AccountResponseTest]
- "dto_apierror": "ApiError.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/ApiError.java:L1 | neighbors=[ApiError]
- "dto_apierror_apierror": "ApiError" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/ApiError.java:L16 | neighbors=[ApiError.java]
- "dto_balanceresponse": "BalanceResponse.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/BalanceResponse.java:L1 | neighbors=[BalanceResponse]
- "dto_balanceresponse_balanceresponse": "BalanceResponse" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/BalanceResponse.java:L13 | neighbors=[BalanceResponse.java]
- "dto_depositrequest": "DepositRequest.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/DepositRequest.java:L1 | neighbors=[DepositRequest]
- "dto_depositrequest_depositrequest": "DepositRequest" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/DepositRequest.java:L16 | neighbors=[DepositRequest.java]
- "dto_movementresponse": "MovementResponse.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/MovementResponse.java:L1 | neighbors=[MovementResponse]
- "dto_movementresponse_movementresponse_from": ".from()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/MovementResponse.java:L30 | neighbors=[MovementResponse]
- "dto_movementresponsetest": "MovementResponseTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/interfaces/rest/dto/MovementResponseTest.java:L1 | neighbors=[MovementResponseTest]
- "dto_movementresponsetest_movementresponsetest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/interfaces/rest/dto/MovementResponseTest.java:L12 | neighbors=[MovementResponseTest]
- "dto_pageresponse": "PageResponse.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/PageResponse.java:L1 | neighbors=[PageResponse]
- "dto_pageresponse_pageresponse": "PageResponse" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/PageResponse.java:L13 | neighbors=[PageResponse.java]
- "dto_withdrawalrequest": "WithdrawalRequest.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/WithdrawalRequest.java:L1 | neighbors=[WithdrawalRequest]
- "dto_withdrawalrequest_withdrawalrequest": "WithdrawalRequest" | kind=code-symbol | source=src/main/java/com/yanki/accounts/interfaces/rest/dto/WithdrawalRequest.java:L16 | neighbors=[WithdrawalRequest.java]
- "event_accountclosedevent": "AccountClosedEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/AccountClosedEvent.java:L1 | neighbors=[AccountClosedEvent]
- "event_accountclosedevent_accountclosedevent": "AccountClosedEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/AccountClosedEvent.java:L16 | neighbors=[AccountClosedEvent.java]
- "event_accountclosedeventtest": "AccountClosedEventTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/AccountClosedEventTest.java:L1 | neighbors=[AccountClosedEventTest]
- "event_accountclosedeventtest_accountclosedeventtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/AccountClosedEventTest.java:L11 | neighbors=[AccountClosedEventTest]
- "event_accountmovementregisteredevent": "AccountMovementRegisteredEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/AccountMovementRegisteredEvent.java:L1 | neighbors=[AccountMovementRegisteredEvent]
- "event_accountmovementregisteredevent_accountmovementregisteredevent": "AccountMovementRegisteredEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/AccountMovementRegisteredEvent.java:L17 | neighbors=[AccountMovementRegisteredEvent.java]
- "event_accountmovementregisteredeventtest": "AccountMovementRegisteredEventTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/AccountMovementRegisteredEventTest.java:L1 | neighbors=[AccountMovementRegisteredEventTest]
- "event_accountmovementregisteredeventtest_accountmovementregisteredeventtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/AccountMovementRegisteredEventTest.java:L12 | neighbors=[AccountMovementRegisteredEventTest]
- "event_accountopenedevent": "AccountOpenedEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/AccountOpenedEvent.java:L1 | neighbors=[AccountOpenedEvent]
- "event_accountopenedevent_accountopenedevent": "AccountOpenedEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/AccountOpenedEvent.java:L17 | neighbors=[AccountOpenedEvent.java]
- "event_accountopenedeventtest": "AccountOpenedEventTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/AccountOpenedEventTest.java:L1 | neighbors=[AccountOpenedEventTest]
- "event_accountopenedeventtest_accountopenedeventtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/AccountOpenedEventTest.java:L12 | neighbors=[AccountOpenedEventTest]
- "event_cardclosedevent": "CardClosedEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/CardClosedEvent.java:L1 | neighbors=[CardClosedEvent]
- "event_cardclosedevent_cardclosedevent": "CardClosedEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/CardClosedEvent.java:L15 | neighbors=[CardClosedEvent.java]
- "event_cardclosedeventtest": "CardClosedEventTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/CardClosedEventTest.java:L1 | neighbors=[CardClosedEventTest]
- "event_cardclosedeventtest_cardclosedeventtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/CardClosedEventTest.java:L11 | neighbors=[CardClosedEventTest]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\accounts-service\.graphify\description-instructions\batch-004.json

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
