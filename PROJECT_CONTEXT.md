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

## 4. Main navigation

Five primary bottom navigation destinations:

1. Home
2. Calendar
3. Bookings
4. Money
5. More

A global add action provides the shell for:

- New booking
- Record payment
- Add expense
- Block date

Until the related data phases are implemented, quick-add actions intentionally show a development message instead of creating fake data.

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

Runtime language selection is implemented in More using Android per-app locales.

Current behaviour:

- User can select English.
- User can select Bahasa Melayu.
- AndroidX AppCompat handles backward-compatible locale switching.
- Locale choice is auto-stored on Android 12 and lower.
- `locales_config.xml` exposes English and Malay as supported app languages.
- User-entered data is never translated.

A dedicated first-launch language onboarding screen can be added later if it improves the final onboarding flow. It is not required for the Phase 1 shell.

## 7. Visual direction

Phase 1 establishes the HOMIQ visual foundation.

Direction:

- Modern, calm, clean and owner-focused.
- Information first, without looking like enterprise property software.
- Primary brand family: deep homestay green with soft mint surfaces.
- Neutral off-white canvas in light mode.
- Full dark mode support.
- Rounded Material 3 cards with low elevation.
- Financial cards prioritise legibility over decoration.
- No neon styling.
- No guest marketplace styling.
- No unnecessary imagery in operational screens.

The theme is implemented through `ui/theme`.

## 8. Local first architecture

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

Local database: Room 2.8.4 over SQLite. Database version 1 uses exported schemas and explicit migrations. See `docs/DATA_MODEL.md`.

Do not make cloud storage the only source for bookings.

## 9. Account strategy

Account is optional.

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

## 10. Backup strategy

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

## 11. Multi-device sync

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

## 12. Security and privacy

Planned:

- App lock using device biometrics or PIN.
- Do not store passwords in plain text.
- Do not commit credentials or API secrets.
- Minimise guest personal data.
- Backups must not expose data unnecessarily.
- Cloud access should be scoped to the signed-in owner.

The manifest disables Android automatic app backup because HOMIQ will implement explicit controlled backup and restore.

## 13. Architecture

Start simple. Avoid premature over-engineering.

Package root:

`com.homiq.app`

Current UI structure:

- `ui/HomiqApp.kt`
- `ui/components`
- `ui/screens`
- `ui/theme`

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

Preferred Android pattern:

- Single activity
- Jetpack Compose
- Unidirectional UI state
- ViewModel per feature where useful
- Repository boundary between UI and persistence
- Coroutines and Flow
- Local first persistence
- Dependency injection only when complexity justifies it

## 14. Development roadmap

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

- [x] App theme and visual direction
- [x] Bottom navigation
- [x] Home shell
- [x] Calendar shell with current-month grid
- [x] Bookings shell
- [x] Money shell
- [x] More shell
- [x] Global quick-add sheet
- [x] Runtime language selection
- [x] Dark mode theme foundation
- [ ] Confirm Phase 1 files build successfully after repository upload

Phase 1 completion is considered final only after the uploaded files build successfully in the repository. If a compile correction is required, keep Phase 2 blocked until Phase 1 is green.

### Phase 2: Local data foundation

- [x] Select Room 2.8.4 as local database
- [x] Define database version 1 and explicit migration policy
- [x] Define Property entity
- [x] Define Booking entity
- [x] Define Payment entity
- [x] Define Deposit entity
- [x] Define Expense entity
- [x] Define Blocked Date entity
- [x] Property repository
- [x] Booking repository
- [x] Payment repository
- [x] Deposit repository
- [x] Expense repository
- [x] Blocked Date repository
- [x] Add overlap-query foundation for booking and blocked dates
- [x] Add local database instrumented test
- [ ] Confirm Phase 2 files build successfully after repository upload

### Phase 3: Properties and bookings

- [x] Property management list
- [x] Add property
- [x] Edit property
- [x] Active/inactive property state
- [x] New booking form
- [x] Booking validation
- [x] Double booking prevention
- [x] Blocked date collision prevention
- [x] Booking list connected to Room
- [x] Booking filters
- [x] Booking details
- [x] Edit booking
- [x] Cancel booking without deleting history
- [x] Booking source tracking
- [x] Unit tests for half-open date overlap rules
- [ ] Confirm Phase 3 files build successfully after repository upload

### Phase 4: Calendar

- [x] Monthly calendar connected to Room data
- [x] Previous and next month navigation
- [x] Jump back to today
- [x] All-properties availability view
- [x] Single-property calendar filter
- [x] Booking and blocked-date indicators
- [x] Booking status colour/state rules in selected-date agenda
- [x] Selected-date agenda
- [x] Tap booking to open details
- [x] Create booking from selected calendar date
- [x] Prefill selected property for calendar booking
- [x] Block Date form
- [x] Block Date collision validation against bookings
- [x] Block Date collision validation against other blocked periods
- [x] Global Block Date quick action connected
- [x] Calendar date-range unit tests
- [ ] Confirm Phase 4 files build successfully after repository upload

