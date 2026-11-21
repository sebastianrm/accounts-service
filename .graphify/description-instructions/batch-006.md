# Node Description Batch 7 of 10

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

- "messaging_accounteventpublisher_accounteventpublisher_publishmovement": ".publishMovement()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventPublisher.java:L59 | neighbors=[AccountEventPublisher]
- "messaging_accounteventpublisher_accounteventpublisher_publishopened": ".publishOpened()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventPublisher.java:L35 | neighbors=[AccountEventPublisher]
- "messaging_accounteventpublisher_accounteventpublisher_publishtransfersettled": ".publishTransferSettled()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/messaging/AccountEventPublisher.java:L83 | neighbors=[AccountEventPublisher]
- "model_account": "Account.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/Account.java:L1 | neighbors=[Account]
- "model_account_account_canoperate": ".canOperate()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/Account.java:L99 | neighbors=[Account]
- "model_account_account_hasexceededfreemovements": ".hasExceededFreeMovements()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/Account.java:L123 | neighbors=[Account]
- "model_account_account_incrementmovementcount": ".incrementMovementCount()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/Account.java:L108 | neighbors=[Account]
- "model_accountholder": "AccountHolder.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/AccountHolder.java:L1 | neighbors=[AccountHolder]
- "model_accountholder_accountholder": "AccountHolder" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/AccountHolder.java:L15 | neighbors=[AccountHolder.java]
- "model_accountholdertest": "AccountHolderTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountHolderTest.java:L1 | neighbors=[AccountHolderTest]
- "model_accountholdertest_accountholdertest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountHolderTest.java:L9 | neighbors=[AccountHolderTest]
- "model_accountsigner": "AccountSigner.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/AccountSigner.java:L1 | neighbors=[AccountSigner]
- "model_accountsigner_accountsigner": "AccountSigner" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/AccountSigner.java:L15 | neighbors=[AccountSigner.java]
- "model_accountsignertest": "AccountSignerTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountSignerTest.java:L1 | neighbors=[AccountSignerTest]
- "model_accountsignertest_accountsignertest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountSignerTest.java:L9 | neighbors=[AccountSignerTest]
- "model_accounttest": "AccountTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountTest.java:L1 | neighbors=[AccountTest]
- "model_accounttest_accounttest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountTest.java:L107 | neighbors=[AccountTest]
- "model_accounttest_accounttest_canoperate_whenblocked_returnsfalse": ".canOperate_whenBlocked_returnsFalse()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountTest.java:L30 | neighbors=[AccountTest]
- "model_accounttest_accounttest_canoperate_whenclosed_returnsfalse": ".canOperate_whenClosed_returnsFalse()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountTest.java:L21 | neighbors=[AccountTest]
- "model_accounttest_accounttest_canoperate_whenopen_returnstrue": ".canOperate_whenOpen_returnsTrue()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountTest.java:L12 | neighbors=[AccountTest]
- "model_accounttest_accounttest_hasexceededfreemovements_whenexceeded_returnstrue": ".hasExceededFreeMovements_whenExceeded_returnsTrue()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountTest.java:L67 | neighbors=[AccountTest]
- "model_accounttest_accounttest_hasexceededfreemovements_whennotexceeded_returnsfalse": ".hasExceededFreeMovements_whenNotExceeded_returnsFalse()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountTest.java:L77 | neighbors=[AccountTest]
- "model_accounttest_accounttest_hasexceededfreemovements_whennullcount_returnsfalse": ".hasExceededFreeMovements_whenNullCount_returnsFalse()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountTest.java:L97 | neighbors=[AccountTest]
- "model_accounttest_accounttest_hasexceededfreemovements_whennulllimit_returnsfalse": ".hasExceededFreeMovements_whenNullLimit_returnsFalse()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountTest.java:L87 | neighbors=[AccountTest]
- "model_accounttest_accounttest_incrementmovementcount_newmonth_resetscounter": ".incrementMovementCount_newMonth_resetsCounter()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountTest.java:L53 | neighbors=[AccountTest]
- "model_accounttest_accounttest_incrementmovementcount_samemonth_incrementscounter": ".incrementMovementCount_sameMonth_incrementsCounter()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/AccountTest.java:L39 | neighbors=[AccountTest]
- "model_customercardview": "CustomerCardView.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/CustomerCardView.java:L1 | neighbors=[CustomerCardView]
- "model_customercardview_customercardview": "CustomerCardView" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/CustomerCardView.java:L20 | neighbors=[CustomerCardView.java]
- "model_customercardviewtest": "CustomerCardViewTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerCardViewTest.java:L1 | neighbors=[CustomerCardViewTest]
- "model_customercardviewtest_customercardviewtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerCardViewTest.java:L9 | neighbors=[CustomerCardViewTest]
- "model_customerprofileview": "CustomerProfileView.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/CustomerProfileView.java:L1 | neighbors=[CustomerProfileView]
- "model_customerprofileview_customerprofileview_canoperate": ".canOperate()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/CustomerProfileView.java:L60 | neighbors=[CustomerProfileView]
- "model_customerprofileview_customerprofileview_isenterprise": ".isEnterprise()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/CustomerProfileView.java:L74 | neighbors=[CustomerProfileView]
- "model_customerprofileview_customerprofileview_ispersonal": ".isPersonal()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/CustomerProfileView.java:L67 | neighbors=[CustomerProfileView]
- "model_customerprofileview_customerprofileview_ispyme": ".isPyme()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/CustomerProfileView.java:L88 | neighbors=[CustomerProfileView]
- "model_customerprofileview_customerprofileview_isvip": ".isVip()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/CustomerProfileView.java:L81 | neighbors=[CustomerProfileView]
- "model_customerprofileviewtest": "CustomerProfileViewTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L1 | neighbors=[CustomerProfileViewTest]
- "model_customerprofileviewtest_customerprofileviewtest_canoperate_whenactive_returnstrue": ".canOperate_whenActive_returnsTrue()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L9 | neighbors=[CustomerProfileViewTest]
- "model_customerprofileviewtest_customerprofileviewtest_canoperate_whenblocked_returnsfalse": ".canOperate_whenBlocked_returnsFalse()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L18 | neighbors=[CustomerProfileViewTest]
- "model_customerprofileviewtest_customerprofileviewtest_canoperate_whendeleted_returnsfalse": ".canOperate_whenDeleted_returnsFalse()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L27 | neighbors=[CustomerProfileViewTest]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\accounts-service\.graphify\description-instructions\batch-006.json

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
