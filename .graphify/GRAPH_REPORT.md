# Graph Report - .  (2026-11-08)

## Corpus Check
- Corpus is ~12,370 words - fits in a single context window. You may not need a graph.

## Summary
- 386 nodes · 427 edges · 80 communities detected
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS
- Token cost: 0 input · 0 output
- Edge kinds: method: 189 · contains: 96 · calls: 95 · imports: 27 · inherits: 19 · implements: 1


## Input Scope
- Requested: auto
- Resolved: all (source: default-auto)
- Included files: 91 · Candidates: recursive
- Excluded: 0 untracked · 0 ignored · 0 sensitive · 0 missing committed
## God Nodes (most connected - your core abstractions)
1. `AccountService` - 44 edges
2. `AccountEventConsumer` - 21 edges
3. `CustomerProfileViewTest` - 12 edges
4. `AccountTest` - 11 edges
5. `AccountRepository` - 10 edges
6. `MovementRepository` - 9 edges
7. `AccountController` - 9 edges
8. `AccountControllerTest` - 8 edges
9. `CustomerProfileView` - 6 edges
10. `AccountEventPublisher` - 6 edges

## Surprising Connections (you probably didn't know these)
- `MovementRepository` --inherits--> `ReactiveMongoRepository`  [EXTRACTED]
  src/main/java/com/yanki/accounts/domain/repository/MovementRepository.java →   _Bridges community 2 → community 1_

## Communities

### Community 24 - "Community 24"
Cohesion: 0.67
Nodes (1): AccountsServiceApplication

### Community 54 - "Community 54"
Cohesion: 1.00
Nodes (1): AccountClosedEvent

### Community 55 - "Community 55"
Cohesion: 1.00
Nodes (1): AccountMovementRegisteredEvent

### Community 56 - "Community 56"
Cohesion: 1.00
Nodes (1): AccountOpenedEvent

### Community 57 - "Community 57"
Cohesion: 1.00
Nodes (1): CardClosedEvent

### Community 58 - "Community 58"
Cohesion: 1.00
Nodes (1): CardOpenedEvent

### Community 59 - "Community 59"
Cohesion: 1.00
Nodes (1): CreditOverdueEvent

### Community 60 - "Community 60"
Cohesion: 1.00
Nodes (1): CustomerDeletedEvent

### Community 61 - "Community 61"
Cohesion: 1.00
Nodes (1): CustomerRegisteredEvent

### Community 62 - "Community 62"
Cohesion: 1.00
Nodes (1): CustomerUpdatedEvent

### Community 63 - "Community 63"
Cohesion: 1.00
Nodes (1): DebitPaymentRequestedEvent

### Community 64 - "Community 64"
Cohesion: 1.00
Nodes (1): DebitPaymentSettledEvent

### Community 35 - "Community 35"
Cohesion: 0.67
Nodes (1): Topics

### Community 65 - "Community 65"
Cohesion: 1.00
Nodes (1): TransferRequestedEvent

### Community 66 - "Community 66"
Cohesion: 1.00
Nodes (1): TransferSettledEvent

### Community 14 - "Community 14"
Cohesion: 0.40
Nodes (2): BusinessException, RuntimeException

### Community 38 - "Community 38"
Cohesion: 0.67
Nodes (1): ErrorCodes

### Community 15 - "Community 15"
Cohesion: 0.40
Nodes (1): Account

### Community 67 - "Community 67"
Cohesion: 1.00
Nodes (1): AccountHolder

### Community 68 - "Community 68"
Cohesion: 1.00
Nodes (1): AccountSigner

### Community 69 - "Community 69"
Cohesion: 1.00
Nodes (1): CustomerCardView

### Community 9 - "Community 9"
Cohesion: 0.29
Nodes (1): CustomerProfileView

### Community 70 - "Community 70"
Cohesion: 1.00
Nodes (1): CustomerRiskView

### Community 71 - "Community 71"
Cohesion: 1.00
Nodes (1): Movement

### Community 1 - "Community 1"
Cohesion: 0.09
Nodes (13): AccountRepository, ReactiveMongoRepository, Account, String, CustomerCardViewRepository, CustomerCardView, CustomerProfileViewRepository, CustomerProfileView (+5 more)

### Community 2 - "Community 2"
Cohesion: 0.08
Nodes (6): MovementRepository, Movement, AccountMapper, CustomAccountRepository, CustomAccountRepositoryImpl, CustomAccountRepository

### Community 22 - "Community 22"
Cohesion: 0.50
Nodes (3): AccountResult, AccountHolderInfo, AccountSignerInfo

### Community 0 - "Community 0"
Cohesion: 0.11
Nodes (1): AccountService

### Community 73 - "Community 73"
Cohesion: 1.00
Nodes (1): DebitPaymentCommand

### Community 74 - "Community 74"
Cohesion: 1.00
Nodes (1): DebitPaymentResult

### Community 75 - "Community 75"
Cohesion: 1.00
Nodes (1): DepositCommand

