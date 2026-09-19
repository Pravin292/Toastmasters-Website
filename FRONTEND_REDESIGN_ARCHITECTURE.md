# Rathinam Toastmasters Platform — Comprehensive Frontend Redesign Architecture

**Document Version:** 2.0.0  
**Target Scope:** Complete Internal Application Redesign (All Post-Authentication Pages & Shell)  
**Excluded Scope:** Login / Public Authentication Page (Preserved as-is)  
**Target URL:** `http://localhost:3000`  
**Author:** Antigravity Principal UI/UX & Systems Architect  
**Date:** September 18, 2026  

---

## 1. Executive Vision & Aesthetic Philosophy

### 1.1 The Objective
The internal experience of the Rathinam Toastmasters Digital Platform must transition from a functional prototype to a **world-class, high-performance executive cockpit**. It must inspire prestige, celebrate member public speaking growth, and streamline club management for executive officers.

### 1.2 Design Language: "Executive Toastmasters Glassmorphism"
Toastmasters International possesses a heritage of leadership, public speaking, and executive gravitas. The redesigned interface fuses Toastmasters' traditional brand identity with modern cutting-edge web design aesthetics:

- **Official Brand Accents with Deep Modern Foundations:**
  - **Burgundy / True Maroon (`#77216F`, `#581652`):** Primary action cues, leadership highlights, and executive branding.
  - **Loyal Blue (`#004165`, `#002D47`):** Depth surfaces, header navigation, and structured governance components.
  - **Rich Gold (`#F2DF74`, `#D4BD3A`):** Trophies, top podium placements, achievements, and VIP status indicators.
  - **Deep Cockpit Dark Background (`#070A11`, `#0B0F19`, `#121826`):** Zero-fatigue dark mode with subtle cosmic radial glows.
- **Layered Frosted Glass (Glassmorphism):**
  - High-performance `backdrop-filter: blur(16px)` on cards, navigation, and modal layers.
  - Multi-stop translucent borders (`border: 1px solid rgba(255, 255, 255, 0.08)`) with subtle gradient highlights.
  - Ambient elevation shadows with inner edge lighting.
- **Dynamic Physics & Micro-Interactions:**
  - Smooth spring transitions (`cubic-bezier(0.16, 1, 0.3, 1)`).
  - Tactile card hover elevations, glow flares on active buttons, and pulsing badges for urgent actions (e.g., unfilled meeting roles).
  - Confetti burst animations upon claiming achievements or earning new badges.
- **Typography Pairing:**
  - **Display / Headers / Numbers:** `'Outfit', sans-serif` — Modern, geometric, bold, and authoritative.
  - **Body / Data / System UI:** `'Inter', system-ui, sans-serif` — Crisp, legible, and optimized for high-density tables.

---

## 2. Directory Structure & Component Architecture

To support a scalable and clean codebase, the internal frontend will adopt a **Modular Feature-Driven Architecture**:

```
rathinam-frontend/src/
├── api/                             # Clean typed API clients
│   ├── client.ts                    # Axios / Fetch HTTP client with interceptors
│   ├── authApi.ts                   # Login, register, token management
│   ├── meetingApi.ts                # Meetings, agendas, attendance, role signups
│   ├── memberApi.ts                 # Member profiles, directory, account settings
│   ├── rankingApi.ts                # Leaderboard, standings, points ledger
│   ├── achievementApi.ts            # Badges, certificates, pathways tracking
│   ├── analyticsApi.ts              # Speech trends, attendance statistics
│   ├── notificationApi.ts           # Real-time notifications and alerts
│   └── aiApi.ts                     # AI meeting summary generation
├── assets/                          # SVG badges, club logo, sound effects
├── components/
│   ├── ui/                          # Atomic, reusable design system primitives
│   │   ├── Button.tsx               # Primary, Secondary, Ghost, Danger, Gold, Icon-only
│   │   ├── Card.tsx                 # GlassCard, InteractiveCard, MetricCard
│   │   ├── Badge.tsx                # StatusBadge, RoleBadge, RankPill
│   │   ├── Modal.tsx                # Accessible backdrop-blur dialogs
│   │   ├── Drawer.tsx               # Slide-over inspection drawers
│   │   ├── Tabs.tsx                 # Sliding-pill segmented controls
│   │   ├── Table.tsx                # Sortable, searchable, paginated glass tables
│   │   ├── Avatar.tsx               # Member avatar with online/role indicators
│   │   ├── AvatarGroup.tsx          # Stacked participant avatars (+3 more)
│   │   ├── ProgressBar.tsx          # Animated gradient milestone bars
│   │   ├── StatCard.tsx             # Metric counter with sparkline delta
│   │   ├── Tooltip.tsx              # Micro floating hints
│   │   ├── DropdownMenu.tsx         # Floating action menus
│   │   └── SkeletonLoader.tsx       # Shimmer skeletons for zero layout shift
│   ├── layout/                      # Global application shell
│   │   ├── AppLayout.tsx            # Main shell wrapper (Sidebar + Header + Canvas)
│   │   ├── Sidebar.tsx              # Collapsible dock with navigation & quick shortcuts
│   │   ├── Header.tsx               # Top command bar, breadcrumbs, search, alerts
│   │   ├── Breadcrumbs.tsx          # Contextual hierarchical navigation
│   │   ├── NotificationCenter.tsx   # Dropdown popover with grouped notifications
│   │   ├── UserProfileMenu.tsx      # Quick profile switcher and logout popover
│   │   └── MobileNav.tsx            # Bottom glass navigation bar for mobile screens
│   └── common/                      # Legacy fallbacks & shared utilities
├── context/
│   ├── AuthContext.tsx              # Authentication state, current user, role helpers
│   ├── ThemeContext.tsx             # Theme token management
│   └── NotificationContext.tsx      # Live notification polling / WebSocket alerts
├── hooks/
│   ├── useDebounce.ts               # Search input debouncing
│   ├── useClickOutside.ts           # Dropdown & modal dismiss
│   ├── useMediaQuery.ts             # Responsive viewport breakpoints
│   └── useConfetti.ts               # Celebration animation triggers
├── pages/                           # Full-page feature views
│   ├── DashboardPage.tsx            # Executive Member Cockpit
│   ├── MeetingsPage.tsx             # Meeting Agendas, Calendar & Grid
│   ├── MeetingDetailPage.tsx        # Live Meeting Command Center (Roles, Timer, AI)
│   ├── MembersPage.tsx              # Club Directory & Profile Inspection
│   ├── LeaderboardPage.tsx          # Competitive Rankings & Podium
│   ├── AchievementsPage.tsx         # Pathways Badges & Trophy Showcase
│   ├── AnalyticsPage.tsx            # Performance Velocity, Charts & Heatmaps
│   ├── CertificatesPage.tsx         # Verified Credentials & PDF Exporter
│   ├── NotificationsPage.tsx        # Dedicated Notification Stream & Filters
│   ├── AdminPage.tsx                # Officer Governance & DCP Tracker
│   └── LoginPage.tsx                # UNTOUCHED (Preserved as requested)
├── styles/
│   ├── tokens.css                   # Comprehensive CSS design variables & tokens
│   ├── index.css                    # Base resets, typography, glass utilities
│   ├── layout.css                   # App shell, grid systems, responsive rules
│   └── components.css               # Specialized component animations & styles
└── types/
    └── index.ts                     # Strict TypeScript schemas for all entities
```