### Phase 5: Payments and deposits

- [x] Record payment from global quick add
- [x] Record payment from Booking Details
- [x] Multiple payments per booking
- [x] Paid calculation
- [x] Outstanding balance calculation
- [x] Reject overpayment
- [x] Payment method tracking
- [x] Payment history
- [x] Security deposit requirement
- [x] Deposit pending state
- [x] Deposit receive
- [x] Partial deposit return
- [x] Full deposit return
- [x] Deposit retain
- [x] Deposit remaining calculation
- [x] Deposit remains separate from booking payment and revenue
- [x] Payment and deposit unit tests
- [ ] Confirm Phase 5 files build successfully after repository upload

### Phase 6: Expenses and money

- [x] Expense entry
- [x] Optional property assignment for expenses
- [x] General business expenses
- [x] Expense categories
- [x] Expense history for selected month
- [x] Cash-based revenue calculation from received payments
- [x] Expense calculation
- [x] Net income calculation
- [x] Deposit exclusion from revenue and expenses
- [x] Previous/next month Money navigation
- [x] Monthly summary
- [x] Revenue per property
- [x] Expenses per property
- [x] Net income per property
- [x] General expense separated from property profitability
- [x] Expense and money calculation unit tests
- [ ] Confirm Phase 6 files build successfully after repository upload

### Phase 7: Dashboard and reports

- [x] Home dashboard connected to live Room data
- [x] Current-month revenue on Home
- [x] Current-month expenses on Home
- [x] Current-month net income on Home
- [x] Occupancy formula
- [x] Blocked nights excluded from sellable capacity
- [x] Today's check-ins
- [x] Today's check-outs
- [x] Outstanding balance attention list
- [x] Upcoming bookings
- [x] Monthly report
- [x] Yearly report
- [x] Booking count
- [x] Booked value
- [x] Average booking value
- [x] Booked and available nights
- [x] Booking source analytics
- [x] Report sharing through Android share sheet
- [x] Analytics formula unit tests
- [ ] Confirm Phase 7 files build successfully after repository upload

### Phase 8: Backup and restore

- [x] Versioned portable HOMIQ backup format
- [x] Backup all six Room business tables
- [x] Preserve stable IDs, sync revisions and tombstones
- [x] Consistent backup snapshot inside Room transaction
- [x] Local backup through Android system file picker
- [x] Google Drive-compatible backup through system DocumentsProvider picker
- [x] No broad storage permission required
- [x] Restore file validation before database changes
- [x] Foreign-key relationship validation
- [x] Restore preview and explicit confirmation
- [x] Transactional full restore
- [x] Restore rollback on failure
- [x] Last successful backup timestamp
- [x] Last successful restore timestamp
- [x] Backup codec round-trip unit test
- [ ] Confirm Phase 8 files build successfully after repository upload

### Phase 9: Optional multi-device sync

- [x] Google Drive selected as the HOMIQ sync layer
- [x] Narrow drive.appdata OAuth scope
- [x] Google Identity AuthorizationClient integration
- [x] Private Drive appDataFolder storage
- [x] Stable per-installation device UUID
- [x] One sync file per HOMIQ installation
- [x] Local Room remains primary and fully offline-capable
- [x] Sync all six business tables
- [x] Tombstone propagation
- [x] Deterministic revision-based merge
- [x] Same-revision conflict detection/counting
- [x] Deposit semantic merge by bookingId
- [x] Transactional Room application of merged state
- [x] Sync on app foreground
- [x] Debounced sync after local writes/deletes
- [x] Manual Sync Now
- [x] Google authorization re-consent handling
- [x] Disconnect/revoke Google Drive access
- [x] Sync status UI under More
- [x] Google Cloud/OAuth setup documentation
- [x] Sync merge unit tests
- [ ] Confirm Phase 9 files build successfully after repository upload

### Phase 10: Security and polish

- [x] Local app lock with 4-8 digit PIN
- [x] Salted PBKDF2-HMAC-SHA256 PIN hashing
- [x] Fresh process starts locked when PIN exists
- [x] Biometric unlock with AndroidX Biometric
- [x] Auto-lock timeout: immediate / 1 / 5 / 15 minutes
- [x] Lock Now action
- [x] Change PIN and disable lock flows
- [x] More -> Security screen
- [x] Business UI gated behind app lock
- [x] Stable debug signing certificate for OAuth testing
- [x] Runtime SHA-1 display/copy on Drive Sync screen
- [x] Google Drive OAuth setup documentation updated
- [x] Dark-mode InfoCard contrast polish
- [x] Error and empty-state review
- [x] Accessibility/touch-target review
- [x] Performance review: no new polling/background worker
- [x] Data-integrity review: no Room migration/business-record mutation
- [x] Backup/sync regression review
- [x] Final English/Malay copy parity review
- [x] English/Malay security strings
- [x] PIN hashing/rule unit tests
- [x] Confirm Phase 10 files build successfully after repository upload