### Community 76 - "Community 76"
Cohesion: 1.00
Nodes (1): MovementResult

### Community 23 - "Community 23"
Cohesion: 0.50
Nodes (3): OpenAccountCommand, AccountHolderCommand, AccountSignerCommand

### Community 77 - "Community 77"
Cohesion: 1.00
Nodes (1): TransferCommand

### Community 78 - "Community 78"
Cohesion: 1.00
Nodes (1): TransferResult

### Community 79 - "Community 79"
Cohesion: 1.00
Nodes (1): WithdrawalCommand

### Community 48 - "Community 48"
Cohesion: 1.00
Nodes (1): CacheProperties

### Community 25 - "Community 25"
Cohesion: 0.67
Nodes (1): OpenApiConfig

### Community 17 - "Community 17"
Cohesion: 0.50
Nodes (1): RedisConfig

### Community 26 - "Community 26"
Cohesion: 0.67
Nodes (1): ResilienceConfig

### Community 3 - "Community 3"
Cohesion: 0.27
Nodes (1): AccountEventConsumer

### Community 8 - "Community 8"
Cohesion: 0.29
Nodes (1): AccountEventPublisher

### Community 72 - "Community 72"
Cohesion: 1.00
Nodes (1): ProcessedEvent

### Community 10 - "Community 10"
Cohesion: 0.40
Nodes (1): GlobalExceptionHandler

### Community 6 - "Community 6"
Cohesion: 0.20
Nodes (1): AccountController

### Community 11 - "Community 11"
Cohesion: 0.40
Nodes (3): AccountRequest, AccountHolderRequest, AccountSignerRequest

### Community 13 - "Community 13"
Cohesion: 0.40
Nodes (3): AccountResponse, AccountHolderResponse, AccountSignerResponse

### Community 49 - "Community 49"
Cohesion: 1.00
Nodes (1): ApiError

### Community 50 - "Community 50"
Cohesion: 1.00
Nodes (1): BalanceResponse

### Community 51 - "Community 51"
Cohesion: 1.00
Nodes (1): DepositRequest

### Community 28 - "Community 28"
Cohesion: 0.67
Nodes (1): MovementResponse

### Community 52 - "Community 52"
Cohesion: 1.00
Nodes (1): PageResponse

### Community 53 - "Community 53"
Cohesion: 1.00
Nodes (1): WithdrawalRequest

### Community 30 - "Community 30"
Cohesion: 0.67
Nodes (1): AccountClosedEventTest

### Community 31 - "Community 31"
Cohesion: 0.67
Nodes (1): AccountMovementRegisteredEventTest

### Community 32 - "Community 32"
Cohesion: 0.67
Nodes (1): AccountOpenedEventTest

### Community 33 - "Community 33"
Cohesion: 0.67
Nodes (1): CardClosedEventTest

### Community 34 - "Community 34"
Cohesion: 0.67
Nodes (1): DebitPaymentSettledEventTest

### Community 36 - "Community 36"
Cohesion: 0.67
Nodes (1): TopicsTest

### Community 37 - "Community 37"
Cohesion: 0.67
Nodes (1): TransferSettledEventTest

### Community 19 - "Community 19"
Cohesion: 0.50
Nodes (1): BusinessExceptionTest

### Community 39 - "Community 39"
Cohesion: 0.67
Nodes (1): ErrorCodesTest

### Community 40 - "Community 40"
Cohesion: 0.67
Nodes (1): AccountHolderTest

### Community 41 - "Community 41"
Cohesion: 0.67
Nodes (1): AccountSignerTest

### Community 5 - "Community 5"
Cohesion: 0.17
Nodes (1): AccountTest

### Community 42 - "Community 42"
Cohesion: 0.67
Nodes (1): CustomerCardViewTest

### Community 4 - "Community 4"
Cohesion: 0.15
Nodes (1): CustomerProfileViewTest

### Community 20 - "Community 20"
Cohesion: 0.50
Nodes (1): CustomerRiskViewTest

### Community 21 - "Community 21"
Cohesion: 0.50
Nodes (1): MovementTest

### Community 43 - "Community 43"
Cohesion: 0.67
Nodes (1): DebitPaymentCommandTest

### Community 44 - "Community 44"
Cohesion: 0.67
Nodes (1): DepositCommandTest

### Community 45 - "Community 45"
Cohesion: 0.67
Nodes (1): OpenAccountCommandTest

### Community 46 - "Community 46"
Cohesion: 0.67
Nodes (1): TransferCommandTest

### Community 47 - "Community 47"
Cohesion: 0.67
Nodes (1): WithdrawalCommandTest

### Community 16 - "Community 16"
Cohesion: 0.50
Nodes (1): CachePropertiesTest

### Community 18 - "Community 18"
Cohesion: 0.50
Nodes (1): AccountControllerIntegrationTest

### Community 7 - "Community 7"
Cohesion: 0.22
Nodes (1): AccountControllerTest

### Community 12 - "Community 12"
Cohesion: 0.40
Nodes (1): AccountRequestTest