---

## 3. Design System & Token Specifications

### 3.1 Color Palette Tokens (`tokens.css`)

```css
:root {
  /* Brand Core */
  --tm-maroon: #77216F;
  --tm-maroon-hover: #8D2984;
  --tm-maroon-dark: #4A1244;
  --tm-maroon-glow: rgba(119, 33, 111, 0.45);

  --tm-blue: #004165;
  --tm-blue-hover: #005483;
  --tm-blue-dark: #00263B;
  --tm-blue-glow: rgba(0, 65, 101, 0.5);

  --tm-gold: #F2DF74;
  --tm-gold-hover: #F6E796;
  --tm-gold-dark: #B59F2A;
  --tm-gold-glow: rgba(242, 223, 116, 0.4);

  --tm-cool-gray: #A9B2B1;

  /* Cockpit Surfaces & Backgrounds */
  --surface-canvas: #070A11;
  --surface-ground: #0B0F19;
  --surface-card: rgba(15, 22, 36, 0.75);
  --surface-card-hover: rgba(22, 32, 52, 0.85);
  --surface-card-active: rgba(30, 42, 68, 0.9);
  --surface-glass-subtle: rgba(255, 255, 255, 0.03);
  --surface-glass-hover: rgba(255, 255, 255, 0.06);

  /* Borders & Dividers */
  --border-subtle: rgba(255, 255, 255, 0.07);
  --border-medium: rgba(255, 255, 255, 0.12);
  --border-bright: rgba(255, 255, 255, 0.22);
  --border-gold: rgba(242, 223, 116, 0.3);
  --border-maroon: rgba(119, 33, 111, 0.35);

  /* Typography Colors */
  --text-primary: #F8FAFC;
  --text-secondary: #94A3B8;
  --text-muted: #64748B;
  --text-inverse: #070A11;
  --text-gold: #F6E796;

  /* Semantic Feedback */
  --success: #10B981;
  --success-glow: rgba(16, 185, 129, 0.35);
  --warning: #F59E0B;
  --warning-glow: rgba(245, 158, 11, 0.35);
  --danger: #EF4444;
  --danger-glow: rgba(239, 68, 68, 0.35);
  --info: #38BDF8;
  --info-glow: rgba(56, 189, 248, 0.35);

  /* Toastmasters Role Themes */
  --role-speaker: #EC4899;
  --role-evaluator: #8B5CF6;
  --role-tmd: #F59E0B;
  --role-timer: #10B981;
  --role-grammarian: #06B6D4;
  --role-ahcounter: #F97316;
  --role-topicsmaster: #6366F1;

  /* Elevation & Shadows */
  --shadow-elevation-1: 0 4px 12px rgba(0, 0, 0, 0.3);
  --shadow-elevation-2: 0 10px 30px -5px rgba(0, 0, 0, 0.5);
  --shadow-elevation-3: 0 20px 45px -10px rgba(0, 0, 0, 0.7);
  --shadow-glow-maroon: 0 0 25px rgba(119, 33, 111, 0.35);
  --shadow-glow-gold: 0 0 25px rgba(242, 223, 116, 0.25);

  /* Layout Constants */
  --sidebar-width-expanded: 260px;
  --sidebar-width-collapsed: 76px;
  --header-height: 72px;
  --canvas-max-width: 1520px;
  --radius-sm: 8px;
  --radius-md: 14px;
  --radius-lg: 20px;
  --radius-xl: 28px;
  --radius-full: 9999px;
}
```

---

## 4. Global Shell Architecture: The "Executive Shell"

The redesigned shell replaces the static top navigation bar with an **ergonomic two-tier cockpit layout**:

```
+-----------------------------------------------------------------------------------------------+
| SIDEBAR               | TOP COMMAND BAR (Header)                                              |
| [TM Logo] Rathinam TM | Breadcrumbs > Meetings > Meeting #104      [Search] [Bell] [Profile] |
|-----------------------+-----------------------------------------------------------------------|
| ▣ Dashboard           | MAIN CONTENT CANVAS (Max-Width: 1520px)                               |
| 📅 Meetings (Live: 1) |                                                                       |
| 👥 Members Directory  | [Page Title & Action Toolbar]                                         |
| 🏆 Leaderboard        | +-------------------------------------------------------------------+ |
| 🎖 Achievements       | | Metric Ribbon (4 KPI Cards with Sparklines & Deltas)              | |
| 📊 Analytics          | +-------------------------------------------------------------------+ |
| 📜 Certificates       | | Feature Grid / Responsive Multi-Column Split                      | |
| 🔔 Notifications (3)  | | [Main Feature Work Area]               | [Contextual Action Rail] | |
| 🛡 Officer Portal     | |                                        |                          | |
|-----------------------| |                                        |                          | |
| [Profile Card]        | +-------------------------------------------------------------------+ |
| Member • Pravin       | FOOTER / STATUS BAR: DB Connected • Next Meeting: Friday, 5:30 PM     |
+-----------------------------------------------------------------------------------------------+
```

### 4.1 Collapsible Ergonomic Sidebar (`Sidebar.tsx`)
- **Dual State:** Expandable (260px) for descriptive navigation; collapsible (76px icon dock) for high-density focus work.
- **Active Navigation Indicator:** Glowing vertical pill on the left border with maroon-to-gold gradient text.
- **Badge Counters:** Live count badges (e.g. Unread notifications, open meeting roles) with pulse rings.
- **Officer Section:** Visually distinguished "Executive Suite" header accessible only to `ADMIN`, `PRESIDENT`, and `OFFICER` roles.
- **Bottom Member Chip:** Displays avatar, full name, Toastmasters title (e.g. "Club President" or "Competent Communicator"), and quick-logout icon button.

### 4.2 Top Command Bar (`Header.tsx`)
- **Smart Breadcrumb Trail:** Deep path hierarchy (e.g., `Home / Meetings / Meeting #142 / Agenda`).
- **Global Search Quick-Launcher:** `Cmd+K` / `Ctrl+K` command palette modal searching members, meetings, and speech topics.
- **Next Meeting Quick-Ticker:** Shows a live countdown badge: `"Next Meeting in 2d 14h"` with direct link to the agenda.
- **Notification Popover:** Rich notification flyout with tabbed filtering (`All`, `Roles`, `Awards`, `Announcements`), 1-click "Mark all as read", and direct action links.
- **Profile Popover:** Direct links to "My Profile", "My Speeches", "Account Settings", and "Sign Out".

### 4.3 Responsive Bottom Dock for Mobile Viewports (`MobileNav.tsx`)
- On screens `< 768px`, the sidebar smoothly transforms into a floating bottom glass tab bar with haptic feedback icons for:
  1. `Dashboard`
  2. `Meetings`
  3. `Members`
  4. `Leaderboard`
  5. `More / Menu` (Drawer sheet containing Analytics, Certificates, Admin, and Settings).

---

## 5. Page-by-Page Redesign Specifications

---

### 5.1 Dashboard Page (`/dashboard`) — The Executive Member Cockpit

#### Objective
Provide every member with an exhilarating, high-clarity snapshot of their Toastmasters journey, upcoming roles, club rank, and immediate actions.

#### Visual Layout & Component Composition

```
+---------------------------------------------------------------------------------------+
| HERO WELCOME BANNER (Gradient Mesh Background with Frosted Overlay)                   |
| "Welcome back, Pravin! Ready to master the stage?"                                    |
| [Current Pathway: Presentation Mastery • Level 3]  [Streak: 4 Meetings 🔥]           |
| Action Buttons: [Register for Upcoming Role]  [Submit Speech Details]                |
+---------------------------------------------------------------------------------------+

+--------------------+ +--------------------+ +--------------------+ +--------------------+
| UPCOMING ROLE      | | SPEECHES GIVEN     | | TOASTMASTERS PTS   | | CLUB RANKING       |
| Timer              | | 8 Speeches         | | 1,450 Points       | | #3 on Leaderboard  |
| Meeting #104 (Fri) | | +2 this month      | | Top 5% of club     | | 150 pts to Rank #2 |
| [View Role Guide]  | | [View History]     | | [View Breakdown]   | | [View Standings]   |
+--------------------+ +--------------------+ +--------------------+ +--------------------+

+--------------------------------------------------+ +----------------------------------+
| UPCOMING MEETINGS SCHEDULE (Main Column - 60%)   | | PATHWAYS PROGRESS & BADGES (40%) |
| Next Meeting #104: "Overcoming Stage Fear"       | | Presentation Mastery (Level 3)   |
| Date: Friday, Sept 25, 2026 • 5:30 PM            | | [=====================>    ] 72% |
| Location: Rathinam Auditorium / Zoom Link        | | Next Goal: 1 Evaluation Speech   |
| Role Matrix Snapshot:                            | |                                  |
| • Toastmaster of the Day: Sarah Jenkins [Filled] | | RECENT BADGES UNLOCKED           |
| • Speaker 1: Alex Rivera [Filled]                | | 🏅 Ice Breaker Champion          |
| • Evaluator 1: UNFILLED [⚡ Claim Role]          | | ⏱ Master Timekeeper              |
| • Table Topics Master: UNFILLED [⚡ Claim Role]  | | 🌟 Table Topics Ace              |
| [View Complete Agenda & Meeting Details ->]      | | [Explore Trophy Room ->]         |
+--------------------------------------------------+ +----------------------------------+

+--------------------------------------------------+ +----------------------------------+
| RECENT POINT ACTIVITIES (Table)                  | | QUICK CLUB ANNOUNCEMENTS         |
| • Role as Table Topics Master (+50 pts)          | | Annual Division Speech Contest   |
| • Meeting Attendance #103 (+20 pts)              | | Registration closes next Tuesday.|
| • Completed Level 2 Pathway (+150 pts)           | | [Read Full Announcement ->]      |
+--------------------------------------------------+ +----------------------------------+
```

