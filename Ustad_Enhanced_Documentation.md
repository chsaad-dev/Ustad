# Ustad — Hyperlocal Skilled Worker Marketplace
### Native Android Documentation — Kotlin + Jetpack Compose (Build-Ready for Antigravity)
Version 3.0 | Native Android · Firebase Backend (existing project, unchanged)
Author: For Ch Saad | Android Developer

---

## 0. Project Foundation

This app connects to an existing Firebase project (Auth, Firestore, Storage, FCM already
provisioned). This documentation defines the full native Android client to be built
against that backend, using Kotlin and Jetpack Compose throughout.

---

## 1. Executive Summary

**App Name:** Ustad (Urdu: "Master Craftsman")
**Tagline:** *Mahir Karigar 30 Minute Me*
**Problem:** In Tier 2/3 Pakistani cities, finding a reliable electrician, plumber, or AC
technician still depends on referrals, shop visits, and long waits.
**Solution:** A native Android app connecting customers with verified nearby Ustads in
under 5 minutes, using voice notes, photos, and auto-location — no advance payment, no
commission in the MVP, direct call-or-book.
**Platform decision:** Android-only, pure Kotlin. Pakistan's smartphone market is
overwhelmingly Android; going native gives full control over foreground services,
notification channels, and performance on low-end devices without any cross-platform
compromise.
**Target Market:** Sahiwal, Punjab first (pilot city), then Okara, Pakpattan.

---

## 2. User Personas

**Persona 1 — Customer: Ali, 32, Sahiwal.** Small shop owner, low English literacy,
prefers voice notes. Phone: Infinix Hot 12, Android 11, low storage, patchy 3G.

**Persona 2 — Worker: Bashir, Electrician, 40.** 15 years' experience, not tech-savvy,
wants big buttons and a loud, unmissable notification. Phone: Tecno Spark.

**Persona 3 — Admin/Ops: Saad (you).** Verifies workers, monitors job volume, and
watches for abuse reports — now through a dedicated Admin section inside the same app
(Section 4A), provisioned to your account only via a Firebase custom claim, not through
public signup.

---

## 3. Goals, Non-Goals, Success Metrics

**Goals:** 100 verified Ustads in Sahiwal; job creation under 45 seconds; worker
notification under 10 seconds; booking success rate >70%; smooth performance on 2GB RAM
devices on 3G.

**Non-Goals for MVP:** no in-app payments/wallet/commission, no bidding, no in-app chat
(phone call is the channel), no dedicated admin app (Firebase Console + later web panel).

**Metrics to track:** jobs/day, job→accept conversion, time-to-accept (median/p90),
worker 7/30-day retention, rating distribution, cancellation rate + reasons.

---

## 4. App Structure & Navigation

Single Android app. **Three user types share this app: Customer, Technician (Worker),
and Admin.** Only Customer/Worker/Both are selectable through the public signup flow —
Admin is never a self-selectable option (see Section 4A for why, and how admin access
is actually granted).

```
MainActivity (single-activity, Compose Navigation)
├── Auth Graph
│   ├── SplashScreen
│   ├── LoginScreen (Phone OTP)
│   ├── OtpVerificationScreen
│   ├── ProfileSetupScreen
│   └── RoleSelectScreen        (Customer / Worker / Both — Admin NOT listed here)
├── Customer Graph (bottom nav: Home | My Jobs | History | Profile)
│   ├── HomeScreen
│   ├── CreateJobScreen
│   ├── FindingUstadScreen
│   ├── JobTrackingScreen
│   ├── HistoryScreen
│   └── ProfileScreen
├── Worker Graph (bottom nav: Dashboard | Requests | My Jobs | Profile)
│   ├── DashboardScreen
│   ├── RequestsScreen
│   ├── ActiveJobScreen
│   ├── VerificationScreen
│   └── WorkerProfileScreen
└── Admin Graph (bottom nav: Overview | Verifications | Jobs | Reports)
    ├── AdminOverviewScreen        (stat cards: total users, active jobs, pending verifications)
    ├── VerificationQueueScreen    (list of unverified workers)
    ├── WorkerVerificationDetailScreen  (CNIC front/back, selfie, Approve/Reject)
    ├── AdminJobMonitorScreen      (all jobs, filterable by status/city/category)
    └── AdminReportsScreen         (Phase 2 — abuse reports queue)
```

