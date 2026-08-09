# Ustad — Design System Reference (Jetpack Compose / Material 3)
Native Android | Kotlin + Jetpack Compose

This is the design system reference for native Android development. It translates the
visual design (from `Ustad_Designs.pdf`) into Compose-native theme definitions so every
screen the agent generates pulls from one consistent setup instead of hardcoding values
inline.

---

## 1. Brand

- **App name:** Ustad (Urdu for "Master Craftsman")
- **Tagline:** Mahir Karigar 30 Minute Me
- **Personality:** Trustworthy, warm, simple, fast — built for shopkeepers and
  tradespeople on small/low-end Android screens, not a dense power-user tool.
- **Design language:** Material 3 (Material You), adapted with rounded, friendly
  geometry rather than default Material dynamic color.

---

## 2. Color Tokens → `Color.kt`

```kotlin
package com.ustad.app.presentation.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF059669)
val PrimaryLight = Color(0xFFD1FAE5)
val SecondaryDark = Color(0xFF111827)
val Background = Color(0xFFF8FAFC)
val Surface = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1F2937)
val TextSecondary = Color(0xFF6B7280)
val BorderColor = Color(0xFFE5E7EB)
val Success = Color(0xFF10B981)
val Warning = Color(0xFFF59E0B)
val Error = Color(0xFFEF4444)
```

## 3. `ColorScheme` mapping → `Theme.kt`

Do **not** use `dynamicColorScheme` (Material You per-device theming) — this app needs a
fixed brand identity across every device for trust/consistency reasons.

```kotlin
private val UstadLightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderColor,
    error = Error,
    onError = Color.White
)

private val UstadWorkerDarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    background = SecondaryDark,
    onBackground = Color.White,
    surface = SecondaryDark,
    onSurface = Color.White
)
```

Use `UstadLightColorScheme` for all customer-facing screens and the worker's non-header
content areas. Use `UstadWorkerDarkColorScheme` (or simply set the header
`Surface`/`Scaffold` background to `SecondaryDark` directly) specifically for the Worker
Dashboard header — the rest of the worker app stays on the light scheme, matching the
original design intent (dark header, light content below).

Do not create a full app-wide dark mode toggle from this — `SecondaryDark` is a
**brand color for the worker context specifically**, not a system dark theme.

---

## 4. Typography → `Type.kt`

Font: **Inter** (bundle as a variable font resource, or fall back to `Poppins` if Inter
isn't available as a Compose-compatible font resource). Use `FontFamily` + `Font(R.font....)`.

| Style token | Compose `Typography` slot | Size | Weight |
|---|---|---|---|
| Heading | `headlineSmall` | 20sp | Bold (700) |
| Subheading | `titleMedium` | 16sp | Medium (500) |
| Body | `bodyMedium` | 14sp | Regular (400) |
| Caption | `labelSmall` | 12sp | Regular (400) |

```kotlin
val UstadTypography = Typography(
    headlineSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 12.sp)
)
```

---

## 5. Shape & Spacing → `Shape.kt`

```kotlin
val UstadShapes = Shapes(
    small = RoundedCornerShape(12.dp),   // chips, small badges
    medium = RoundedCornerShape(16.dp),  // cards, inputs, buttons, sheets — DEFAULT
    large = RoundedCornerShape(24.dp)    // large sheets/dialogs only if needed
)
```

- **Corner radius:** 16dp default on all cards, buttons, inputs, bottom sheets.
  Fully circular (`CircleShape`) only for avatars, icon badges, and toggle switches.
- **Minimum touch target:** 48dp — enforce via `Modifier.heightIn(min = 48.dp)` on all
  tappable rows/buttons. This is a low-dexterity, low-literacy-friendly app; do not
  ship anything below this.
- **Spacing scale:** use a single `object Spacing { val xs=4.dp; val sm=8.dp; val md=12.dp;
  val lg=16.dp; val xl=24.dp; val xxl=32.dp }` object — reference these everywhere
  instead of ad hoc `.dp` literals scattered through Composables.
- **Elevation/shadow:** `Modifier.shadow(4.dp, RoundedCornerShape(16.dp), spotColor =
  Color.Black.copy(alpha = 0.05f))` to approximate the soft `0 4px 12px rgba(0,0,0,0.05)`
  card shadow from the original spec.

---

## 6. Core Reusable Composables

Build these once in `presentation/components/` and reuse everywhere — do not let
individual screens redefine their own button or card styles.

- **`UstadPrimaryButton`** — solid `Primary` fill, white text, 48dp height, `medium` shape.
- **`UstadSecondaryButton`** — `Primary`-outlined border, `Primary` text, transparent fill.
- **`CategoryCard`** — square-ish card, circular icon badge (`PrimaryLight` background,
  accent-colored icon), label below in `titleMedium`.
- **`WorkerCard`** — 48dp circular avatar, bold name, green "Verified" pill badge, rating
  row (star icon + score + job count + distance string), two side-by-side buttons
  (`UstadSecondaryButton` "Call" / `UstadPrimaryButton` "Book Now").
- **`StatusPill`** — rounded-full `Surface`, `labelSmall` text, color by state
  (`Warning`=pending, `Success`=completed, `Error`=cancelled).
- **`StepIndicator`** — small `labelSmall` caption row, e.g. "Step 2 of 3 · Service
  Selected".
- **`DashedUploadBox`** — dashed border (`Modifier.border` with a custom `DashedShape` or
  `drawBehind` dash pattern), centered camera icon in a `PrimaryLight` circular badge,
  helper caption below.
- **`VoiceRecorderBar`** — pill-shaped `Surface`, mic icon circle, animated waveform bars
  in `Primary` while recording, duration counter aligned end.
- **`StatusTimeline`** — vertical `Column` of connected dots (`Canvas` or a simple
  `Divider` + `Circle` composition), filled `Primary` for completed steps, outlined gray
  for upcoming, current step visually highlighted.
- **`OnlineToggleSwitch`** — large iOS-style `Switch` (custom-styled Material `Switch`),
  green thumb/track when ON, "ONLINE"/"OFFLINE" `labelSmall` caption above.
- **`IncomingJobCard`** — white card, glowing green border (`Modifier.border(2.dp,
  Success, medium shape)` + optional pulse `Animatable` alpha loop), "LIVE · NEW JOB"
  `StatusPill` at top, Accept (`UstadPrimaryButton`)/Reject (`UstadSecondaryButton`
  in `Error` color) buttons.
- **`AdminStatCard`** — same visual shell as the worker dashboard's stat cards (white
  card, bold large number, `labelSmall` caption below), reused for Overview screen
  metrics (Total Users, Active Jobs, Pending Verifications, etc).
