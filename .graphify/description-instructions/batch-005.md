# Node Description Batch 6 of 10

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

- "event_cardopenedevent": "CardOpenedEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/CardOpenedEvent.java:L1 | neighbors=[CardOpenedEvent]
- "event_cardopenedevent_cardopenedevent": "CardOpenedEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/CardOpenedEvent.java:L15 | neighbors=[CardOpenedEvent.java]
- "event_creditoverdueevent": "CreditOverdueEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/CreditOverdueEvent.java:L1 | neighbors=[CreditOverdueEvent]
- "event_creditoverdueevent_creditoverdueevent": "CreditOverdueEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/CreditOverdueEvent.java:L15 | neighbors=[CreditOverdueEvent.java]
- "event_customerdeletedevent": "CustomerDeletedEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/CustomerDeletedEvent.java:L1 | neighbors=[CustomerDeletedEvent]
- "event_customerdeletedevent_customerdeletedevent": "CustomerDeletedEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/CustomerDeletedEvent.java:L14 | neighbors=[CustomerDeletedEvent.java]
- "event_customerregisteredevent": "CustomerRegisteredEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/CustomerRegisteredEvent.java:L1 | neighbors=[CustomerRegisteredEvent]
- "event_customerregisteredevent_customerregisteredevent": "CustomerRegisteredEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/CustomerRegisteredEvent.java:L15 | neighbors=[CustomerRegisteredEvent.java]
- "event_customerupdatedevent": "CustomerUpdatedEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/CustomerUpdatedEvent.java:L1 | neighbors=[CustomerUpdatedEvent]
- "event_customerupdatedevent_customerupdatedevent": "CustomerUpdatedEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/CustomerUpdatedEvent.java:L14 | neighbors=[CustomerUpdatedEvent.java]
- "event_debitpaymentrequestedevent": "DebitPaymentRequestedEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/DebitPaymentRequestedEvent.java:L1 | neighbors=[DebitPaymentRequestedEvent]
- "event_debitpaymentrequestedevent_debitpaymentrequestedevent": "DebitPaymentRequestedEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/DebitPaymentRequestedEvent.java:L15 | neighbors=[DebitPaymentRequestedEvent.java]
- "event_debitpaymentsettledevent": "DebitPaymentSettledEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/DebitPaymentSettledEvent.java:L1 | neighbors=[DebitPaymentSettledEvent]
- "event_debitpaymentsettledevent_debitpaymentsettledevent": "DebitPaymentSettledEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/DebitPaymentSettledEvent.java:L16 | neighbors=[DebitPaymentSettledEvent.java]
- "event_debitpaymentsettledeventtest": "DebitPaymentSettledEventTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/DebitPaymentSettledEventTest.java:L1 | neighbors=[DebitPaymentSettledEventTest]
- "event_debitpaymentsettledeventtest_debitpaymentsettledeventtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/DebitPaymentSettledEventTest.java:L11 | neighbors=[DebitPaymentSettledEventTest]
- "event_topics": "Topics.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/Topics.java:L1 | neighbors=[Topics]
- "event_topics_topics_topics": ".Topics()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/Topics.java:L25 | neighbors=[Topics]
- "event_topicstest": "TopicsTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/TopicsTest.java:L1 | neighbors=[TopicsTest]
- "event_topicstest_topicstest_alltopicsaredefined": ".allTopicsAreDefined()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/TopicsTest.java:L9 | neighbors=[TopicsTest]
- "event_transferrequestedevent": "TransferRequestedEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/TransferRequestedEvent.java:L1 | neighbors=[TransferRequestedEvent]
- "event_transferrequestedevent_transferrequestedevent": "TransferRequestedEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/TransferRequestedEvent.java:L15 | neighbors=[TransferRequestedEvent.java]
- "event_transfersettledevent": "TransferSettledEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/TransferSettledEvent.java:L1 | neighbors=[TransferSettledEvent]
- "event_transfersettledevent_transfersettledevent": "TransferSettledEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/event/TransferSettledEvent.java:L16 | neighbors=[TransferSettledEvent.java]
- "event_transfersettledeventtest": "TransferSettledEventTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/TransferSettledEventTest.java:L1 | neighbors=[TransferSettledEventTest]
- "event_transfersettledeventtest_transfersettledeventtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/event/TransferSettledEventTest.java:L11 | neighbors=[TransferSettledEventTest]
- "exception_businessexception": "BusinessException.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/exception/BusinessException.java:L1 | neighbors=[BusinessException]
- "exception_businessexception_businessexception_businessexception": ".BusinessException()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/exception/BusinessException.java:L13 | neighbors=[BusinessException]
- "exception_businessexception_businessexception_getcode": ".getCode()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/exception/BusinessException.java:L18 | neighbors=[BusinessException]
- "exception_businessexceptiontest": "BusinessExceptionTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/exception/BusinessExceptionTest.java:L1 | neighbors=[BusinessExceptionTest]
- "exception_businessexceptiontest_businessexceptiontest_constructor_setscodeandmessage": ".constructor_setsCodeAndMessage()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/exception/BusinessExceptionTest.java:L9 | neighbors=[BusinessExceptionTest]
- "exception_businessexceptiontest_businessexceptiontest_isruntimeexception": ".isRuntimeException()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/exception/BusinessExceptionTest.java:L17 | neighbors=[BusinessExceptionTest]
- "exception_errorcodes": "ErrorCodes.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/exception/ErrorCodes.java:L1 | neighbors=[ErrorCodes]
- "exception_errorcodes_errorcodes_errorcodes": ".ErrorCodes()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/exception/ErrorCodes.java:L49 | neighbors=[ErrorCodes]
- "exception_errorcodestest": "ErrorCodesTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/exception/ErrorCodesTest.java:L1 | neighbors=[ErrorCodesTest]
- "exception_errorcodestest_errorcodestest_allcodesarenotnull": ".allCodesAreNotNull()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/exception/ErrorCodesTest.java:L9 | neighbors=[ErrorCodesTest]
- "messaging_accounteventconsumer_accounteventconsumer_toflowable": ".toFlowable()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventConsumer.java:L166 | neighbors=[AccountEventConsumer]
- "messaging_accounteventpublisher": "AccountEventPublisher.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventPublisher.java:L1 | neighbors=[AccountEventPublisher]
- "messaging_accounteventpublisher_accounteventpublisher_publishclosed": ".publishClosed()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventPublisher.java:L47 | neighbors=[AccountEventPublisher]
- "messaging_accounteventpublisher_accounteventpublisher_publishdebitpaymentsettled": ".publishDebitPaymentSettled()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventPublisher.java:L71 | neighbors=[AccountEventPublisher]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\accounts-service\.graphify\description-instructions\batch-005.json

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
