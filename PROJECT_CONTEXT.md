# HOMIQ Project Context

Last context reset: 31 August 2026

This file is the main source of truth for HOMIQ. Any AI, developer, or new ChatGPT account continuing this project must read this file and `docs/FLOWCHART.md` before proposing code changes.

## 1. Resume protocol

When continuing HOMIQ in a new conversation or account:

1. Read this entire file.
2. Read `docs/FLOWCHART.md`.
3. Inspect the live repository before changing code.
4. Check the current phase and completed checklist.
5. Do not redesign locked product decisions unless the owner explicitly asks.
6. Continue from the first incomplete roadmap item.
7. After a major product or architecture decision, update this file in the same change.
8. Keep the project zero recurring cost unless the owner explicitly changes that rule.

Suggested resume prompt:

`Read PROJECT_CONTEXT.md and docs/FLOWCHART.md, inspect the current repository, then continue HOMIQ from the first incomplete roadmap item without changing locked decisions.`

## 2. Product identity

Name: HOMIQ

Type: Private homestay management app for the owner.

Primary purpose: Record and manage bookings that have already been received manually from WhatsApp or any other source, then track operations and money in one place.

HOMIQ is not:

- A public booking platform.
- A marketplace for guests.
- A guest social app.
- A payment gateway.
- A subscription SaaS product.
- Dependent on a server for basic daily use.

## 3. Locked requirements

These decisions are locked unless the owner explicitly changes them.

- App name is HOMIQ.
- Android first.
- Kotlin and Jetpack Compose.
- Owner only.
- Zero recurring operating cost is a core requirement.
- App must work offline for normal daily management.
- Bahasa Melayu and English.
- Manual booking entry is the primary workflow.
- Multiple homestay properties must be supported.
- Local data is the primary working copy.
- Account sign-in is optional.
- Local backup is required.
- Google Drive backup is required.
- Multi-device sync using the same account is optional but planned.
- Deposits must be tracked separately from revenue.
- The app must remain simple and fast.
- Guest installation is never required.

## 4. Main navigation target

Five primary bottom navigation destinations:

1. Home
2. Calendar
3. Bookings
4. Money
5. More

A global add action will provide quick entry for:

- New booking
- Record payment
- Add expense
- Block date

## 5. Core feature scope

### Home

The owner should immediately see:

- Revenue for selected period
- Expenses
- Net income
- Occupancy
- Today's check-ins
- Today's check-outs
- Outstanding balances
- Upcoming bookings
- Items needing attention

### Properties

Support one or many homestays.

Planned property fields:

- id
- name
- optional address
- optional notes
- default nightly rate
- active status
- createdAt
- updatedAt

### Bookings

Fast manual entry is critical.

Planned booking fields:

- id
- propertyId
- guestName
- guestPhone
- checkInDate
- checkOutDate
- source
- totalAmount
- bookingStatus
- notes
- createdAt
- updatedAt
- sync metadata when sync is enabled

Booking sources should include:

- WhatsApp
- Airbnb
- Booking.com
- Facebook
- TikTok
- Repeat Guest
- Walk-in
- Other

### Calendar

Calendar must show booked, available, and blocked dates clearly.

For multiple properties, the owner must be able to understand availability without opening each property separately.

Double booking prevention is required.

### Payments

Payments are records only. HOMIQ does not process money.

A booking can have multiple payment entries.

Planned payment fields:

- id
- bookingId
- amount
- paymentDate
- method
- notes
- createdAt
- updatedAt

The app calculates:

- Total booking value
- Total paid
- Outstanding balance

### Deposits

Security deposit is not revenue.

Deposit state should support:

- Not required
- Pending
- Received
- Partially returned
- Returned
- Retained

### Expenses

Planned categories:

- Cleaning
- Electricity
- Water
- Internet
- Supplies
- Maintenance
- Laundry
- Platform fee
- Other

Expenses may optionally be attached to a property.

### Money and reports

Required calculations:

- Revenue
- Expenses
- Net income
- Occupancy percentage
- Average booking value
- Revenue per property
- Expenses per property
- Booking source breakdown
- Monthly performance
- Yearly performance

Financial formulas must be documented and tested before reports are considered complete.

## 6. Language

Supported languages:

- Bahasa Melayu
- English