Start destination after login is resolved by checking, in order: (1) the Firebase Auth
**custom claim** `admin == true` → Admin Graph; otherwise (2) `users/{uid}.role` from
Firestore → `customer`/`worker` route directly, `both` defaults to Customer Graph with a
role switch in Profile. The admin check must use the custom claim, never the Firestore
`role` field — see Section 4A.

---

## 4A. Admin Role & Authorization Model

**Why Admin can't just be a value in the `role` field:** the `users/{userId}` document
is client-writable (a user needs to update their own name, city, language). If `role`
were the only gate and clients could write it, any user could set `role: "admin"` on
their own document via a tampered request and gain full access to worker verification,
job data, and reports. This is a real, common mobile-app vulnerability — client-supplied
authorization is not authorization.

**The fix — two-tier role model:**

1. **`role` field in Firestore** (`customer` | `worker` | `both`) — controls UX/routing
   only. Client-writable, low stakes, purely about which screens a user sees.
2. **`admin` custom claim on the Firebase Auth token** — controls actual authorization.
   Custom claims can only be set server-side (Firebase Admin SDK), never by the client
   app itself. This is what security rules and privileged UI actually check.

**How you grant yourself (or a future ops hire) admin access:**
- Write a small one-off Node.js script (or a callable Cloud Function you invoke
  manually, never exposed to the client app) using the Firebase Admin SDK:
  ```js
  admin.auth().setCustomUserClaims(uid, { admin: true });
  ```
- Run this once per admin account from a trusted environment (your machine, or a
  protected Cloud Function you call directly via the Firebase CLI/console — not a route
  the app exposes). The user must sign out/in once (or force-refresh their ID token) for
  the new claim to take effect.
- There is intentionally **no in-app flow** to become an admin. This is a permanent
  design rule, not a Phase 1 shortcut to revisit later.

**Client-side check:**
```kotlin
suspend fun isAdmin(): Boolean {
    val result = FirebaseAuth.getInstance().currentUser
        ?.getIdToken(true)?.await()
    return result?.claims?.get("admin") == true
}
```
Call this once after login to decide whether to route into the Admin Graph. Also
re-verify server-side (security rules) for every privileged write — never trust a client
routing decision as the actual security boundary.

**Admin capabilities in this app:**
- View pending worker verifications (CNIC front/back, selfie) and Approve/Reject —
  writes `workers/{id}.isVerified`.
- View aggregate stats: total users, total jobs by status, jobs created today/this week.
- Browse/filter all jobs (for support/dispute purposes) — read-only in MVP.
- View abuse reports (Phase 2, once `reports` collection exists) and mark
  reviewed/actioned.

**Explicitly NOT in the Admin panel for MVP:** editing other users' profiles, deleting
jobs, refunds/payments (none exist), banning users (flag for Phase 2 at most — banning
needs its own careful design, not bolted on here).

---

## 5. Feature List — Phase by Phase

### Phase 0 — Project Setup
Android Studio project, package structure, Hilt setup, Compose theme, Firebase
(existing project — just re-download `google-services.json`), navigation skeleton,
Firebase Emulator Suite config for local dev.

### Phase 1 — MVP Core
Phone OTP auth, profile + role setup, Home screen, Create Job (description, template
chips, photo upload compressed <800kb, voice note capped 60s, geohash location), Find
Nearby Workers (geohash + skill + isOnline + isVerified query, Haversine distance sort),
Call (Intent.ACTION_DIAL), Book flow (`preferredWorkerId` → real worker Accept sets
`workerId`+`status`), FCM notifications, Job Tracking timeline, Rating, Job History,
worker online/offline toggle with ForegroundService.