#### Key Functional Upgrades
1. **Interactive Role Claiming from Dashboard:** Members can claim an open role in the next meeting with a single click right from their dashboard without hunting through meetings.
2. **Radial Pathways Milestone Progress:** Visual SVG radial ring displaying percentage completion toward the current Pathway level.
3. **Animated Milestone Micro-Surprises:** Upon reaching point milestones or advancing ranks, an in-app gold burst congratulates the user.

---

### 5.2 Meetings Page (`/meetings`) — Meeting Agenda & Schedule Hub

#### Objective
Transform meeting discovery and role scheduling into an intuitive, multi-view experience for both regular members and meeting organizers.

#### Visual Layout & Component Composition

```
+---------------------------------------------------------------------------------------+
| HEADER TOOLBAR                                                                        |
| Title: Club Meetings & Agendas                                                        |
| View Switcher: [ ■ Agenda Cards ]  [ ▦ Calendar Grid ]  [ ≡ Timeline ]                |
| Filters: [All Statuses ▾] [All Types ▾] [Search Theme or Number...]                    |
| Actions: [ + Schedule Meeting ] (Officer Only)   [ Export Season Calendar ]           |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| STATUS FILTER TABS: [ All (24) ] [ Upcoming (3) ] [ In Progress (1) ] [ Past (20) ]   |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| FEATURED: NEXT UPCOMING MEETING (Large Interactive Card)                              |
| Meeting #104 • "The Power of Persuasion"                                              |
| 📅 Friday, Sept 25, 2026 | ⏰ 05:30 PM - 07:00 PM | 📍 Seminar Hall B & Online Zoom  |
|                                                                                       |
| Role Fill Status: 8 of 11 Roles Filled (73%)                                          |
| [===============>       ] 3 Open Roles Remaining: Evaluator 2, Grammarian, Ah-Counter |
| Attendees: 24 Members Confirmed • 4 Guests                                            |
|                                                                                       |
| Action Buttons:                                                                       |
| [ ⚡ Quick-Claim Role ]   [ 📋 View Full Agenda ]   [ 🎥 Join Video Call ]            |
+---------------------------------------------------------------------------------------+

+--------------------+ +--------------------+ +--------------------+
| MEETING #105       | | MEETING #106       | | MEETING #107       |
| "Humor That Heals" | | "Leadership Sparks"| | "Table Topics Gala"|
| Oct 2, 2026        | | Oct 9, 2026        | | Oct 16, 2026       |
| 5 of 11 Filled     | | 2 of 11 Filled     | | Planning Phase     |
| [Claim Open Role]  | | [Claim Open Role]  | | [View Schedule]    |
+--------------------+ +--------------------+ +--------------------+
```

#### Key Functional Upgrades
1. **Multi-View Engine:** Toggle between Glass Card View, Month Calendar Grid, and Chronological Table.
2. **"My Role Filter":** A dedicated toggle switch `"Show only meetings where I have a role"` allows speakers and role takers to instantly see their commitments.
3. **Role Deficit Indicator:** Cards feature amber/red warning badges if a meeting is within 48 hours and critical roles (TMD, Timer, Evaluators) remain unfilled.
4. **Officer Fast Creation Modal:** Includes template presets (e.g. "Standard 90-min Agenda", "Speech Marathon", "Table Topics Special") which auto-populate role slots and times.

---

### 5.3 Meeting Detail Page (`/meetings/:id`) — The Live Meeting Cockpit

#### Objective
The crown jewel of the platform: a comprehensive live cockpit supporting pre-meeting role assignment, live meeting operations (timer, evaluation, attendance), and post-meeting AI generation.

#### Visual Layout & Component Composition