### Phase 11: Release

- [ ] Versioning
- [ ] Release signing strategy
- [ ] Release build
- [ ] Fresh-install test
- [ ] Upgrade test
- [ ] Backup and restore test
- [ ] APK distribution for owner use
- [ ] Update repository context

## 15. Definition of done for V1

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

## 16. Current state

Expected repository state after Phase 10 files are uploaded:

- All Phase 1-9 workflows remain available.
- Phase 9 Google Drive sync code remains local-first and optional.
- Phase 10 adds a functional PIN app lock, biometric unlock and configurable auto-lock.
- The plain PIN is not stored; HOMIQ stores a salted PBKDF2-HMAC-SHA256 hash.
- A fresh process is locked before business screens are composed whenever a PIN exists.
- More -> Security manages PIN, biometrics, timeout and Lock Now.
- Phase 10 debug APKs use one stable debug-only signing certificate.
- Debug package: `com.homiq.app`.
- Debug SHA-1: `5B:FC:0E:63:6E:F3:06:80:F3:BD:A1:5D:4B:B9:93:C4:22:B1:48:D9`.
- The Sync screen shows/copies the actual installed certificate SHA-1 for Google Cloud OAuth setup.
- Google Drive still requires one-time Drive API + Android OAuth client configuration in the user Google Cloud project.
- The Phase 10 debug key must never be used for production release signing.
- Because the signing certificate changes from previous debug builds, the first Phase 10 installation may require backup -> uninstall old app -> install -> restore.
- InfoCard contrast is improved for dark mode.
- No database migration is required for Phase 10.
- Phase 11 is Release Readiness.
- Next development task after a green Phase 10 build is Phase 11: Release Readiness.

## 17. Change log

### 31 August 2026, Phase 10

- Added PIN app lock and optional biometric unlock.
- Added auto-lock timeout and Lock Now.
- Added salted PBKDF2 PIN hashing; plain PIN is never stored.
- Added Security screen and app-wide lock gate.
- Added AndroidX Biometric 1.1.0.
- Added stable debug-only signing certificate for Google OAuth testing.
- Phase 10 debug SHA-1: `5B:FC:0E:63:6E:F3:06:80:F3:BD:A1:5D:4B:B9:93:C4:22:B1:48:D9`.
- Added installed-certificate SHA-1 display/copy on Sync screen.
- Updated Google Drive OAuth setup documentation.
- Improved InfoCard contrast in dark mode.
- Added EN/MS security resources and app-lock tests.
- No database migration.

### 31 August 2026, Phase 9

- Locked Google Drive appDataFolder as the optional multi-device sync layer.
- Added Google Identity AuthorizationClient with drive.appdata scope.
- Added stable per-installation device identity.
- Added one hidden Drive current-state snapshot per device.
- Added Drive REST list/download/create/update implementation.
- Added deterministic six-table sync merge and tombstone propagation.
- Added same-revision conflict counting.
- Added bookingId-aware deposit merge.
- Added transactional Room application of merged state.
- Added app-foreground automatic sync.
- Added debounced sync after successful local repository changes.
- Added manual Sync Now and Google re-authorization handling.
- Added Drive access disconnect/revoke.
- Added More -> Google Drive Sync UI and status.
- Added `docs/SYNC_FLOW.md`.
- Added `docs/GOOGLE_DRIVE_SYNC_SETUP.md`.
- Added sync merger unit tests.
- Added Google Play services Auth 21.6.0.
- Drive REST calls use platform HttpURLConnection, avoiding an unnecessary networking dependency.
- Added INTERNET permission.
- Added no database migration and no HOMIQ backend dependency.

### 31 August 2026, Phase 8

- Added versioned HOMIQ JSON backup format.
- Added complete logical snapshot of all six business tables.
- Preserved sync-ready UUID, timestamp, revision and tombstone metadata.
- Added Android Storage Access Framework backup creation.
- Enabled Google Drive-compatible backup through the system document provider.
- Added backup file inspection and compatibility validation.
- Added foreign-key and deposit uniqueness validation before restore.
- Added restore preview with explicit owner confirmation.
- Added full transactional restore with rollback safety.
- Added local last-backup and last-restore history.
- Added backup codec round-trip unit coverage.
- Added `docs/BACKUP_RESTORE_FLOW.md`.
- Added no schema migration and no external dependency.