### Phase 1.5C — Admin Panel
Custom-claim-gated Admin Graph inside this app (see Section 4A): Overview stats,
Worker Verification Queue with Approve/Reject, Job Monitor (read-only), Reports queue
(wired up once the `reports` collection exists in Phase 2).

### Phase 2 — Polish & Trust
Voice note waveform playback, Urdu/English string resources (`values-ur/strings.xml`),
Home search, edit/cancel job, report flow (feeds the Admin Reports queue), Trust Score
(Cloud Function), after-photo on completion, urgent job toggle, "Available Now" browse
map.

### Phase 3 — Growth
Referral codes, city waitlist screen, opt-in re-engagement notifications, worker
leaderboard, on-device speech-to-text preview (`SpeechRecognizer` API).

### Phase 4+ — Monetization (post-validation)
In-app chat, wallet/Easypaisa/JazzCash, premium worker badge, referral rewards,
city expansion.

---

## 6. Tech Stack

**Language:** Kotlin (latest stable)
**UI:** Jetpack Compose + Material 3
**Architecture:** MVVM + Clean Architecture — `data` (Firestore/Storage sources,
repository implementations), `domain` (models, use-cases where logic branches),
`presentation` (Composable screens, ViewModels, UI state)
**DI:** Hilt
**Async:** Kotlin Coroutines + Flow (Firestore snapshots exposed as `Flow<T>` via
`callbackFlow`)
**Navigation:** Navigation Compose, with a sealed `Screen` class for type-safe routes
**Backend (unchanged):** Firebase Auth (Phone OTP), Firestore, Cloud Storage, Cloud
Functions (Node/TypeScript, unchanged), FCM, Remote Config, Crashlytics, App Check
**Location:** FusedLocationProviderClient (Google Play Services location), a small
custom Kotlin geohash encoder/decoder (or GeoFirestore-Android)
**Maps:** Maps SDK for Android (`com.google.android.gms:play-services-maps`),
`Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=lat,lng"))` for navigation
handoff
**Media:** CameraX or standard `Intent.ACTION_IMAGE_CAPTURE`/`ACTION_PICK` for photos,
`MediaRecorder` for voice notes, `MediaPlayer`/`ExoPlayer` for playback, a
`Compressor`-style Bitmap compression before Storage upload
**Notifications:** FCM + `NotificationChannel` (high-importance, custom sound, vibration
pattern) for the "urgent job" channel specifically
**Background/online-status:** `ForegroundService` (worker online session) +
`BroadcastReceiver` for `BOOT_COMPLETED` to prompt re-login/online state after reboot
**Testing:** JUnit + Turbine (Flow testing) + Espresso/Compose UI Test + Firebase
Emulator Suite for repository-level integration tests

**Why native Android:** a persistent foreground service to keep workers "online," and a
custom high-priority notification channel with vibration, are first-class Android APIs
with no cross-platform overhead. Combined with an Android-only target market, native
Kotlin gives full control over performance and background behavior on low-end devices.

---

## 7. System Architecture (Logical View)

```
┌─────────────────────────────┐
│   Compose UI (Screens)       │
│   collects StateFlow from    │
│   ViewModels                  │
└───────────┬───────────────────┘
            │
┌───────────▼───────────────────┐
│   ViewModel (per screen)       │
│   exposes UiState, handles     │
│   user intents via UseCases    │
└───────────┬───────────────────┘
            │
┌───────────▼───────────────────┐
│   Domain (UseCases, Models)    │
│   pure Kotlin, no Android deps │
└───────────┬───────────────────┘
            │
┌───────────▼───────────────────┐
│   Repository (interface +      │
│   FirebaseXRepositoryImpl)     │
└───────────┬───────────────────┘
            │
┌───────────▼───────────────────┐
│  Firebase: Auth / Firestore /  │
│  Storage / FCM  (existing      │
│  project — unchanged)          │
└─────────────────────────────────┘
            │ triggers
┌───────────▼───────────────────┐
│  Cloud Functions (unchanged,   │
│  TypeScript) — onJobCreated,   │
│  onJobStatusUpdate, etc.       │
└─────────────────────────────────┘
```