```
+---------------------------------------------------------------------------------------+
| MEETING HERO BAR                                                                      |
| Meeting #104 • "The Art of Persuasive Storytelling"                                   |
| Status Pill: [ 🟢 SCHEDULED ]  Meeting Type: [ Regular Weekly ]                       |
| Date & Time: Sept 25, 2026 • 5:30 PM - 7:00 PM IST                                    |
| Location: Rathinam Central Auditorium • [ 🎥 Join Hybrid Zoom Meeting ]              |
|                                                                                       |
| Actions: [ 🖨 Print Agenda ] [ 📥 Download PDF ] [ 🤖 Generate AI Minutes ]           |
| (Officer Controls: [ Start Meeting ] [ Complete Meeting ] [ Cancel Meeting ])         |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| COCKPIT WORKSPACE TABS:                                                               |
| [ 📋 Role Matrix & Sign-ups ] [ ⏱ Live Timer ] [ 📝 Speech Evaluations ] [ 👥 Attendance ] |
+---------------------------------------------------------------------------------------+

+--------------------------------------------------+ +----------------------------------+
| TAB 1: ROLE MATRIX & SIGN-UPS (Interactive Grid) | | MEETING OVERVIEW & AGENDA NOTES  |
|                                                  | | Theme: Storytelling that Inspires|
| [LEADERSHIP ROLES]                               | | Word of the Day: "Eloquent"      |
| • Toastmaster of the Day:                        | | Definition: Fluent or persuasive |
|   [Avatar] Sarah Jenkins (Vice President Ed)     | |                                  |
| • Table Topics Master:                           | | AGENDA SCHEDULE                  |
|   [Avatar] David Chen • [Relinquish Role]        | | 05:30 PM - Call to Order (SAA)   |
| • General Evaluator:                             | | 05:35 PM - President Opening     |
|   [ UNFILLED ] -> [ ⚡ Sign Up For This Role ]   | | 05:45 PM - Prepared Speeches     |
|                                                  | | 06:15 PM - Table Topics Session  |
| [SPEECH PRESENTERS]                              | | 06:35 PM - Evaluation Session    |
| • Speaker 1: Alex Rivera (Pathways Level 2)      | | 06:55 PM - Awards & Closing      |
|   Title: "The Unseen Symphony" (5-7 Mins)        | |                                  |
| • Speaker 2: [ UNFILLED ] -> [ ⚡ Take Slot ]    | | AI SUMMARY CARD                  |
|                                                  | | [Generate Meeting Minutes with AI]
| [TAG TEAM (EVALUATION & TIME)]                   | | Focus: Action Items & Feedback   |
| • Timer: Pravin (You) [Assigned]                 | | Tone: Professional & Inspiring   |
| • Grammarian: Anita Roy                          | | (Generated output renders here)  |
| • Ah-Counter: [ UNFILLED ] -> [ ⚡ Take Slot ]   | |                                  |
+--------------------------------------------------+ +----------------------------------+
```

#### Dedicated Cockpit Sub-Modules
1. **Live Interactive Stopwatch (`Live Timer Tab`):**
   - Clean full-screen timer modal for the Timer role with Green (5:00), Yellow (6:00), and Red (7:00) visual color shifts.
   - 1-click time recording that directly saves speech times to the meeting record.
2. **Evaluator Form (`Speech Evaluations Tab`):**
   - Structured evaluation template (Strengths, Areas for Growth, Specific Recommendations).
   - Instant export to speaker's personal speech log.
3. **AI Meeting Summary Suite (`AI Minutes Modal`):**
   - Select focus areas (e.g., "Grammar & Vocabulary Highlights", "Key Action Items", "Contest Highlights").
   - 1-click generation using protected backend LLM service.
   - Markdown viewer with copy-to-clipboard and email minutes to members.

---

### 5.4 Members Directory Page (`/members`) — Club Community & Profiles

#### Objective
Showcase club talent, foster peer mentorship, and provide seamless member profile discovery.

#### Visual Layout & Component Composition

```
+---------------------------------------------------------------------------------------+
| HEADER: Club Member Directory (Total: 48 Members)                                     |
| Search: [ 🔍 Search by name, designation, email, or badge... ]                        |
| Filters: [ All Roles ▾ ] [ Status: Active ▾ ] [ Sort: Highest Points ▾ ]              |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| ROLE FILTER CHIPS: [ All ] [ Executive Officers ] [ Mentors ] [ Active Speakers ]     |
+---------------------------------------------------------------------------------------+

+------------------------------------+ +------------------------------------+
| MEMBER CARD                        | | MEMBER CARD                        |
| [Avatar with Golden Crown Ring]    | | [Avatar with Silver Ring]          |
| Sarah Jenkins                      | | Alex Rivera                        |
| Role: Vice President Education     | | Role: Member                       |
| Level: Presentation Mastery (L4)   | | Level: Dynamic Leadership (L2)     |
| Total Speeches: 14 | Points: 2,100 | | Total Speeches: 6 | Points: 890    |
| Badges: [🏅] [⏱] [🌟] [+5 more]    | | Badges: [🏅] [⏱] [+2 more]         |
| Status: [ 🟢 ACTIVE ]              | | Status: [ 🟢 ACTIVE ]              |
|                                    | |                                    |
| [ View Full Profile ]              | | [ View Full Profile ]              |
+------------------------------------+ +------------------------------------+
```

#### Member Profile Drawer (`Drawer.tsx`)
Clicking any member opens a glass slide-over drawer showing:
- Comprehensive Bio, Social Links, and Toastmasters Join Date.
- Historical Speech Ledger (Titles, Dates, Evaluator Notes).
- Role Distribution Breakdown (Pie chart of TMD, Timer, Speaker, Evaluator times).
- Direct Edit Profile button (Restricted via BOLA/IDOR permissions to profile owner or officers).

---

### 5.5 Leaderboard & Rankings Page (`/rankings`) — Gamified Club Standings

#### Objective
Ignite positive motivation and friendly competition through an Olympic-style awards podium and dynamic rankings ledger.

#### Visual Layout & Component Composition

