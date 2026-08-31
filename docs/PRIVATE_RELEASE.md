# HOMIQU Private Release

HOMIQU V1 is distributed privately as an APK. It is not a Play Store product.

## Identity

- App display name: `HOMIQU`
- Android application ID remains: `com.homiq.app`
- V1 version: `1.0.0` (`versionCode 10000`)
- Release SHA-1: `48:46:FC:28:4B:53:09:21:D3:5D:6A:95:4D:10:A6:2C:49:C9:9A:77`
- Debug SHA-1 remains: `5B:FC:0E:63:6E:F3:06:80:F3:BD:A1:5D:4B:B9:93:C4:22:B1:48:D9`

Keeping `com.homiq.app` preserves the Room database, SharedPreferences, backup compatibility and OAuth package identity. Internal Kotlin class names may still use `Homiq`; these are implementation identifiers, not user-facing branding.

## Release signing

The private release key is **not stored in the repository**. `app/build.gradle.kts` reads release signing only from these environment variables:

- `HOMIQU_KEYSTORE_PATH`
- `HOMIQU_KEYSTORE_PASSWORD`
- `HOMIQU_KEY_ALIAS`
- `HOMIQU_KEY_PASSWORD`

The separate `private-release.yml` workflow reconstructs the keystore from GitHub Actions secret `HOMIQU_KEYSTORE_BASE64`, builds `assembleRelease`, verifies the APK certificate and uploads a versioned APK artifact.

Never commit the release keystore or the release-secret text file. Keep an offline copy. Losing the release key prevents future signed updates to installed private-release APKs.

## Google OAuth

The Phase 10 debug OAuth client remains valid for debug builds. Add a **second Android OAuth client** in the existing Google Cloud project for the private release certificate:

- Package: `com.homiq.app`
- SHA-1: `48:46:FC:28:4B:53:09:21:D3:5D:6A:95:4D:10:A6:2C:49:C9:9A:77`

Use the same Google Drive API and `drive.appdata` scope. No `google-services.json` is needed.

## First transition from Phase 10 debug to V1 release

The release certificate is intentionally different from the Phase 10 debug certificate. Android therefore cannot install V1 release directly over the Phase 10 debug APK.

1. Sync both phones and create a local backup.
2. Add the release Android OAuth client above.
3. Uninstall the old debug build.
4. Install the V1 private-release APK.
5. Connect the same Google account and sync, or restore the local backup.
6. Future HOMIQU private releases signed with this same release key will update V1 normally.

## Compatibility retained

- Existing backup parser magic remains `HOMIQ_BACKUP` so older backups restore.
- Existing hidden Drive sync filenames/IDs retain legacy `homiq` identifiers so the V1 release sees the already-synced data.
- Database schema remains version 1.