---

## 8. Database Schema (Firestore)

Your existing Firebase project already has this schema live. No migration needed —
only the client reading/writing it is being rebuilt.

### `users`
```
docId = uid
{
  name: string, phone: string, role: 'customer'|'worker'|'both', // UX routing only —
  // never the security boundary; see Section 4A for the admin custom-claim model
  photoUrl: string,
  createdAt: timestamp, fcmToken: string, language: 'en'|'ur', city: string,
  referralCode: string, referredBy: string|null
}
```

### `workers`
```
docId = uid
{
  userId: string, displayName: string, skills: array<string>, experienceYears: int,
  cnicFrontUrl: string, cnicBackUrl: string, selfieUrl: string, isVerified: bool,
  verificationTier: 'basic'|'pro', isOnline: bool, rating: double, trustScore: double,
  totalJobs: int, completedJobs: int, cancelledJobs: int,
  avgResponseTimeSeconds: int, location: GeoPoint, geohash: string, address: string,
  bio: string, lastOnlineAt: timestamp
}
```

### `jobs`
```
docId = auto
{
  customerId: string, workerId: string|null, preferredWorkerId: string|null,
  category: string, description: string, templateTag: string|null, isUrgent: bool,
  photoUrls: array<string>, afterPhotoUrls: array<string>, voiceUrl: string|null,
  location: GeoPoint, geohash: string, address: string, city: string,
  status: 'pending'|'accepted'|'onTheWay'|'workStarted'|'completed'|'cancelled'|'rejected',
  createdAt: timestamp, acceptedAt: timestamp|null, completedAt: timestamp|null,
  cancellationReason: string|null, isRated: bool
}
```

### `ratings`
```
{ jobId, customerId, workerId, rating: int(1-5), comment, createdAt }
```

### `reports` (Phase 2)
```
{ reportedBy, reportedUser, jobId, reason, note, createdAt, status: 'open'|'reviewed' }
```

**Composite indexes:** `workers`: `skills` + `geohash` + `isOnline` + `isVerified`;
`jobs`: `customerId`+`status`, `workerId`+`status`.

---

## 9. Security Rules

```js
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId
                   // A user can never grant themselves admin via this doc —
                   // the 'admin' custom claim (not this field) is the real gate.
                   && request.resource.data.role in ['customer', 'worker', 'both'];
    }

    match /workers/{workerId} {
      allow read: if request.auth != null;
      allow update: if request.auth != null && (
        // the worker can edit their own profile but never their own verification status
        (request.auth.uid == workerId &&
         !('isVerified' in request.resource.data.diff(resource.data).affectedKeys())) ||
        // only an admin custom claim can flip isVerified
        request.auth.token.admin == true
      );
      allow create: if request.auth != null && request.auth.uid == workerId;
    }

    match /jobs/{jobId} {
      allow read: if request.auth != null &&
        (request.auth.uid == resource.data.customerId ||
         request.auth.uid == resource.data.workerId ||
         request.auth.uid == resource.data.preferredWorkerId);

      allow create: if request.auth != null &&
        request.auth.uid == request.resource.data.customerId;

      allow update: if request.auth != null && (
        request.auth.uid == resource.data.customerId ||
        request.auth.uid == resource.data.workerId ||
        (resource.data.workerId == null &&
         resource.data.status == 'pending' &&
         request.resource.data.workerId == request.auth.uid &&
         request.resource.data.status == 'accepted')
      );
    }

    match /ratings/{ratingId} {
      allow create: if request.auth != null &&
        request.auth.uid == request.resource.data.customerId;
      allow read: if request.auth != null;
    }

    match /reports/{reportId} {
      allow create: if request.auth != null;
      // only admins can read/triage the reports queue
      allow read, update: if request.auth != null && request.auth.token.admin == true;
    }
  }
}
```