```
+---------------------------------------------------------------------------------------+
| HEADER & SEASON SELECTOR                                                              |
| Title: Club Championship & Rankings                                                   |
| Season: [ Fall Season 2026 ▾ ]   Timeframe: [ All-Time ] [ This Month ] [ This Term ] |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| THE CHAMPIONS PODIUM (3D Elevated Glass Pedestals)                                    |
|                                                                                       |
|             [ 🥇 1st Place - GOLD ]                                                   |
|             [Avatar with Gold Halo]                                                   |
|             Sarah Jenkins                                                             |
|             2,450 Points                                                              |
|             14 Speeches • 12 Roles                                                    |
|             +---------------------+                                                   |
|             |       PODIUM 1      |                                                   |
| [ 🥈 2nd - SILVER ] |                     | [ 🥉 3rd - BRONZE ]                       |
| [Avatar]            |                     | [Avatar]                                  |
| Michael Chang       |                     | Pravin (You)                              |
| 1,820 Points        |                     | 1,450 Points                              |
| 9 Speeches          |                     | 8 Speeches                                |
| +-----------------+ |                     | +-----------------+                       |
| |    PODIUM 2     | |                     | |    PODIUM 3     |                       |
| +-----------------+ +---------------------+ +-----------------+                       |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| LEADERBOARD TABLE (Ranks 4 through 50)                                                |
| Search Member: [ 🔍 Filter rankings... ]                                              |
|                                                                                       |
| Rank | Member                | Speeches | Roles Filled | Attendance | Total Points    |
|------+-----------------------+----------+--------------+------------+-----------------|
| #4   | Elena Rostova         | 7        | 11           | 95%        | 1,320 pts       |
| #5   | David Chen            | 6        | 9            | 90%        | 1,150 pts       |
| #6   | Anita Roy             | 5        | 10           | 88%        | 1,020 pts       |
| #7   | Rajiv Menon           | 4        | 8            | 85%        | 940 pts         |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| STICKY BOTTOM BAR (Your Standing)                                                     |
| Your Rank: #3 • Points: 1,450 • 370 Points behind #2 Michael Chang [View Breakdown]   |
+---------------------------------------------------------------------------------------+
```

---

### 5.6 Achievements & Trophy Room (`/achievements`) — Pathways & Badges

#### Objective
Celebrate milestones, recognize leadership contributions, and gamify skill development.

#### Visual Layout & Component Composition

```
+---------------------------------------------------------------------------------------+
| HEADER: Toastmasters Trophy Room                                                      |
| Stats: [ 12 Badges Unlocked ] [ 4 In Progress ] [ 8 Locked ] [ Total Points: 1,450 ]  |
| Category Filter: [ All ] [ Speech Mastery ] [ Meeting Roles ] [ Pathways ] [ Streaks ]|
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| SECTION: PATHWAYS MILESTONE BADGES (Hexagonal Metallic Design)                        |
|                                                                                       |
| +----------------------+  +----------------------+  +----------------------+          |
| | 🌟 LEVEL 1 MASTER    |  | 🚀 LEVEL 2 ACHIEVER  |  | 👑 LEVEL 3 LEADER    |          |
| | Presentation Mastery |  | Presentation Mastery |  | Presentation Mastery |          |
| | Status: [ UNLOCKED ] |  | Status: [ UNLOCKED ] |  | Progress: [ 75% ]    |          |
| | Unlocked: Aug 12     |  | Unlocked: Sept 02    |  | 1 Speech Remaining   |          |
| +----------------------+  +----------------------+  +----------------------+          |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| SECTION: ROLE & SPECIALTY BADGES                                                      |
|                                                                                       |
| +----------------------+  +----------------------+  +----------------------+          |
| | 🎙 ICE BREAKER       |  | ⏱ CLOCKWORK TIMER    |  | 🦅 GENERAL EVALUATOR |          |
| | First Speech Given   |  | Timed 5 Meetings     |  | 3 Evaluations Done   |          |
| | Status: [ UNLOCKED ] |  | Status: [ UNLOCKED ] |  | Status: [ LOCKED ]   |          |
| | Points: +100         |  | Points: +150         |  | Hint: Take GE role   |          |
| +----------------------+  +----------------------+  +----------------------+          |
+---------------------------------------------------------------------------------------+
```

---

### 5.7 Performance Analytics Page (`/analytics`) — Growth & Insights

#### Objective
Empower members and executive officers with data-driven speech velocity, attendance frequency, and role diversity analytics.

#### Visual Layout & Component Composition

```
+---------------------------------------------------------------------------------------+
| HEADER: Personal Speech & Performance Analytics                                       |
| Timeframe: [ Past 6 Months ▾ ]   Target: [ Self Analytics ▾ ] (Officers can view club)|
+---------------------------------------------------------------------------------------+

+--------------------+ +--------------------+ +--------------------+ +--------------------+
| ATTENDANCE RATE    | | SPEECHES DELIVERED | | ROLE DIVERSITY     | | EVALUATION SCORE   |
| 92.5%              | | 8 Speeches         | | 6 Different Roles  | | 4.8 / 5.0          |
| Top tier           | | +3 from last term  | | Well-balanced      | | Consistent praise  |
+--------------------+ +--------------------+ +--------------------+ +--------------------+

+--------------------------------------------------+ +----------------------------------+
| ATTENDANCE & PARTICIPATION HEATMAP               | | ROLE DIVERSITY BREAKDOWN (Donut) |
| (GitHub-Style Weekly Square Grid)                | |                                  |
| Sep: ■ ■ ■ ■                                     | | • Speaker: 35%                   |
| Aug: ■ ■ ■ □                                     | | • Timer: 20%                     |
| Jul: ■ ■ ■ ■                                     | | • Evaluator: 15%                 |
| Jun: ■ ■ □ ■                                     | | • Table Topics: 15%              |
| [ Less ■ ■ ■ ■ More ]                            | | • TMD: 15%                       |
+--------------------------------------------------+ +----------------------------------+

+---------------------------------------------------------------------------------------+
| SPEECH VELOCITY TREND (SVG Area Chart)                                                |
| Speeches given per month over time with pathway milestones annotated                  |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| AI GROWTH RECOMMENDATION                                                              |
| "You have delivered 8 speeches and excelled as Timer and Topics Master. To complete   |
| your Pathways Level 3 requirements, take on General Evaluator in the next 2 meetings."|
+---------------------------------------------------------------------------------------+
```

