# Homika UI/UX Rebuild

## Goal

Rebuild Homika as a production-quality private homestay operations app while preserving the proven local-first data, booking, finance, backup, Google Drive sync and app-lock engines.

## Design system

- Brand: Homika
- Mark: mint/teal geometric home + H on deep dark background
- Visual character: calm, premium, operational, modern hospitality
- Primary: deep teal
- Accent: mint
- Surfaces: warm off-white in light mode; deep green-black in dark mode
- Cards: restrained outlines rather than heavy elevation
- Radius: 10–24 dp depending on hierarchy
- Typography: compact, high legibility, no oversized demo-style headings
- Main horizontal margins: 16 dp

## Navigation

Five fixed destinations remain Home, Calendar, Bookings, Money and More. The bottom bar is custom-built so labels stay on one line on narrow phones and under larger display/font scaling. Quick Add remains globally available.

## First-run experience

1. Welcome / brand
2. Malay or English
3. Optional Google Drive connection (same-account sync)
4. Optional local PIN
5. Add first homestay or enter dashboard

The onboarding completion flag is local-only and does not change database schema.

## Rebuilt surfaces in this pass

- App-wide design tokens, typography and shapes
- Launcher/brand mark
- Main navigation
- Welcome/onboarding
- Home dashboard
- Bookings list
- Money overview
- More/settings
- Shared headers, metrics, empty states, information cards and picker fields
- Spacing normalization on remaining screens

## Compatibility

The package remains `com.homiq.app`. Room schema stays version 1. Existing backup/sync identifiers are intentionally retained where required for compatibility.
