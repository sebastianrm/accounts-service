# Node Description Batch 8 of 10

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

- "model_customerprofileviewtest_customerprofileviewtest_isenterprise_whenenterprise_returnstrue": ".isEnterprise_whenEnterprise_returnsTrue()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L54 | neighbors=[CustomerProfileViewTest]
- "model_customerprofileviewtest_customerprofileviewtest_isenterprise_whenpersonal_returnsfalse": ".isEnterprise_whenPersonal_returnsFalse()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L63 | neighbors=[CustomerProfileViewTest]
- "model_customerprofileviewtest_customerprofileviewtest_ispersonal_whenenterprise_returnsfalse": ".isPersonal_whenEnterprise_returnsFalse()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L45 | neighbors=[CustomerProfileViewTest]
- "model_customerprofileviewtest_customerprofileviewtest_ispersonal_whenpersonal_returnstrue": ".isPersonal_whenPersonal_returnsTrue()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L36 | neighbors=[CustomerProfileViewTest]
- "model_customerprofileviewtest_customerprofileviewtest_ispyme_whenpyme_returnstrue": ".isPyme_whenPyme_returnsTrue()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L90 | neighbors=[CustomerProfileViewTest]
- "model_customerprofileviewtest_customerprofileviewtest_ispyme_whenstandard_returnsfalse": ".isPyme_whenStandard_returnsFalse()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L99 | neighbors=[CustomerProfileViewTest]
- "model_customerprofileviewtest_customerprofileviewtest_isvip_whenstandard_returnsfalse": ".isVip_whenStandard_returnsFalse()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L81 | neighbors=[CustomerProfileViewTest]
- "model_customerprofileviewtest_customerprofileviewtest_isvip_whenvip_returnstrue": ".isVip_whenVip_returnsTrue()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerProfileViewTest.java:L72 | neighbors=[CustomerProfileViewTest]
- "model_customerriskview": "CustomerRiskView.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/CustomerRiskView.java:L1 | neighbors=[CustomerRiskView]
- "model_customerriskview_customerriskview": "CustomerRiskView" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/CustomerRiskView.java:L21 | neighbors=[CustomerRiskView.java]
- "model_customerriskviewtest": "CustomerRiskViewTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerRiskViewTest.java:L1 | neighbors=[CustomerRiskViewTest]
- "model_customerriskviewtest_customerriskviewtest_builder_nooverduedebt": ".builder_noOverdueDebt()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerRiskViewTest.java:L24 | neighbors=[CustomerRiskViewTest]
- "model_customerriskviewtest_customerriskviewtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/CustomerRiskViewTest.java:L11 | neighbors=[CustomerRiskViewTest]
- "model_movement": "Movement.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/Movement.java:L1 | neighbors=[Movement]
- "model_movement_movement": "Movement" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/model/Movement.java:L20 | neighbors=[Movement.java]
- "model_movementtest": "MovementTest.java" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/MovementTest.java:L1 | neighbors=[MovementTest]
- "model_movementtest_movementtest_builder_setsallfields": ".builder_setsAllFields()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/MovementTest.java:L9 | neighbors=[MovementTest]
- "model_movementtest_movementtest_movementtype_enumvalues": ".movementType_enumValues()" | kind=code-symbol | source=src/test/java/com/yanki/accounts/domain/model/MovementTest.java:L31 | neighbors=[MovementTest]
- "persistence_accountmapper_accountmapper_toaccount": ".toAccount()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/AccountMapper.java:L25 | neighbors=[AccountMapper]
- "persistence_accountmapper_accountmapper_todocument": ".toDocument()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/AccountMapper.java:L33 | neighbors=[AccountMapper]
- "persistence_accountmapper_accountmapper_tomovement": ".toMovement()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/AccountMapper.java:L40 | neighbors=[AccountMapper]
- "persistence_customaccountrepository_customaccountrepository_countmovementsinmonth": ".countMovementsInMonth()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/CustomAccountRepository.java:L18 | neighbors=[CustomAccountRepository]
- "persistence_customaccountrepository_customaccountrepository_findbycustomerandtypeandstatus": ".findByCustomerAndTypeAndStatus()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/CustomAccountRepository.java:L16 | neighbors=[CustomAccountRepository]
- "persistence_customaccountrepository_customaccountrepository_findmovementsbyaccountanddaterange": ".findMovementsByAccountAndDateRange()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/CustomAccountRepository.java:L20 | neighbors=[CustomAccountRepository]
- "persistence_customaccountrepositoryimpl_customaccountrepositoryimpl_countmovementsinmonth": ".countMovementsInMonth()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/CustomAccountRepositoryImpl.java:L38 | neighbors=[CustomAccountRepositoryImpl]
- "persistence_customaccountrepositoryimpl_customaccountrepositoryimpl_customaccountrepositoryimpl": ".CustomAccountRepositoryImpl()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/CustomAccountRepositoryImpl.java:L26 | neighbors=[CustomAccountRepositoryImpl]
- "persistence_customaccountrepositoryimpl_customaccountrepositoryimpl_findbycustomerandtypeandstatus": ".findByCustomerAndTypeAndStatus()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/CustomAccountRepositoryImpl.java:L30 | neighbors=[CustomAccountRepositoryImpl]
- "persistence_customaccountrepositoryimpl_customaccountrepositoryimpl_findmovementsbyaccountanddaterange": ".findMovementsByAccountAndDateRange()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/CustomAccountRepositoryImpl.java:L45 | neighbors=[CustomAccountRepositoryImpl]
- "persistence_processedevent": "ProcessedEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/ProcessedEvent.java:L1 | neighbors=[ProcessedEvent]
- "persistence_processedevent_processedevent": "ProcessedEvent" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/ProcessedEvent.java:L17 | neighbors=[ProcessedEvent.java]
- "persistence_processedeventrepository_processedeventrepository_findbyid": ".findById()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/infrastructure/persistence/ProcessedEventRepository.java:L14 | neighbors=[ProcessedEventRepository]
- "repository_accountrepository_accountrepository_countbycustomeridandtype": ".countByCustomerIdAndType()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/AccountRepository.java:L38 | neighbors=[AccountRepository]
- "repository_accountrepository_accountrepository_existsbycustomeridandtype": ".existsByCustomerIdAndType()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/AccountRepository.java:L33 | neighbors=[AccountRepository]
- "repository_accountrepository_accountrepository_findallbycustomerid": ".findAllByCustomerId()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/AccountRepository.java:L18 | neighbors=[AccountRepository]
- "repository_accountrepository_accountrepository_findallbycustomeridandstatus": ".findAllByCustomerIdAndStatus()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/AccountRepository.java:L28 | neighbors=[AccountRepository]
- "repository_accountrepository_accountrepository_findallbycustomeridandtype": ".findAllByCustomerIdAndType()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/AccountRepository.java:L23 | neighbors=[AccountRepository]
- "repository_accountrepository_accountrepository_findbyaccountnumber": ".findByAccountNumber()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/AccountRepository.java:L43 | neighbors=[AccountRepository]
- "repository_customercardviewrepository_customercardviewrepository_findbycustomerid": ".findByCustomerId()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/CustomerCardViewRepository.java:L15 | neighbors=[CustomerCardViewRepository]
- "repository_customerprofileviewrepository_customerprofileviewrepository_findbycustomerid": ".findByCustomerId()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/CustomerProfileViewRepository.java:L15 | neighbors=[CustomerProfileViewRepository]
- "repository_customerriskviewrepository_customerriskviewrepository_findbycustomerid": ".findByCustomerId()" | kind=code-symbol | source=src/main/java/com/yanki/accounts/domain/repository/CustomerRiskViewRepository.java:L15 | neighbors=[CustomerRiskViewRepository]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\accounts-service\.graphify\description-instructions\batch-007.json

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
