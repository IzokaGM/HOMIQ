# HOMIQ Flowcharts

These diagrams describe the intended path from an empty repository to a finished HOMIQ V1 and the main owner workflows.

## 1. End-to-end development flow

```mermaid
flowchart TD
    A[Empty HOMIQ repository] --> B[Phase 0 Foundation]
    B --> C[Android project builds]
    C --> D[Phase 1 Product shell]
    D --> E[BM and English UI]
    E --> F[Phase 2 Local data]
    F --> G[Properties and Bookings]
    G --> H[Calendar and blocked dates]
    H --> I[Payments and Deposits]
    I --> J[Expenses and Money]
    J --> K[Dashboard and Reports]
    K --> L[Local Backup and Restore]
    L --> M[Google Account and Drive Backup]
    M --> N{Enable multi-device sync?}
    N -->|No| P[Security and Polish]
    N -->|Yes| O[Sync Engine and Conflict Handling]
    O --> P
    P --> Q[Full Test Pass]
    Q --> R[Release APK]
    R --> S[HOMIQ V1 Complete]
```

## 2. Owner daily workflow

```mermaid
flowchart TD
    A[Booking received externally] --> B[Open HOMIQ]
    B --> C[New Booking]
    C --> D[Choose property]
    D --> E[Enter guest and dates]
    E --> F[Choose booking source]
    F --> G[Enter total amount]
    G --> H{Dates available?}
    H -->|No| I[Show conflict and stop save]
    H -->|Yes| J[Save booking locally]
    J --> K[Calendar updates]
    J --> L[Dashboard updates]
    J --> M{Payment received?}
    M -->|Yes| N[Record payment]
    M -->|No| O[Outstanding balance remains]
    N --> P[Paid and balance recalculated]
    P --> Q{Deposit involved?}
    O --> Q
    Q -->|Yes| R[Track deposit separately]
    Q -->|No| S[Booking active]
    R --> S
    S --> T[Check-in]
    T --> U[Check-out]
    U --> V[Return or retain deposit]
    V --> W[Booking completed]
    W --> X[Reports update]
```

## 3. Local-first data architecture

```mermaid
flowchart LR
    UI[Compose UI] --> VM[ViewModel]
    VM --> REPO[Repository]
    REPO --> LOCAL[(Local Database)]
    LOCAL --> REPO
    REPO --> VM
    VM --> UI

    LOCAL --> SYNC[Optional Sync Engine]
    SYNC --> CLOUD[(Cloud Copy)]
    CLOUD --> SYNC
    SYNC --> LOCAL

    LOCAL --> BACKUP[Backup Engine]
    BACKUP --> FILE[Local Backup File]
    BACKUP --> DRIVE[Google Drive Backup]
```

Rule: the core owner workflow reads and writes locally first. Cloud and Drive are optional layers.

## 4. Backup versus sync

```mermaid
flowchart TD
    A[HOMIQ local database] --> B{Owner action or schedule}
    B --> C[Create versioned backup]
    C --> D[Local backup file]
    C --> E[Google Drive backup]

    F[Phone A local database] --> G[Sync engine]
    G --> H[Cloud current-state data]
    H --> I[Sync engine]
    I --> J[Phone B local database]

    K[Backup] --> L[Recovery and restore]
    M[Sync] --> N[Keep active devices current]
```

## 5. First launch target

```mermaid
flowchart TD
    A[Launch HOMIQ] --> B{First launch?}
    B -->|Yes| C[Choose BM or English]
    C --> D{Sign in now?}
    D -->|No| E[Local-only mode]
    D -->|Yes| F[Google account sign-in]
    E --> G{Any property exists?}
    F --> G
    G -->|No| H[Create first property]
    G -->|Yes| I[Home]
    H --> I
    B -->|No| I
```

## 6. New booking target

```mermaid
flowchart TD
    A[Tap Add] --> B[New Booking]
    B --> C[Property]
    C --> D[Guest details]
    D --> E[Check-in and Check-out]
    E --> F[Source]
    F --> G[Total amount]
    G --> H[Optional notes]
    H --> I[Validate]
    I --> J{Overlaps booked or blocked dates?}
    J -->|Yes| K[Show conflict]
    K --> E
    J -->|No| L[Save locally]
    L --> M[Create booking]
    M --> N[Refresh Calendar]
    M --> O[Refresh Home]
    M --> P[Optional Record Payment]
```

## 7. Payment and deposit target

```mermaid
flowchart TD
    A[Booking details] --> B[Record payment]
    B --> C[Enter amount and method]
    C --> D[Save payment]
    D --> E[Recalculate total paid]
    E --> F[Recalculate outstanding balance]

    A --> G[Deposit]
    G --> H{Deposit state}
    H --> I[Pending]
    H --> J[Received]
    H --> K[Returned]
    H --> L[Partially returned]
    H --> M[Retained]

    N[Revenue reports] --> O[Exclude refundable deposit from revenue]
```

## 8. Expense and profit target

```mermaid
flowchart TD
    A[Add Expense] --> B[Amount]
    B --> C[Category]
    C --> D[Date]
    D --> E[Optional property]
    E --> F[Save locally]
    F --> G[Expense total updates]

    H[Recognised booking revenue] --> I[Revenue total]
    G --> J[Net income]
    I --> J
    J --> K[Net income = Revenue minus Expenses]
```

## 9. Multi-device sync target

```mermaid
sequenceDiagram
    participant A as Phone A
    participant LA as Local DB A
    participant C as Cloud Sync
    participant LB as Local DB B
    participant B as Phone B

    A->>LA: Save booking locally
    LA-->>A: Immediate success
    LA->>C: Upload pending change when online
    C->>LB: Deliver remote change
    LB-->>B: UI observes updated local data

    Note over A,B: Both phones remain usable offline
```

## 10. Restore target

```mermaid
flowchart TD
    A[Restore requested] --> B[Select local file or Drive backup]
    B --> C[Read backup metadata]
    C --> D{Schema supported?}
    D -->|No| E[Stop and explain incompatibility]
    D -->|Yes| F[Validate integrity]
    F --> G{Valid?}
    G -->|No| H[Stop without modifying current data]
    G -->|Yes| I[Confirm destructive restore]
    I --> J[Create safety snapshot]
    J --> K[Restore data]
    K --> L[Run migrations if required]
    L --> M[Verify record counts and integrity]
    M --> N[Restore complete]
```

## 11. V1 completion gate

```mermaid
flowchart TD
    A[Core features implemented] --> B[Offline test]
    B --> C[Financial calculation tests]
    C --> D[Calendar conflict tests]
    D --> E[BM and English review]
    E --> F[Local backup recovery test]
    F --> G[Drive backup recovery test]
    G --> H{Sync enabled in V1?}
    H -->|Yes| I[Two-phone sync and conflict tests]
    H -->|No| J[Security tests]
    I --> J
    J --> K[Fresh install test]
    K --> L[Upgrade test]
    L --> M[Release APK]
    M --> N[Update PROJECT_CONTEXT.md]
```