The first launch experience will eventually allow language selection.

Language can later be changed from More > Settings > Language.

User-entered data is never translated.

Phase 0 only establishes Android localisation resources. In-app manual language switching is a later implementation task.

## 7. Local first architecture

Daily HOMIQ usage must not depend on internet access.

Target data flow:

`UI -> ViewModel -> Repository -> Local database`

When optional sync is enabled:

`Local database <-> Sync engine <-> Cloud copy`

Local data remains usable when:

- Internet is unavailable.
- Cloud sync is unavailable.
- Google Drive is unavailable.
- User chooses not to create an account.

Planned local database: Room or the current recommended Android local persistence layer after compatibility verification at implementation time.

Do not make cloud storage the only source for bookings.

## 8. Account strategy

Account is optional.

Target modes:

### Local only

- No sign-in required.
- All features except cloud backup and cross-device sync should remain usable.
- Local backup remains available.

### Signed in

- Account identity.
- Google Drive backup.
- Restore from Google Drive.
- Optional multi-device sync.
- Sync status visible to owner.

Google account sign-in is the preferred route for Drive-linked backup because a Drive backup necessarily belongs to a Google account.

Any cloud provider must be evaluated against the zero recurring cost requirement before implementation.

## 9. Backup strategy

Backup and sync are separate features.

### Local backup

Required actions:

- Export backup file.
- Import backup file.
- Validate backup version before restore.
- Prevent accidental destructive restore.
- Preserve all business records and app settings required for recovery.

### Google Drive backup

Required actions:

- Backup now.
- Show last successful backup.
- Restore from Drive.
- Handle no internet.
- Handle revoked Google permission.
- Never silently overwrite a newer backup without conflict checks.

Google Drive backup is a disaster recovery mechanism, not the live sync engine.

## 10. Multi-device sync

Goal:

Phone A and Phone B signed into the same HOMIQ account can eventually share current data.

Required behaviour:

- Changes save locally first.
- Pending local changes queue while offline.
- Changes sync when internet returns.
- Visible sync state such as Synced or Changes waiting.
- Conflict handling is explicit.
- Deletions must sync safely.
- Sync must never create duplicate bookings or payments.

Initial conflict strategy to evaluate:

- Stable UUID per record.
- updatedAt timestamp.
- revision or version number.
- soft delete tombstone for synced deletion.
- deterministic conflict rules.
- manual conflict UI only where automatic resolution is unsafe.

Cloud implementation is not locked yet. It must be chosen only after verifying quota, cost, Android support, offline behaviour, and long-term maintenance.

## 11. Security and privacy

Planned:

- App lock using device biometrics or PIN.
- Do not store passwords in plain text.
- Do not commit credentials or API secrets.
- Minimise guest personal data.
- Backups must not expose data unnecessarily.
- Cloud access should be scoped to the signed-in owner.

The initial manifest disables Android automatic app backup because HOMIQ will implement explicit controlled backup and restore.

## 12. Architecture target

Start simple. Avoid premature over-engineering.

Proposed package direction:

`com.homiq.app`

Later packages may include:

- data.local
- data.model
- data.repository
- data.backup
- data.sync
- domain
- ui.home
- ui.calendar
- ui.bookings
- ui.money
- ui.more
- ui.components

Preferred Android pattern:

- Single activity
- Jetpack Compose
- Unidirectional UI state
- ViewModel per feature where useful
- Repository boundary between UI and persistence
- Coroutines and Flow
- Local first persistence
- Dependency injection only when complexity justifies it

## 13. Development roadmap

### Phase 0: Foundation

- [x] Lock product name HOMIQ
- [x] Lock owner-only concept
- [x] Lock bilingual requirement
- [x] Lock local first direction
- [x] Lock local and Google Drive backup requirements
- [x] Lock optional multi-device sync direction
- [x] Create Android project scaffold
- [x] Create bilingual Android resource scaffold
- [x] Create repository context document
- [x] Create end-to-end flowchart
- [x] Confirm generated project builds in GitHub Actions

### Phase 1: Product shell

- [ ] App theme and visual direction
- [ ] Bottom navigation
- [ ] Home placeholder
- [ ] Calendar placeholder
- [ ] Bookings placeholder
- [ ] Money placeholder
- [ ] More placeholder
- [ ] Runtime language selection