- **`VerificationQueueItem`** — list row: worker name + skill `StatusPill`, experience
  years caption, a small "View" chevron leading to the detail screen. Keep this list
  dense — an admin scanning many rows should not need to scroll through card-sized
  spacing.
- **`DocumentImageViewer`** — full-width tappable image (opens a zoomable full-screen
  view) for CNIC front/back and selfie review on the Verification Detail screen.

---

## 7. Iconography

Use **Material Symbols/Icons Extended** (`androidx.compose.material:material-icons-extended`)
in **filled/rounded** style — never `Icons.Outlined` hairline variants, which are hard to
see on cheap, sun-glared screens.

- Electrician → `Icons.Rounded.Bolt`
- Plumber → `Icons.Rounded.WaterDrop`
- AC → `Icons.Rounded.AcUnit`
- Carpenter → a hammer/tools icon (custom vector asset if not in the default icon set)
- Painter → a paint-roller icon (custom vector asset)
- Bike Mechanic → `Icons.Rounded.TwoWheeler` or a custom wrench+bike composite

Status icons: `Icons.Rounded.LocationOn`, `Icons.Rounded.Schedule`,
`Icons.Rounded.CameraAlt`, `Icons.Rounded.Mic`, `Icons.Rounded.Star`,
`Icons.Rounded.CheckCircle` (verified).

---

## 8. Two App Contexts, One Theme

**Customer screens:** `UstadLightColorScheme` throughout — `Background` (#F8FAFC) base,
white `Surface` cards, `Primary` green accents.

**Worker screens:** Dashboard header specifically uses `SecondaryDark` (#111827) with
white text and `Success` green accents (online toggle, stat highlights); all content
below the header (Requests list, Active Job, Profile) uses the same light theme as the
customer app. Do not implement this as a full system dark-mode toggle — it's a fixed
brand treatment for the worker dashboard header only.

---

## 9. Screen Inventory

**Customer:** Splash → Phone OTP Login → Role Selection → Home → Create Job Request →
Finding Nearby Ustad → Job Tracking → Rating → Job History → Profile

**Worker:** Dashboard (dark header) → Requests → Active Job → Verification (CNIC +
selfie capture) → Worker Profile

**Admin:** Overview (stat cards) → Verification Queue → Worker Verification Detail
(CNIC/selfie viewer + Approve/Reject) → Job Monitor (filterable list) → Reports
(Phase 2)

**Admin visual treatment:** reuse the same light `UstadLightColorScheme` as the
customer app — do not give Admin its own dark theme or a visually "separate product"
feel. It should read as an internal tool built with the same component library
(`UstadPrimaryButton`, `StatusPill`, card shells), just with denser, list-oriented
layouts appropriate for an operator reviewing many records quickly. The Overview
screen's stat cards can reuse the same card component as the Worker Dashboard's
"Today / Rating" stat cards.

Reference `Ustad_Designs.pdf` for exact layout/spacing per screen — this file governs
the *tokens and component structure*, the PDF governs the *visual layout*.

---

## 10. Rules for Consistency

- Never introduce a new `Color(0x...)` literal outside Section 2 — always reference the
  named token.
- Never use a corner radius other than the `UstadShapes.medium` (16dp) default, except
  fully circular elements.
- Every screen uses `UstadTypography` slots — no inline `TextStyle(fontSize = ...)`
  overrides scattered through screens.
- Worker-facing screens keep the dark-header-only pattern; do not extend `SecondaryDark`
  into customer screens or into the rest of the worker app.
- Reuse the exact `WorkerCard` and `CategoryCard` Composables everywhere they appear —
  do not let each screen redefine its own version of these components.