---

### 5.8 Certificates & Credentials Page (`/certificates`) — Verified Recognition

#### Objective
Display official completion credentials, verified awards, and enable 1-click printable PDF generation.

#### Visual Layout & Component Composition

```
+---------------------------------------------------------------------------------------+
| HEADER: Verified Toastmasters Certificates & Awards                                   |
| Action: [ 📥 Download All Certificates (ZIP) ]                                         |
+---------------------------------------------------------------------------------------+

+--------------------------------------------------+ +----------------------------------+
| CERTIFICATE PREVIEW CARD                         | | CERTIFICATE PREVIEW CARD         |
| [Official Rathinam Parchment Style Frame]        | | [Official Rathinam Frame]        |
|                                                  | |                                  |
|       Rathinam Toastmasters Club                 | |    Rathinam Toastmasters Club    |
|      CERTIFICATE OF ACHIEVEMENT                  | |    CERTIFICATE OF COMPLETION     |
|   Awarded to: Pravin Kumar                       | | Awarded to: Pravin Kumar         |
|   For: Presentation Mastery - Level 1 Completion | | For: Ice Breaker Speech Mastery  |
|   Date: August 14, 2026 • Verified Credential ID | | Date: July 10, 2026              |
|                                                  | |                                  |
| Actions:                                         | | Actions:                         |
| [ 👁 View High-Res ]  [ 🖨 Print ]  [ 📄 PDF ]   | | [ 👁 View ]  [ 🖨 Print ] [ 📄 PDF]|
+--------------------------------------------------+ +----------------------------------+
```

---

### 5.9 Notifications Page (`/notifications`) — Real-time Alert Center

#### Objective
Centralize all meeting role notifications, agenda updates, badge awards, and officer broadcast messages.

#### Visual Layout & Component Composition

```
+---------------------------------------------------------------------------------------+
| HEADER: Notification Center                                                          |
| Controls: [ All (14) ] [ Unread (3) ] [ Roles (5) ] [ Awards (4) ] [ Announcements (2)]|
| Actions: [ ✓ Mark all as read ] [ ⚙ Notification Preferences ]                        |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| NOTIFICATION STREAM                                                                   |
|                                                                                       |
| 🟢 [ROLE REMINDER] • 15 minutes ago                                                   |
| You are scheduled as Timer for Meeting #104 this Friday at 5:30 PM.                   |
| Actions: [ View Meeting Agenda ] [ Confirm Role ]                                     |
|---------------------------------------------------------------------------------------|
| 🏆 [ACHIEVEMENT UNLOCKED] • 2 hours ago                                               |
| Congratulations! You unlocked the "Clockwork Timer" badge (+150 Toastmasters Points). |
| Actions: [ View in Trophy Room ]                                                      |
|---------------------------------------------------------------------------------------|
| 📣 [CLUB ANNOUNCEMENT] • 1 day ago • By President Sarah Jenkins                      |
| Division Speech Contest registrations are now open.                                   |
| Actions: [ Read Announcement ]                                                        |
+---------------------------------------------------------------------------------------+
```

---

### 5.10 Admin & Officer Portal (`/admin`) — Executive Club Cockpit

#### Objective
Provide club officers (`ADMIN`, `PRESIDENT`, `OFFICER`) with a high-command governance interface to manage members, configure meetings, award custom points, and monitor club health.

#### Visual Layout & Component Composition

```
+---------------------------------------------------------------------------------------+
| EXECUTIVE COMMAND BAR                                                                 |
| Rathinam Toastmasters Club #074218 • Executive Committee Portal                       |
| Officer Privileges Active: President / VP Education / VP Membership / Secretary       |
+---------------------------------------------------------------------------------------+

+--------------------+ +--------------------+ +--------------------+ +--------------------+
| ACTIVE ROSTER      | | DCP GOALS MET      | | UPCOMING MEETINGS  | | TERM ATTENDANCE    |
| 48 Members         | | 7 of 10 Goals      | | 4 Scheduled        | | 88.4% Average      |
| 4 Pending Signups  | | Distinguished Club | | 8 Roles to fill    | | Healthy club status|
+--------------------+ +--------------------+ +--------------------+ +--------------------+

+---------------------------------------------------------------------------------------+
| GOVERNANCE TABS:                                                                      |
| [ 👥 Member Management ] [ 🎯 DCP Goals Tracker ] [ 💎 Points & Awards ] [ 📜 Audit ]  |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| TAB 1: MEMBER MANAGEMENT TABLE                                                        |
| Search: [ 🔍 Filter by name or email... ]   Actions: [ + Add Member ]                 |
|                                                                                       |
| Name & Email       | Account Role | Executive Title        | Status   | Actions       |
|--------------------+--------------+------------------------+----------+---------------|
| Sarah Jenkins      | ADMIN        | President              | ACTIVE   | [Edit] [Role] |
| Michael Chang      | OFFICER      | VP Education           | ACTIVE   | [Edit] [Role] |
| Pravin Kumar       | MEMBER       | -                      | ACTIVE   | [Edit] [Role] |
| Guest Visitor      | GUEST        | -                      | PENDING  | [Approve]     |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| TAB 2: DISTINGUISHED CLUB PROGRAM (DCP) GOALS CHECKLIST                               |
| 1. Four Level 1 awards achieved ............................. [ COMPLETED ✓ ]        |
| 2. Two Level 2 awards achieved ............................. [ COMPLETED ✓ ]        |
| 3. Two more Level 2 awards achieved ......................... [ IN PROGRESS (1/2) ]  |
| 4. Two Level 3 awards achieved ............................. [ COMPLETED ✓ ]        |
| 5. One Level 4, Level 5, or DTM award ....................... [ COMPLETED ✓ ]        |
| 6. One more Level 4, Level 5, or DTM award .................. [ NOT STARTED ]        |
| 7. Four new members recruited ............................... [ COMPLETED ✓ ]        |
| 8. Four more new members recruited .......................... [ COMPLETED ✓ ]        |
| 9. Minimum four club officers trained (Round 1 & 2) ......... [ COMPLETED ✓ ]        |
| 10. Membership dues paid on time & officer list submitted ... [ COMPLETED ✓ ]        |
+---------------------------------------------------------------------------------------+

+---------------------------------------------------------------------------------------+
| TAB 3: CUSTOM POINT ADJUSTMENT & MERIT AWARDS                                         |
| Award points for external speech contests, organizing workshops, or club service:     |
| [Select Member ▾] [Points: e.g. 100] [Category: Contest Win ▾] [Reason: Area Contest]|
| Action: [ 💎 Award Points & Trigger Notification ]                                    |
+---------------------------------------------------------------------------------------+
```