### Phase 2: Local data foundation

- [ ] Select and add local database
- [ ] Define entities and migrations
- [ ] Property repository
- [ ] Booking repository
- [ ] Payment repository
- [ ] Deposit repository or deposit model
- [ ] Expense repository
- [ ] Blocked dates
- [ ] Local data tests

### Phase 3: Properties and bookings

- [ ] Property management
- [ ] New booking form
- [ ] Booking validation
- [ ] Double booking prevention
- [ ] Booking details
- [ ] Edit booking
- [ ] Cancel booking
- [ ] Booking source tracking

### Phase 4: Calendar

- [ ] Monthly calendar
- [ ] Multi-property availability
- [ ] Booking colour/state rules
- [ ] Block date
- [ ] Tap date to create booking
- [ ] Tap booking to open details

### Phase 5: Payments and deposits

- [ ] Record payment
- [ ] Multiple payments per booking
- [ ] Paid and balance calculation
- [ ] Deposit receive
- [ ] Deposit return
- [ ] Deposit retain
- [ ] Payment history

### Phase 6: Expenses and money

- [ ] Expense entry
- [ ] Expense categories
- [ ] Revenue calculation
- [ ] Expense calculation
- [ ] Net income calculation
- [ ] Monthly summary
- [ ] Property breakdown

### Phase 7: Dashboard and reports

- [ ] Today dashboard
- [ ] Check-ins and check-outs
- [ ] Occupancy formula
- [ ] Booking source analytics
- [ ] Monthly report
- [ ] Yearly report
- [ ] Export report if useful

### Phase 8: Backup and restore

- [ ] Backup schema and versioning
- [ ] Local export
- [ ] Local import
- [ ] Restore validation
- [ ] Google sign-in for Drive
- [ ] Google Drive backup
- [ ] Google Drive restore
- [ ] Backup status

### Phase 9: Optional multi-device sync

- [ ] Select free-cost-compatible sync backend
- [ ] Account data ownership rules
- [ ] Sync metadata
- [ ] Upload pending local changes
- [ ] Download remote changes
- [ ] Offline queue
- [ ] Conflict strategy
- [ ] Safe deletion sync
- [ ] Sync status UI
- [ ] Two-device testing

### Phase 10: Security and polish

- [ ] Biometric or PIN app lock
- [ ] Error states
- [ ] Empty states
- [ ] Accessibility
- [ ] Performance review
- [ ] Data integrity tests
- [ ] Backup recovery test
- [ ] Final BM and English copy review
- [ ] UI polish

### Phase 11: Release

- [ ] Versioning
- [ ] Release signing strategy
- [ ] Release build
- [ ] Fresh-install test
- [ ] Upgrade test
- [ ] Backup and restore test
- [ ] APK distribution for owner use
- [ ] Update repository context

## 14. Definition of done for V1

V1 is complete only when the owner can:

1. Install HOMIQ.
2. Choose BM or English.
3. Create at least one property.
4. Enter a manual booking quickly.
5. View bookings on a calendar.
6. Record partial and full payments.
7. Track deposits separately.
8. Add operating expenses.
9. See revenue, expenses, net income, and occupancy.
10. View booking source performance.
11. Use the core app offline.
12. Create and restore a local backup.
13. Optionally sign in and back up to Google Drive.
14. If sync is enabled, use the same data safely on two phones.
15. Lock the app with device security.
16. Recover data after reinstall or phone replacement using a valid backup.

## 15. Current state after first setup

Expected repository state after the first-setup workflow succeeds:

- Android project scaffold builds a debug APK.
- HOMIQ launches to a minimal foundation screen.
- English and Malay string resources exist.
- No business database is implemented yet.
- No account, Drive, or cloud sync code is implemented yet.
- `PROJECT_CONTEXT.md` and `docs/FLOWCHART.md` define the project direction.
- The next development task is Phase 1: Product shell.

## 16. Change log

### 31 August 2026

- HOMIQ name locked.
- Owner-only manual homestay management concept locked.
- Zero recurring operating cost requirement locked.
- BM and English requirement locked.
- Local backup and Google Drive backup planned.
- Optional same-account multi-device sync planned.
- Android local-first architecture selected.
- Initial repository bootstrap created.