### 31 August 2026, Phase 7

- Connected Home to live business data.
- Added today's check-in and check-out operations.
- Added outstanding balance attention cards.
- Added live upcoming booking cards.
- Added occupancy based on booked sellable nights.
- Excluded blocked nights from occupancy capacity.
- Added Monthly and Yearly reports.
- Added booking count, booked value and average booking value.
- Added booking-source analytics.
- Added report sharing through the Android share sheet.
- Preserved cash-based financial reporting and strict deposit exclusion.
- Added analytics formula unit tests.
- Added `docs/REPORTS_FLOW.md`.
- Added no schema migration and no external dependency.

### 31 August 2026, Phase 6

- Activated Room-backed expense entry.
- Added optional property assignment and General expenses.
- Added all locked expense categories.
- Connected Money to real payment and expense data.
- Locked cash-based revenue to payments actually received in the selected month.
- Added monthly Revenue, Expenses and Net Income.
- Added property-level profitability breakdown.
- Kept General expenses separate instead of inventing allocations.
- Preserved strict security-deposit exclusion from financial results.
- Added expense history for the selected month.
- Added expense and financial formula unit tests.
- Added `docs/MONEY_FLOW.md`.
- Added no schema migration and no external dependency.

### 31 August 2026, Phase 5

- Activated Room-backed payment recording.
- Added global booking picker for outstanding balances.
- Added multiple-payment support and payment history.
- Added total paid and outstanding balance calculations.
- Added overpayment prevention.
- Added Payment Method tracking.
- Activated the security deposit lifecycle.
- Added partial/full deposit return and retention.
- Kept deposits strictly outside booking payment totals and future revenue.
- Added pure payment and deposit rule tests.
- Added `docs/PAYMENTS_DEPOSITS_FLOW.md`.
- Added no new external dependency and no database migration.

### 31 August 2026, Phase 4

- Connected Calendar to live Room booking and blocked-date data.
- Added month navigation and today shortcut.
- Added portfolio and per-property availability filters.
- Added booked and blocked date indicators.
- Added selected-date agenda and booking navigation.
- Added New Booking prefill from Calendar.
- Added functional Block Date flow and validation.
- Connected global Block Date quick action.
- Added `CalendarRules` tests for half-open date occupancy.
- Added `docs/CALENDAR_FLOW.md`.
- Added no new external dependency in this phase.

### 31 August 2026, Phase 3

- Added property management with add, edit and active state.
- Connected Bookings UI to Room data.
- Added real manual booking creation.
- Added date, guest, property and amount validation.
- Added double-booking and blocked-date collision prevention.
- Added booking detail and edit flows.
- Added non-destructive cancellation.
- Added booking source persistence and display.
- Added pure unit coverage for half-open date-range rules.
- Lifecycle Compose pinned to 2.10.0 to preserve compileSdk 36 compatibility.
- Phase 3 compile correction removes obsolete explicit Compose `weight` imports and uses explicit suspend repository lookups for Kotlin 2.4 compatibility.
- Added `docs/BOOKING_FLOW.md`.
- Included the Phase 1 product shell as a self-heal because the live repository still exposed the original foundation UI when Phase 3 work began.

### 31 August 2026, Phase 2

- Selected Room 2.8.4 for the Android local database.
- Added KSP 2.3.10 for Room code generation and the Room Gradle plugin for reproducible schema export.
- Added database version 1 with exported schemas.
- Added six core persistent entities.
- Added Room DAOs and repository boundaries.
- Stored monetary values in integer sen.
- Added stable UUID, revision, timestamps and tombstones for future sync.
- Added overlap queries using half-open stay/date ranges.
- Added explicit non-destructive migration policy.
- Added instrumented database coverage for overlap and payment totals.
- Added `docs/DATA_MODEL.md` as the local-data reference.

### 31 August 2026, Phase 1

- Established HOMIQ deep-green and soft-mint visual system.
- Added full five-tab product shell.
- Added Home zero-state dashboard.
- Added current-month Calendar shell.
- Added Bookings and Money zero states.
- Added More settings shell.
- Added global quick-add bottom sheet.
- Added English and Bahasa Melayu runtime language switching.
- Added AppCompat locale storage compatibility.
- Added light and dark theme foundations.

### 31 August 2026, Phase 0

- HOMIQ name locked.
- Owner-only manual homestay management concept locked.
- Zero recurring operating cost requirement locked.
- BM and English requirement locked.
- Local backup and Google Drive backup planned.
- Optional same-account multi-device sync planned.
- Android local-first architecture selected.
- Initial repository bootstrap created.