---

## 6. Shared Component Library Specification (Atomic Design)

### 6.1 `Button.tsx` Variants
- **Primary:** Gradient fill `linear-gradient(135deg, var(--tm-maroon), var(--tm-maroon-hover))` with subtle inner top border and glowing hover halo.
- **Gold:** `linear-gradient(135deg, #F2DF74, #D4BD3A)` with dark text for trophies, highlights, and claims.
- **Glass / Secondary:** `rgba(255, 255, 255, 0.05)` with `border: 1px solid rgba(255, 255, 255, 0.1)`.
- **Danger:** Soft ruby glass with bright red text for cancellations and drops.
- **States:** Built-in loading spinner slot (`loading={true}`), icon left/right props, and ripple touch feedback.

### 6.2 `Card.tsx` / `GlassCard.tsx`
- **Surface:** `background: rgba(15, 22, 36, 0.75); backdrop-filter: blur(16px);`
- **Border:** Dual-tone border stroke with corner highlights.
- **Interactive Props:** `hoverable={true}` triggers smooth 3D tilt, subtle vertical lift (`transform: translateY(-4px)`), and border illumination.

### 6.3 `Badge.tsx`
- Pre-configured Toastmasters variants:
  - Role Badges (`TMD`, `Timer`, `Evaluator`, `Speaker`, `Grammarian`) with matching distinct role colors.
  - Meeting Status Badges (`SCHEDULED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`).
  - Pulsing Status Indicators for live events or unfilled slots.

### 6.4 `Modal.tsx` & `Drawer.tsx`
- High-grade accessibility with keyboard Escape listener, focus traps, and smooth entry/exit opacity and scale animations.
- Backdrop blur (`backdrop-filter: blur(12px)` over deep black tint `rgba(3, 7, 18, 0.7)`).

---

## 7. State Management, Performance & Micro-Interactions

### 7.1 Real-Time Data Flow & Optimistic Updates
- **Optimistic Role Sign-ups:** When a member clicks `"Sign Up For This Role"`, the UI instantly updates the role card to show the member's avatar and name while the API call dispatches in the background, providing instantaneous feedback.
- **Debounced Global Search:** Member and meeting searches are debounced by 300ms to eliminate redundant network traffic.
- **Notification Polling / SSE:** Periodic background checks for unread notifications and meeting status updates every 45 seconds.

### 7.2 Micro-Interactions & Audio-Visual Delight
- **Confetti Engine:** Triggered upon claiming an achievement, reaching a new rank tier, or when an officer marks a meeting as `COMPLETED`.
- **Zero Layout Shifts:** Skeleton loaders matching the exact dimensions of cards, tables, and metric blocks render during data fetches.

---

## 8. Implementation Roadmap & Milestones

| Phase | Core Objective | Key Deliverables |
| :--- | :--- | :--- |
| **Phase 1** | **Design Foundation & Shared Primitives** | Implement `tokens.css`, `index.css`, and core UI components (`Button`, `Card`, `Badge`, `Modal`, `Drawer`, `Tabs`). |
| **Phase 2** | **The Executive Shell** | Implement `AppLayout`, collapsible `Sidebar`, dynamic `Header`, command palette, and `MobileNav`. |
| **Phase 3** | **Cockpit & Meetings Redesign** | Redesign `DashboardPage`, `MeetingsPage`, and the multi-tab `MeetingDetailPage` (with Live Timer & AI summary). |
| **Phase 4** | **Community & Gamification** | Redesign `MembersPage`, `LeaderboardPage`, `AchievementsPage`, and `CertificatesPage`. |
| **Phase 5** | **Analytics & Officer Suite** | Redesign `AnalyticsPage`, `NotificationsPage`, and the comprehensive `AdminPage`. |
| **Phase 6** | **Polish & Micro-Interactions** | Add confetti celebrations, skeleton loaders, responsive audits, and accessibility QA. |

---

## 9. Verification & Quality Checklist

- [ ] **Aesthetic Standard:** Vibrant Toastmasters royal palette (Burgundy, Blue, Gold) over deep dark glassmorphism surfaces.
- [ ] **Scope Integrity:** Login page remains completely unchanged.
- [ ] **Responsiveness:** Flawless experience from mobile viewports (375px) through large 4K displays (2560px).
- [ ] **Performance:** 60 FPS animations, zero layout shifts with skeletons, debounced queries.
- [ ] **Security Compliance:** All role and IDOR validations strictly preserved across all newly styled components.