**Admin read access to `jobs` for the monitor screen:** rather than loosening the
existing `jobs` rule (which is deliberately scoped to the customer/worker/preferredWorker
involved), fetch aggregate/browsable job data for the Admin Job Monitor screen through a
callable Cloud Function that runs with Admin SDK privileges server-side, rather than a
direct client Firestore query. This keeps the `jobs` collection's client-facing rules
simple and tight, and keeps admin browsing logic (filtering, pagination) off the phone
and in one place you control.

Always wrap the client-side "accept" write in a Firestore transaction that re-checks
`status == 'pending'` before committing, so a losing worker gets a clean "already
taken" message instead of a raw permission error (see Section 14).

---

## 10. Cloud Functions

These are backend-agnostic and require no changes for the Kotlin rebuild:
1. `onJobCreated` — geohash + skill match, FCM fan-out to online verified workers.
2. `onJobStatusUpdate` — notifies the opposite party on status change.
3. `updateWorkerLocation` — callable, invoked every 2 min while worker is online.
4. `cleanupOfflineWorkers` — scheduled every 15 min, flips stale `isOnline` to false.
5. `recalculateTrustScore` (Phase 2) — on rating/completion.
6. `rateLimitJobCreation` — max 5 job creates/hour/user.

---

## 11. Package Structure (Kotlin/Android)

```
com.ustad.app/
├── UstadApplication.kt              (Hilt @HiltAndroidApp)
├── MainActivity.kt                   (single Activity, sets up NavHost)
├── di/
│   ├── FirebaseModule.kt             (provides FirebaseAuth, Firestore, Storage, FCM)
│   ├── RepositoryModule.kt           (binds repository interfaces to impls)
│   └── LocationModule.kt
├── navigation/
│   ├── Screen.kt                     (sealed class of routes)
│   ├── AuthNavGraph.kt
│   ├── CustomerNavGraph.kt
│   ├── WorkerNavGraph.kt
│   └── AdminNavGraph.kt
├── core/
│   ├── location/LocationService.kt
│   ├── geohash/GeohashHelper.kt
│   ├── notification/NotificationHelper.kt   (channels, FCM handling)
│   ├── media/ImageCompressor.kt
│   ├── media/VoiceRecorderManager.kt
│   └── util/DistanceHelper.kt        (Haversine)
├── data/
│   ├── model/
│   │   ├── UserModel.kt
│   │   ├── WorkerModel.kt
│   │   └── JobModel.kt
│   ├── remote/
│   │   ├── AuthRemoteSource.kt
│   │   ├── JobRemoteSource.kt
│   │   ├── WorkerRemoteSource.kt
│   │   └── StorageRemoteSource.kt
│   └── repository/
│       ├── AuthRepositoryImpl.kt
│       ├── JobRepositoryImpl.kt
│       └── WorkerRepositoryImpl.kt
├── domain/
│   ├── repository/                   (interfaces: AuthRepository, JobRepository, WorkerRepository)
│   └── usecase/
│       ├── CreateJobUseCase.kt
│       ├── AcceptJobUseCase.kt       (wraps the transactional claim logic)
│       ├── FindNearbyWorkersUseCase.kt
│       └── RateWorkerUseCase.kt
├── presentation/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   ├── Shape.kt
│   │   └── Theme.kt
│   ├── components/                   (reusable Composables: UstadButton, WorkerCard,
│   │                                   CategoryCard, StatusTimeline, VoiceRecorderBar)
│   ├── auth/                         (SplashScreen, LoginScreen, OtpScreen,
│   │                                   ProfileSetupScreen, RoleSelectScreen + ViewModels)
│   ├── customer/
│   │   ├── home/
│   │   ├── createjob/
│   │   ├── finding/
│   │   ├── tracking/
│   │   ├── history/
│   │   └── profile/
│   ├── worker/
│   │   ├── dashboard/
│   │   ├── requests/
│   │   ├── activejob/
│   │   ├── verification/
│   │   └── profile/
│   └── admin/
│       ├── overview/           (AdminOverviewScreen — stat cards)
│       ├── verification/       (VerificationQueueScreen, WorkerVerificationDetailScreen)
│       ├── jobs/                (AdminJobMonitorScreen)
│       └── reports/             (AdminReportsScreen — Phase 2)
└── service/
    └── WorkerOnlineForegroundService.kt
```

