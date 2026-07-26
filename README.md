# Kheteebaadi — Android app (खेतीबाड़ी)

A native Android app (Kotlin + Jetpack Compose) that rebuilds **kheteebaadi.com** and
talks to the **same live PHP backend** (`https://kheteebaadi.com/api.php`), so the app and
website share the same accounts, products, orders, and support requests. No backend changes.

- **Package:** `com.kheteebaadi.app`
- **Min Android:** 7.0 (API 24) · **Target:** Android 14 (API 34)
- **Language:** Hindi-first with an instant **हिं / EN** toggle on every screen (choice is remembered)
- **Theme:** green `#2E7D32`, gold `#F9A825`, red `#C62828`, cream `#F4F6F2`

## How the app talks to the backend

- Every call is `POST https://kheteebaadi.com/api.php?action=<ACTION>` with a JSON body.
- Cookies (the PHP session) are persisted on-device, so the user stays logged in across restarts.
- "Write" actions send the CSRF token as the `X-CSRF-Token` header. The token comes from
  `bootstrap` / `login` / `register`.
- Responses are `{"ok":true,...}` or `{"ok":false,"error":"code"}`; error codes are shown as
  bilingual messages.

The location dataset (75 UP districts, 332 tehsils, 875 blocks) is bundled offline at
`app/src/main/assets/up_locations.json` and drives the District → Tehsil / District → Block
cascades on Create account and Edit profile.

## Build it (GitHub Actions — no Android Studio needed)

1. Create a new **empty** GitHub repository.
2. Push this project to it:
   ```bash
   cd kheteebaadi-android
   git init && git add . && git commit -m "Kheteebaadi Android app"
   git branch -M main
   git remote add origin https://github.com/<you>/<repo>.git
   git push -u origin main
   ```
3. Add the signing secrets (repo → **Settings → Secrets and variables → Actions → New repository secret**).
   Values are in the `kheteebaadi-signing-credentials.txt` file that was generated with this project:
   - `KEYSTORE_BASE64` — the full contents of `kheteebaadi-keystore.base64.txt`
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS` — `kheteebaadi`
   - `KEY_PASSWORD`
4. The workflow (`.github/workflows/android.yml`) runs on every push. Open the **Actions** tab →
   latest run → **Artifacts** to download:
   - `kheteebaadi-apk` → the installable **APK** (share with farmers to sideload)
   - `kheteebaadi-aab` → the **AAB** for Google Play
5. To cut a release with the files attached to a GitHub Release, push a tag:
   `git tag v1.0.0 && git push origin v1.0.0`.

> Before the secrets are added, CI still builds — it just falls back to a debug signature so you
> can test wiring. Add the secrets to get a properly signed release.

### Building locally (optional)
Open the folder in **Android Studio** (Giraffe+). It will use its bundled Gradle and JDK 17.
For a CLI build, install JDK 17 + Android SDK, then `gradle :app:assembleRelease` (or generate a
wrapper once with `gradle wrapper`). Put your signing values in a local `keystore.properties`
(`storeFile`, `storePassword`, `keyAlias`, `keyPassword`) — that file is git-ignored.

## Sideloading the APK now
Send the APK to farmers (WhatsApp/file share). On the phone: open the file, allow **Install from
unknown sources** when prompted, install. Only these permissions are requested: **Internet**,
**Camera** and **Photos** (for the support/produce photo uploads).

## Publishing to Google Play (later)
1. In Play Console, create the app, then upload the **AAB** to a testing track.
2. Enable **Play App Signing** (recommended) — Google holds the app signing key and your
   generated keystore becomes the recoverable **upload key**.
3. Complete the listing: title + short/long description (Hindi & English), phone screenshots,
   feature graphic, **privacy policy URL**, Data-safety form, content rating, target audience.
4. Bump `versionCode` / `versionName` in `app/build.gradle.kts` for each release.

## Screens implemented
Auth gate, Login, Register (District→Tehsil→Block cascades + crop chips), Home shortcuts,
Buy Inputs (catalog + category filter + cart with savings), Checkout (UPI QR + copyable UPI ID
+ numbered steps + "open UPI app" intent + Bank details + COD + "I have paid → place order"),
Orders (status + payment labels + cancel/remove), Sell Produce (5-stage tracker + payout choice
after Delivered), Support (analysis → specialist, photo upload, delete — never the word
"approval"), Learn (bilingual tips), Profile / Edit profile / Addresses, Lead Zone, and an Admin
panel (folders for Products, Farmers by district, Orders, Support, Produce with the documented
`admin_*` actions).

## Notes / points to verify against the live backend
These were implemented to the spec but a few backend contracts could not be confirmed from the
site and are isolated so they are easy to adjust:

- **`image_upload`** — sent as base64 JSON (`{data, name}`) expecting `{ok,url}`. If the site uses
  multipart or a different field name, change `AppViewModel.uploadImage()`.
- **Admin `admin_overview` field names** — the Admin screen reads tolerant keys
  (`users`/`farmers`, `orders`, `support`, `listings`, `products`). If the JSON uses different keys,
  adjust `arrOf(...)` in `ui/screens/Extra.kt`.
- **Learn** content is a bundled starter set of bilingual tips; swap in the site's articles.
- **Lead Zone** shows the group-order intro; wire the `listing_group` specifics when its shape is known.

## Project layout
```
app/src/main/java/com/kheteebaadi/app/
  data/    Api, PersistentCookieJar, Models, Locations, Prefs
  ui/      AppViewModel (state + all API actions), Strings (bilingual), AppRoot (nav + tabs)
  ui/theme, ui/components (buttons, cards, dropdowns, crop chips, stage tracker, QR)
  ui/screens (Auth, Home, Buy, Orders, Sell, Support, Profile, Learn, Extra, Photo)
app/src/main/assets/up_locations.json    bundled UP district/tehsil/block dataset
.github/workflows/android.yml            CI: builds signed APK + AAB
```
