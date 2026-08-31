# Homika Private APK Updater

Homika uses GitHub Releases as the zero-cost update source for private APK distribution.

## Update source

- Repository: `IzokaGM/HOMIQ`
- Latest release endpoint: `https://api.github.com/repos/IzokaGM/HOMIQ/releases/latest`
- Recommended release tag: `v1.0.1`, `v1.1.0`, etc.
- Recommended APK asset name: `Homika-v1.0.1.apk`
- Draft and prerelease builds are not returned by GitHub's `releases/latest` endpoint.

## Runtime flow

1. After first-run setup is complete, Homika silently checks for a newer GitHub Release at most once every six hours.
2. `More -> Homika vX.Y.Z -> Check update` can force an immediate manual check.
3. If a newer semantic version is found, Homika shows the release notes and asks before downloading.
4. The APK downloads into Homika's private cache directory.
5. Before installation Homika verifies:
   - the downloaded asset is a parseable APK;
   - package name is exactly `com.homiq.app`;
   - APK `versionCode` is newer than the installed build;
   - signing certificate matches the currently installed Homika certificate.
6. Android's PackageInstaller performs the actual install and still requires user confirmation.
7. On Android 8+, if Homika is not yet allowed to install apps from this source, Homika opens the system `Install unknown apps` settings page. The user remains in control of that permission.

## Security model

The updater never silently installs an APK. Homika verifies the APK identity/signature before handing it to Android, and Android presents its own install confirmation UI.

No GitHub token, account password, API secret, or updater signing secret is embedded in the app.

## Signing requirement

All upgrade-compatible APKs must be signed with the same certificate.

The current development/debug builds use the stable Homika debug certificate. The future private release certificate will be different. Therefore the first move from a debug-signed Homika to the final release-signed Homika may require:

1. Backup Homika data.
2. Uninstall the debug build.
3. Install the first release-signed APK.
4. Restore data if necessary.

After that first release installation, every later private release must keep the same release signing certificate so in-app updates install over the existing app without removing data.

## Release checklist

For each update:

1. Increase `versionCode`.
2. Increase `versionName`.
3. Build the signed release APK with the stable private release key.
4. Create a GitHub Release using the matching version tag.
5. Attach the signed `Homika-vX.Y.Z.apk` asset.
6. Add concise release notes.
7. Test manual `Check update` from the previous release before sharing the APK through Telegram.

## Repository visibility

The updater intentionally uses GitHub's unauthenticated release API so no reusable GitHub credential is shipped inside the APK. The release endpoint/assets therefore need to be publicly readable. If the main source repository is ever made private, use a separate public releases-only repository rather than embedding a GitHub personal access token in Homika.