---

## 12. Repository Contracts (Domain Layer)

```kotlin
interface JobRepository {
    fun watchJob(jobId: String): Flow<Job>
    fun watchCustomerJobs(customerId: String): Flow<List<Job>>
    fun watchPendingJobsForWorker(skills: List<String>, geohashPrefix: String): Flow<List<Job>>
    suspend fun createJob(job: Job): Result<String>
    suspend fun requestWorker(jobId: String, workerId: String): Result<Unit>   // sets preferredWorkerId
    suspend fun acceptJob(jobId: String, workerId: String): Result<Unit>        // transactional claim
    suspend fun updateStatus(jobId: String, status: JobStatus): Result<Unit>
    suspend fun cancelJob(jobId: String, reason: String): Result<Unit>
}

interface WorkerRepository {
    fun watchNearbyWorkers(origin: GeoPoint, category: String, radiusKm: Double): Flow<List<Worker>>
    suspend fun setOnlineStatus(workerId: String, isOnline: Boolean): Result<Unit>
    suspend fun updateLocation(workerId: String, location: GeoPoint): Result<Unit>
    suspend fun submitVerificationDocs(workerId: String, docs: VerificationDocs): Result<Unit>
}

interface AuthRepository {
    suspend fun sendOtp(phone: String): Result<String>            // returns verificationId
    suspend fun verifyOtp(verificationId: String, code: String): Result<FirebaseUser>
    fun authStateChanges(): Flow<FirebaseUser?>
    suspend fun createOrUpdateUserProfile(user: UserModel): Result<Unit>
    suspend fun isAdmin(): Boolean   // checks the 'admin' custom claim, see Section 4A
}

interface AdminRepository {
    fun watchPendingVerifications(): Flow<List<Worker>>          // isVerified == false
    suspend fun approveWorker(workerId: String): Result<Unit>     // sets isVerified = true
    suspend fun rejectWorker(workerId: String, reason: String): Result<Unit>
    suspend fun fetchOverviewStats(): Result<AdminStats>          // via callable Cloud Function
    suspend fun fetchJobs(filter: JobFilter): Result<List<Job>>   // via callable Cloud Function
    fun watchReports(): Flow<List<Report>>                        // Phase 2
    suspend fun markReportReviewed(reportId: String): Result<Unit>
}
```

Antigravity note: generate the interfaces in `domain/repository/` first, then
`FirebaseJobRepositoryImpl` etc. in `data/repository/` implementing them — this keeps
ViewModels testable against fake repositories without touching real Firestore.

---

## 13. Compose Theme — Wiring to design.md Tokens

`presentation/theme/Color.kt` should define exactly these tokens (see the companion
`design_compose.md` file for the full Compose-specific design system):