### Community 27 - "Community 27"
Cohesion: 0.67
Nodes (1): AccountResponseTest

### Community 29 - "Community 29"
Cohesion: 0.67
Nodes (1): MovementResponseTest

## Knowledge Gaps
- **42 isolated node(s):** `AccountClosedEvent`, `AccountMovementRegisteredEvent`, `AccountOpenedEvent`, `CardClosedEvent`, `CardOpenedEvent` (+37 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **Thin community `Community 24`** (1 nodes): `AccountsServiceApplication`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 54`** (1 nodes): `AccountClosedEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 55`** (1 nodes): `AccountMovementRegisteredEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 56`** (1 nodes): `AccountOpenedEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 57`** (1 nodes): `CardClosedEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 58`** (1 nodes): `CardOpenedEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 59`** (1 nodes): `CreditOverdueEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 60`** (1 nodes): `CustomerDeletedEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 61`** (1 nodes): `CustomerRegisteredEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 62`** (1 nodes): `CustomerUpdatedEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 63`** (1 nodes): `DebitPaymentRequestedEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 64`** (1 nodes): `DebitPaymentSettledEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 35`** (1 nodes): `Topics`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 65`** (1 nodes): `TransferRequestedEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 66`** (1 nodes): `TransferSettledEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 14`** (2 nodes): `BusinessException`, `RuntimeException`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 38`** (1 nodes): `ErrorCodes`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 15`** (1 nodes): `Account`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 67`** (1 nodes): `AccountHolder`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 68`** (1 nodes): `AccountSigner`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 69`** (1 nodes): `CustomerCardView`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 9`** (1 nodes): `CustomerProfileView`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 70`** (1 nodes): `CustomerRiskView`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 71`** (1 nodes): `Movement`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 0`** (1 nodes): `AccountService`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 73`** (1 nodes): `DebitPaymentCommand`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 74`** (1 nodes): `DebitPaymentResult`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 75`** (1 nodes): `DepositCommand`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 76`** (1 nodes): `MovementResult`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 77`** (1 nodes): `TransferCommand`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 78`** (1 nodes): `TransferResult`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 79`** (1 nodes): `WithdrawalCommand`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 48`** (1 nodes): `CacheProperties`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 25`** (1 nodes): `OpenApiConfig`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 17`** (1 nodes): `RedisConfig`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 26`** (1 nodes): `ResilienceConfig`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 3`** (1 nodes): `AccountEventConsumer`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 8`** (1 nodes): `AccountEventPublisher`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 72`** (1 nodes): `ProcessedEvent`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 10`** (1 nodes): `GlobalExceptionHandler`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 6`** (1 nodes): `AccountController`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 49`** (1 nodes): `ApiError`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 50`** (1 nodes): `BalanceResponse`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 51`** (1 nodes): `DepositRequest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 28`** (1 nodes): `MovementResponse`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 52`** (1 nodes): `PageResponse`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 53`** (1 nodes): `WithdrawalRequest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 30`** (1 nodes): `AccountClosedEventTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 31`** (1 nodes): `AccountMovementRegisteredEventTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 32`** (1 nodes): `AccountOpenedEventTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 33`** (1 nodes): `CardClosedEventTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 34`** (1 nodes): `DebitPaymentSettledEventTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 36`** (1 nodes): `TopicsTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 37`** (1 nodes): `TransferSettledEventTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 19`** (1 nodes): `BusinessExceptionTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 39`** (1 nodes): `ErrorCodesTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 40`** (1 nodes): `AccountHolderTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 41`** (1 nodes): `AccountSignerTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 5`** (1 nodes): `AccountTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 42`** (1 nodes): `CustomerCardViewTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 4`** (1 nodes): `CustomerProfileViewTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 20`** (1 nodes): `CustomerRiskViewTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 21`** (1 nodes): `MovementTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 43`** (1 nodes): `DebitPaymentCommandTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 44`** (1 nodes): `DepositCommandTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 45`** (1 nodes): `OpenAccountCommandTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 46`** (1 nodes): `TransferCommandTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 47`** (1 nodes): `WithdrawalCommandTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 16`** (1 nodes): `CachePropertiesTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 18`** (1 nodes): `AccountControllerIntegrationTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 7`** (1 nodes): `AccountControllerTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 12`** (1 nodes): `AccountRequestTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 27`** (1 nodes): `AccountResponseTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 29`** (1 nodes): `MovementResponseTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AccountService` connect `Community 0` to `Community 1`?**
  _High betweenness centrality (0.058) - this node is a cross-community bridge._
- **Why does `AccountEventConsumer` connect `Community 3` to `Community 1`?**
  _High betweenness centrality (0.030) - this node is a cross-community bridge._
- **What connects `AccountClosedEvent`, `AccountMovementRegisteredEvent`, `AccountOpenedEvent` to the rest of the system?**
  _42 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.08571428571428572 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.08333333333333333 - nodes in this community are weakly interconnected._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.10570824524312897 - nodes in this community are weakly interconnected._