```kotlin
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

`Shape.kt` uses `RoundedCornerShape(16.dp)` for cards/buttons/inputs/sheets (fully
circular only for avatars/toggles/icon badges). Buttons are `48.dp` minimum height.

---

## 14. Edge Cases & Concurrency

- **Location permission denied:** manual city/address fallback screen.
- **No workers nearby:** "No Ustad online within 5km — expand to 10km?" (widen geohash
  query radius).
- **Worker rejects:** removed from that worker's local feed only; job remains visible to
  other matching workers (no document change).
- **Double-accept race:** two workers tapping Accept near-simultaneously — handled by
  the security rule (Section 9) rejecting the second write, AND by wrapping the client
  call in a Firestore `runTransaction` that re-reads `status` before committing, so the
  losing worker sees a clear "This job was already accepted" message rather than a raw
  permission error or silent failure. Test this explicitly with two devices/emulators.
- **Customer cancels after acceptance:** notify worker immediately with reason.
- **Photo size:** compress to <800kb before upload.
- **Voice note:** hard cap at 60 seconds in `MediaRecorder` config.
- **Abuse prevention:** max 5 job creations/hour/user, enforced in `onJobCreated`
  Cloud Function or a `beforeCreate`-style check.
- **App killed while worker online:** `cleanupOfflineWorkers` scheduled function flips
  stale `isOnline` after 30 min; `ForegroundService` + `BOOT_COMPLETED` receiver handle
  the client side of staying "seen as online" during normal use.

---

## 15. Testing Strategy

- **Unit tests (JUnit):** UseCases, `GeohashHelper`, `DistanceHelper`, Trust Score
  calculation, job status transition guards — pure Kotlin, no Android framework deps.
- **Flow testing (Turbine):** repository Flow emissions (e.g. `watchPendingJobsForWorker`
  emitting correctly as Firestore snapshots change).
- **Compose UI tests:** Create Job form validation, WorkerCard rendering, StatusTimeline
  state changes.
- **Integration tests:** full customer→worker loop against the **Firebase Emulator
  Suite** (Auth, Firestore, Storage) — never against production Firebase during
  development.
- **Manual device checklist:** 2GB RAM device, 3G throttled, permission-denied paths,
  app killed while worker online, notification delivery with screen off, the
  double-accept race condition with two physical/emulated devices.

---

## 16. Antigravity Build Plan — Phase-by-Phase Tickets (Kotlin/Compose)

> Feed one phase at a time. Each ticket: **Goal**, **Touches**, **Done when…**

### PHASE 0 — Scaffold
1. **New Android Studio project (Kotlin, Compose, min SDK 24+)** — Done when: app
   builds and runs a placeholder Compose screen.
2. **Hilt setup** — Touches: `UstadApplication.kt`, `di/*`. Done when: a sample
   ViewModel receives an injected dependency without error.
3. **Reconnect existing Firebase project** — download fresh `google-services.json`
   from the Firebase Console for this new Android package name (confirm the package
   name matches what's registered, or register a new Android app under the same
   Firebase project). Done when: `Firebase.initialize` succeeds and Firestore reads
   work against the existing project data (e.g. any workers seeded previously).
4. **Compose theme** — Touches: `presentation/theme/*`. Done when: `Color.kt`,
   `Type.kt`, `Shape.kt`, `Theme.kt` match Section 13 and a sample `UstadButton`
   Composable renders correctly.
5. **Navigation skeleton** — Touches: `navigation/*`. Done when: Auth → Customer graph,
   Auth → Worker graph, and Auth → Admin graph all navigate with placeholder screens and
   bottom nav bars (Admin graph reachable only via a temporary debug shortcut for now —
   real custom-claim gating comes in Phase 1.5C).
6. **Firebase Emulator Suite config** — Done when: app can point at local emulators in
   debug builds via a build-config flag.

### PHASE 1 — MVP Core (Customer Journey)
7. Phone OTP auth flow (real device test required — emulators can't receive real SMS
   without test phone numbers configured in Firebase Console).
8. Profile setup + role selection, writing `users/{uid}`.
9. Home screen (category grid, search, promo banner, recent jobs).
10. Create Job screen (template chips per category, photo picker + compression, voice
    recorder capped 60s, geohash location capture, urgent toggle).
11. `CreateJobUseCase` writing the full `jobs` schema.
12. Find Nearby Workers screen (geohash + skill + isOnline + isVerified query,
    Haversine sort, WorkerCard list).
13. Call (`Intent.ACTION_DIAL`) + Book (`requestWorker()` sets `preferredWorkerId`,
    keeps `status: pending`).
14. Job Tracking screen (Firestore snapshot listener, vertical timeline Composable).
15. Rating screen, Job History screen.
16. Deploy/verify Security Rules from Section 9 against the emulator.

### PHASE 1.5 — Worker Journey (completes the real acceptance loop)
17. Worker Dashboard (dark theme header, online toggle wired to
    `WorkerOnlineForegroundService`, stat cards, live incoming job card).
18. Requests screen (full pending feed for this worker's skills/area).
19. `AcceptJobUseCase` — transactional claim (re-check `status == pending` before
    committing; surface a clean "already taken" error on failure).
20. Active Job screen (Navigate intent, Call Customer, sequential status buttons).
21. Seed/verify mock worker data across all 6 categories for testing.
22. Manual two-device test: customer creates → worker sees & accepts → status updates
    flow live in both directions → explicitly test the double-accept race.

### PHASE 1.5C — Admin Panel (per Section 4A's authorization model)
23. Replace the temporary debug shortcut from Phase 0 with the real gate: after login,
    call `AuthRepository.isAdmin()` (checks the `admin` custom claim) and route into the
    Admin Graph only if true — never based on the Firestore `role` field.
24. AdminOverviewScreen — stat cards (total users, jobs by status, jobs created
    today/this week) sourced from a callable Cloud Function that aggregates server-side,
    not a raw client Firestore scan.
25. VerificationQueueScreen — real-time stream of `workers` where `isVerified == false`,
    showing name, skills, and experience per row.
26. WorkerVerificationDetailScreen — CNIC front/back + selfie viewer, Approve button
    (writes `isVerified = true`, permitted by the admin-claim security rule from
    Section 9), Reject button (writes a rejection reason; does not delete the worker
    document).
27. Provision your own admin account: run the one-off custom-claim script from
    Section 4A against your own `uid`, sign out/in (or force-refresh the ID token), and
    confirm you land in the Admin Graph.
28. **Security verification (do not skip):** from a non-admin test account, attempt to
    write `role: "admin"` directly to that account's own `users/{uid}` document (e.g. via
    the Firebase Console or a quick script) and confirm the write is rejected by the
    security rule. This proves the self-elevation path is actually closed, not just
    hidden by the UI.

### PHASE 2 — Polish & Trust
29. Voice note waveform playback (`MediaPlayer`/`ExoPlayer`).
30. Urdu/English via `values-ur/strings.xml` + locale switch.
31. Home search, edit/cancel job, report flow — wire AdminReportsScreen to the new
    `reports` collection once it exists.
32. Trust Score display (Cloud Function already computes it — just render it).
33. After-photo upload on completion.
34. Urgent job badge, "Available Now" browse map.

### PHASE 3 — Growth
35. Referral codes, city waitlist screen, opt-in re-engagement notifications,
    worker leaderboard.

### PHASE 4+ — Monetization (post-100-jobs validation, separate planning pass)

---

## 17. Launch Plan — Sahiwal Pilot

- **Week 1:** Auth + Home + Create Job + Find + Call.
- **Week 2:** Worker side + FCM + tracking + rating.
- **Week 3:** Polish, compression, low-end device testing, onboard 10 real
  electricians/plumbers in person.
- **Marketing:** local Facebook groups, WhatsApp status, shop pamphlets.
- **Success metric:** 20 completed jobs in the first 2 weeks.

---

## 18. Portfolio Presentation Notes

Strongest talking points for this version specifically:
- Native Android depth done properly (no platform-channel workaround) — real
  `ForegroundService`, custom `NotificationChannel` with vibration, `BOOT_COMPLETED`
  handling for worker online-status continuity.
- Clean MVVM + Clean Architecture with a testable domain layer (UseCases independent
  of Android framework classes).
- Real geospatial querying (geohash bounding-box, Haversine distance) — not a naive
  full-collection scan.
- A concurrency bug you found and fixed yourself (double-accept race) with both a
  security-rule-level and a transaction-level guard — this is an excellent story for
  interviews; most portfolio projects never surface this class of bug at all.
- A genuine go-to-market plan (Sahiwal pilot, shop-by-shop onboarding).
- A three-tier authorization model (Customer/Worker/Admin) built on Firebase custom
  claims rather than a client-writable role field — a real, non-trivial security
  decision that's easy to explain and rarely present in portfolio-scale apps.

Record a short demo video of the full two-sided loop once Phase 1.5 is done — this
is worth more than a README screenshot list.

---

## 19. Future Roadmap

In-app chat, wallet/Easypaisa/JazzCash, premium worker badge, referral rewards,
dispute resolution with evidence trail, small-business worker accounts, expansion to
Okara and Pakpattan.
