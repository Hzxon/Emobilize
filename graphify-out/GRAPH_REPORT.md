# Graph Report - Emobilize  (2026-10-22)

## Corpus Check
- 54 files · ~33,346 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 23 file(s) not represented in the graph (top: .xml 11, .ttf 4, .properties 3)

## Summary
- 1001 nodes · 2680 edges · 55 communities (41 shown, 14 thin omitted)
- Extraction: 92% EXTRACTED · 7% INFERRED · 1% AMBIGUOUS · INFERRED: 197 edges (avg confidence: 0.9)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `192a0f82`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Schedule repository
- Splash and forms
- EmobilizeApp
- Add task card
- Radial dock menu
- Launcher icon art
- EmobilizePreviews.kt
- Task draft logic
- CalendarFormSheet
- Palette colors
- TasksViewModel.kt
- Design system tokens
- Task.kt
- In-memory task store
- App settings prefs
- RadialMenu
- Auth account store
- Task
- Light app icon
- Calendar
- Settings and theme
- Previews.kt
- Main activity startup
- Emobilize
- Calendar agenda
- TasksScreen.kt
- In-memory data layer
- Category preferences
- Dark splash mark
- FilterKey
- Typography and layers
- Palette.kt
- Dock and theme orb
- AppRoute
- ExpandableReminderCard
- CI and Gradle
- Logo composable
- Night app icon
- Reminder system notes
- Instrumented test
- TasksScreen
- Reminder options
- Add task options
- Emobilize — Mobile Task & Schedule Management
- Unit test
- Halaman Emobilize
- deadlineToInputDate
- EventType
- Priority
- BottomDock

## God Nodes (most connected - your core abstractions)
1. `appStyle()` - 66 edges
2. `TasksViewModel` - 63 edges
3. `TimeBlock` - 27 edges
4. `AppContainer` - 26 edges
5. `FabAction` - 26 edges
6. `AppSettings` - 24 edges
7. `TaskDraft` - 24 edges
8. `AddTaskCard()` - 23 edges
9. `EmobilizeApp()` - 22 edges
10. `Emobilize` - 22 edges

## Surprising Connections (you probably didn't know these)
- `Event` --references--> `Category`  [INFERRED]
  HALAMAN.md → app/src/main/java/ayodong/emobilize/domain/model/Task.kt
- `Kartu pengingat` --references--> `Task`  [INFERRED]
  HALAMAN.md → app/src/main/java/ayodong/emobilize/domain/model/Task.kt
- `1. Metode Pembagian Tugas (Separation of Concerns)` --references--> `AppRoute`  [INFERRED]
  PROJECT_REPORT.md → app/src/main/java/ayodong/emobilize/ui/navigation/AppRoute.kt
- `4. Anggota ke-4 / Christian Pratama (QA & Design System / Component Specialist)` --references--> `ExpandableReminderCard()`  [INFERRED]
  PROJECT_REPORT.md → app/src/main/java/ayodong/emobilize/ui/notifications/ExpandableReminderCard.kt
- `Fitur & Karakteristik Utama:` --references--> `appStyle()`  [INFERRED]
  PROJECT_REPORT.md → app/src/main/java/ayodong/emobilize/ui/theme/Type.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Light Rounded Tile Centers the Letter E** — app_src_main_res_drawable_ic_emobilize_logo_app_icon, app_src_main_res_drawable_ic_emobilize_logo_app_rounded_square, app_src_main_res_drawable_ic_emobilize_logo_app_light_fill, app_src_main_res_drawable_ic_emobilize_logo_app_letter_e [EXTRACTED 1.00]
- **Equal Outer Bars and Short Middle Bar Form Letter E** — app_src_main_res_drawable_ic_emobilize_logo_app_top_bar, app_src_main_res_drawable_ic_emobilize_logo_app_middle_bar, app_src_main_res_drawable_ic_emobilize_logo_app_bottom_bar, app_src_main_res_drawable_ic_emobilize_logo_app_letter_e [EXTRACTED 1.00]
- **Dark Logo Composition** — app_src_main_res_drawable_ic_emobilize_logo_dark_logo, app_src_main_res_drawable_ic_emobilize_logo_app_rounded_square, app_src_main_res_drawable_ic_emobilize_logo_dark_dark_fill, app_src_main_res_drawable_ic_emobilize_logo_dark_three_bar_e [EXTRACTED 1.00]
- **Emobilize Light Logo Composition** — app_src_main_res_drawable_ic_emobilize_logo_app_rounded_square, app_src_main_res_drawable_ic_emobilize_logo_app_light_fill, app_src_main_res_drawable_ic_emobilize_logo_light_three_bar_e [EXTRACTED 1.00]
- **Three pill bars form the letter E** — app_src_main_res_drawable_ic_emobilize_mark_dark_letter_e, app_src_main_res_drawable_ic_emobilize_mark_dark_top_bar, app_src_main_res_drawable_ic_emobilize_mark_dark_middle_bar, app_src_main_res_drawable_ic_emobilize_mark_dark_bottom_bar [EXTRACTED 1.00]
- **Three-bar letter E** — app_src_main_res_drawable_ic_emobilize_mark_light_three_rounded_bars, app_src_main_res_drawable_ic_emobilize_mark_light_dark_charcoal, app_src_main_res_drawable_ic_emobilize_mark_dark_letter_e [EXTRACTED 1.00]
- **Left-aligned unequal capsules on charcoal** — app_src_main_res_drawable_night_ic_emobilize_logo_app_rounded_square, app_src_main_res_drawable_night_ic_emobilize_logo_app_top_bar, app_src_main_res_drawable_night_ic_emobilize_logo_app_middle_bar, app_src_main_res_drawable_night_ic_emobilize_logo_app_bottom_bar [EXTRACTED 1.00]
- **Composed round Android launcher mark** — app_src_main_res_mipmap_hdpi_ic_launcher_round, app_src_main_res_mipmap_xhdpi_ic_launcher_android_robot, app_src_main_res_mipmap_hdpi_ic_launcher_round_green_disk [EXTRACTED 1.00]
- **Launcher Icon Visual Composition** — app_src_main_res_mipmap_hdpi_ic_launcher_icon, app_src_main_res_mipmap_hdpi_ic_launcher_android_robot, app_src_main_res_mipmap_hdpi_ic_launcher_green_grid, app_src_main_res_mipmap_hdpi_ic_launcher_rounded_square, app_src_main_res_mipmap_hdpi_ic_launcher_drop_shadow [EXTRACTED 1.00]
- **Launcher Icon Composition** — app_src_main_res_mipmap_mdpi_ic_launcher_launcher_icon, app_src_main_res_mipmap_mdpi_ic_launcher_android_robot, app_src_main_res_mipmap_mdpi_ic_launcher_green_squircle [EXTRACTED 1.00]
- **Round Android launcher glyph** — app_src_main_res_mipmap_mdpi_ic_launcher_round_icon, app_src_main_res_mipmap_hdpi_ic_launcher_android_robot, app_src_main_res_mipmap_mdpi_ic_launcher_round_green_disc, app_src_main_res_mipmap_mdpi_ic_launcher_round_round_crop [EXTRACTED 1.00]
- **Launcher Icon Composition** — app_src_main_res_mipmap_xhdpi_ic_launcher, app_src_main_res_mipmap_xhdpi_ic_launcher_android_robot, app_src_main_res_mipmap_xhdpi_ic_launcher_green_grid, app_src_main_res_mipmap_xhdpi_ic_launcher_rounded_square, app_src_main_res_mipmap_hdpi_ic_launcher_drop_shadow [EXTRACTED 1.00]
- **Round launcher icon composition** — app_src_main_res_mipmap_xhdpi_ic_launcher_android_robot, app_src_main_res_mipmap_mdpi_ic_launcher_round_green_disc, app_src_main_res_mipmap_xhdpi_ic_launcher_round_grid_pattern [EXTRACTED 1.00]
- **Launcher Icon Composition** — app_src_main_res_mipmap_xxhdpi_ic_launcher_android_robot, app_src_main_res_mipmap_xhdpi_ic_launcher_green_grid, app_src_main_res_mipmap_xxhdpi_ic_launcher_material_long_shadow, app_src_main_res_mipmap_xhdpi_ic_launcher_rounded_square [EXTRACTED 1.00]
- **Round launcher glyph composition** — app_src_main_res_mipmap_xxhdpi_ic_launcher_round_android_robot, app_src_main_res_mipmap_xxhdpi_ic_launcher_round_green_circular_field, app_src_main_res_mipmap_xxhdpi_ic_launcher_round_white_grid_overlay, app_src_main_res_mipmap_xxhdpi_ic_launcher_round_long_shadow [EXTRACTED 1.00]
- **Round Android launcher icon composition** — app_src_main_res_mipmap_xxxhdpi_ic_launcher_android_robot, app_src_main_res_mipmap_xxxhdpi_ic_launcher_round_green_grid, app_src_main_res_mipmap_xxhdpi_ic_launcher_round_circular_mask, app_src_main_res_mipmap_hdpi_ic_launcher_drop_shadow [EXTRACTED 1.00]
- **Launcher Icon Visual Composition** — app_src_main_res_mipmap_xxxhdpi_ic_launcher_icon, app_src_main_res_mipmap_xxxhdpi_ic_launcher_android_robot, app_src_main_res_mipmap_hdpi_ic_launcher_green_grid, app_src_main_res_mipmap_hdpi_ic_launcher_rounded_square, app_src_main_res_mipmap_xxxhdpi_ic_launcher_long_shadow [EXTRACTED 1.00]
- **CI check pipeline** — github_workflows_ci_check, github_workflows_ci_resolvecidependencies, github_workflows_ci_testdebugunittest, github_workflows_ci_assembledebug [EXTRACTED 1.00]
- **Manual layered composition root** — context_mainactivity, context_appcontainer, context_tasksviewmodelfactory, context_tasksviewmodel, context_layered_architecture, context_in_memory_data [EXTRACTED 1.00]
- **Product colors alias chart and destructive tokens** — design_product_colors, design_chart_tokens, design_color_tokens, design_neutral_light, design_neutral_dark [EXTRACTED 1.00]
- **System back dismissal order** — halaman_back_order, context_settingsscreen, context_addtaskcard, context_calendarformsheet, context_taskformsheet, halaman_radial_menu, halaman_calendar_selection [EXTRACTED 1.00]
- **Code present but not mounted** — context_emobilizeapp, halaman_auth_screen, halaman_reminder_card [EXTRACTED 1.00]
- **Emobilize Development Team** — project_report_louis_alexander_andreas, project_report_stevent_richardo, project_report_nathan_lee, project_report_christian_pratama [EXTRACTED 1.00]
- **UI Navigation Stack** — project_report_nav_host, project_report_remember_nav_controller, project_report_app_route, project_report_radial_dock [EXTRACTED 1.00]
- **Settings and Category Use Cases** — project_report_update_category_use_case, project_report_get_app_settings_use_case, project_report_update_app_settings_use_case [EXTRACTED 1.00]
- **Light bars on transparency for a dark splash** — app_src_main_res_drawable_ic_emobilize_mark_dark_light_gray_fill, app_src_main_res_drawable_ic_emobilize_mark_dark_transparent_background, app_src_main_res_drawable_ic_emobilize_mark_dark_dark_splash [INFERRED 0.85]
- **Dark splash lockup beside MOBILIZE** — app_src_main_res_drawable_ic_emobilize_mark_dark_mark, app_src_main_res_drawable_ic_emobilize_mark_dark_dark_splash, app_src_main_res_drawable_ic_emobilize_mark_dark_mobilize_word [INFERRED 0.85]
- **Light splash wordmark** — app_src_main_res_drawable_ic_emobilize_mark_light_mark, app_src_main_res_drawable_ic_emobilize_mark_dark_letter_e, app_src_main_res_drawable_ic_emobilize_mark_light_mobilize [INFERRED 0.95]

## Communities (55 total, 14 thin omitted)

### Community 0 - "Schedule repository"
Cohesion: 0.05
Nodes (60): sampleBlocks(), sampleTasks(), CalView, Day, Month, Week, formatBlockTime(), inputToHours() (+52 more)

### Community 2 - "EmobilizeApp"
Cohesion: 0.24
Nodes (7): CalendarFormDestination(), EmobilizeApp(), leave(), MainShell(), openTasks(), TabPage(), AppRoute

### Community 3 - "Add task card"
Cohesion: 0.06
Nodes (36): filterKeyFor(), errorInk(), FormField(), AddTaskCard(), CategoryMark(), CategoryOptions(), OptionButton(), PriorityMark() (+28 more)

### Community 5 - "Launcher icon art"
Cohesion: 0.06
Nodes (41): White Android Robot Mascot, Bottom-Right Drop Shadow, Android Green Grid Background, HDPI Android Launcher Icon, Android App Launcher Icon, Round launcher icon (hdpi), Android platform, Green circular field (+33 more)

### Community 7 - "Task draft logic"
Cohesion: 0.13
Nodes (3): hoursToInput(), TasksViewModel, 2. Sistem Kerja Kelompok (Working System)

### Community 8 - "CalendarFormSheet"
Cohesion: 0.13
Nodes (21): CalendarFormSheet, TaskFormSheet, Appearance, Calendar, canSubmitCalendar, Categories, Category.Limit, Event (+13 more)

### Community 9 - "Palette colors"
Cohesion: 0.13
Nodes (8): PreviewFrame(), ScreenPreview(), AppPalette, paletteFrom(), hsl(), ShadcnColors, ShadcnSpace, EmobilizeTheme()

### Community 10 - "TasksViewModel.kt"
Cohesion: 0.15
Nodes (14): previewViewModel(), InMemoryScheduleRepository, AppContainer, ScheduleRepository, GetCategoriesUseCase, UpdateCategoryUseCase, DeleteTimeBlockUseCase, GetScheduleUseCase (+6 more)

### Community 11 - "Design system tokens"
Cohesion: 0.13
Nodes (27): TaskCard, Chart tokens, Chip, Neutral color tokens, Dock, Emobilize Design System, Input, Jarak (+19 more)

### Community 12 - "Task.kt"
Cohesion: 0.20
Nodes (7): displayAngle(), NavTab, Calendar, Tasks, ScheduleMode, Deadline, Range

### Community 13 - "In-memory task store"
Cohesion: 0.11
Nodes (8): InMemoryTaskRepository, TaskStatus, Done, Progress, Todo, TaskBoard, TaskRepository, StatusHeader()

### Community 14 - "App settings prefs"
Cohesion: 0.12
Nodes (16): PreviewSettingsRepository, PreferencesAppSettingsRepository, AppSettings, ThemeMode, Dark, Light, System, WeekStart (+8 more)

### Community 15 - "RadialMenu"
Cohesion: 0.20
Nodes (7): FabFace(), actionIcon(), RadialMenu(), 1. Louis Alexander Andreas (Lead Developer / UI & Navigation Architect), 3. Nathan Lee (Repository & Environment Specialist / DevOps), 4. Anggota ke-4 / Christian Pratama (QA & Design System / Component Specialist), d. Specific Task/Contribution of Each Individual Member

### Community 16 - "Auth account store"
Cohesion: 0.08
Nodes (20): PreferencesAuthRepository, StoredAccount, Account, AuthError, EmailTaken, InvalidEmail, InvalidName, ShortPassword (+12 more)

### Community 17 - "Task"
Cohesion: 0.25
Nodes (9): deadlineRank(), isOverdue(), Task, Aksi dari radial di tab ini, Filter, Isi layar, Kartu tugas, Model yang dipakai semua halaman (+1 more)

### Community 18 - "Light app icon"
Cohesion: 0.14
Nodes (19): Android Launcher Icon, Long Bottom Bar, Charcoal Bar Color, Gray Edge Rim, Emobilize App Icon, Shared Left Alignment, Letter E Mark, Light Gray Fill (+11 more)

### Community 19 - "Calendar"
Cohesion: 0.20
Nodes (7): Calendar, Calendar, Day, Header, Month, Pilih, edit, hapus, Week

### Community 20 - "Settings and theme"
Cohesion: 0.18
Nodes (20): LocalPalette, NeutralDark, NeutralLight, SettingsScreen, Shadcn scheme, AppSettings, Calendar settings, deadlineRank (+12 more)

### Community 21 - "Previews.kt"
Cohesion: 0.32
Nodes (3): PreviewDeadlineDarkExpanded(), Android Studio, @Preview

### Community 22 - "Main activity startup"
Cohesion: 0.12
Nodes (7): AuthViewModelFactory, TasksViewModelFactory, MainActivity, Arsitektur, Auth, Kartu pengingat, Yang ada di kode tetapi tidak dipasang

### Community 23 - "Emobilize"
Cohesion: 0.10
Nodes (21): Android Launcher Artwork, White Android Robot, Green Squircle Background, Launcher Icon, AppFont, AppMetrics, Offline Kotlin compile, Emobilize (+13 more)

### Community 24 - "Calendar agenda"
Cohesion: 0.16
Nodes (16): AgendaRow, CalendarScreen, DayAgenda, deleteSelectedBlock, EventType, Task, TimeBlock, Agenda (+8 more)

### Community 26 - "In-memory data layer"
Cohesion: 0.25
Nodes (15): In-memory data, SampleData, In-memory repositories, Android, Android SDK 37, Local Compile Verification, Data/DI Layer, Git (+7 more)

### Community 28 - "Category preferences"
Cohesion: 0.19
Nodes (3): PreferencesCategoryRepository, Category, CategoryRepository

### Community 29 - "Dark splash mark"
Cohesion: 0.19
Nodes (14): Full-Width Bottom Bar, Dark Splash, Stylized Letter E, Light Gray Bar Fill, Emobilize Dark Mark, Short Left-Aligned Middle Bar, MOBILIZE Word, Full-Width Top Bar (+6 more)

### Community 30 - "FilterKey"
Cohesion: 0.25
Nodes (6): filteredTasks(), FilterKey, All, Later, Today, Tomorrow

### Community 31 - "Typography and layers"
Cohesion: 0.29
Nodes (10): Montserrat, appStyle(), Design System and Theming, Domain Layer, domain/usecase, ui/calendar, UI Layer, ui/notifications (+2 more)

### Community 32 - "Palette.kt"
Cohesion: 0.39
Nodes (5): at(), categoryColor(), inkOn(), menuColor(), priorityColor()

### Community 33 - "Dock and theme orb"
Cohesion: 0.32
Nodes (7): BottomDock, FabFace, menuColor, RadialMenu, ThemeOrb, Application shell, Radial menu

### Community 34 - "AppRoute"
Cohesion: 0.25
Nodes (8): AppRoute, AppRoute.AddTask, AppRoute.Calendar, AppRoute.Settings, AppRoute.Tasks, Calendar and Schedule Blocking, DayAgenda, Time Blocking

### Community 35 - "ExpandableReminderCard"
Cohesion: 0.29
Nodes (4): AuthScreen(), SwitchLine(), ExpandableReminderCard(), Alur yang benar-benar terbuka

### Community 36 - "CI and Gradle"
Cohesion: 0.24
Nodes (9): Android SDK, :app:assembleDebug, check, CI, dependency-graph, Gradle dependency submission, JDK 25 Temurin, :app:resolveCiDependencies (+1 more)

### Community 38 - "Night app icon"
Cohesion: 0.39
Nodes (7): Bottom long light capsule, Emobilize night app icon, Short middle light capsule, Android night drawable, Charcoal rounded-square tile, Stemless three-bar letter E, Top long light capsule

### Community 39 - "Reminder system notes"
Cohesion: 0.33
Nodes (8): ExpandableReminderCard, Christian Pratama, ic_emobilize_logo.xml, ic_launcher_foreground.xml, Monogram E, Notification Quick Actions (Mark as Done, Reschedule), Reminder Option (None, At deadline, 10 minutes before, 1 hour before, 1 day before), Task Status (To-do, In Progress, Done)

### Community 41 - "TasksScreen"
Cohesion: 0.18
Nodes (15): AddTaskCard, AppContainer, EmobilizeApp, MainActivity, Task filters, TaskDraft, TasksScreen, TasksViewModel (+7 more)

### Community 42 - "Reminder options"
Cohesion: 0.33
Nodes (6): ReminderOption, AtDeadline, None, OneDayBefore, OneHourBefore, TenMinutesBefore

### Community 43 - "Add task options"
Cohesion: 0.40
Nodes (5): AddOption, Category, Priority, Reminder, Schedule

### Community 44 - "Emobilize — Mobile Task & Schedule Management"
Cohesion: 0.29
Nodes (6): 1. Metode Pembagian Tugas (Separation of Concerns), a. Brief Description of the App, b. Full Names of All Group Members, c. Explanation of How Tasks Were Divided and the Group's Working System, Emobilize — Mobile Task & Schedule Management, Fitur & Karakteristik Utama:

### Community 50 - "Halaman Emobilize"
Cohesion: 0.33
Nodes (5): Add task, Edit task, Halaman Emobilize, Kerangka aplikasi, Radial menu dan dock

### Community 52 - "EventType"
Cohesion: 0.50
Nodes (4): EventType, Event, RegularSchedule, Task

### Community 53 - "Priority"
Cohesion: 0.50
Nodes (4): Priority, High, Low, Medium

## Ambiguous Edges - Review These
- `ScheduleUseCases.kt` → `GetAppSettingsUseCase`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: conceptually_related_to
- `ScheduleUseCases.kt` → `UpdateAppSettingsUseCase`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: conceptually_related_to
- `TaskUseCases.kt` → `UpdateCategoryUseCase`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: conceptually_related_to
- `Previews.kt` → `Christian Pratama`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `SettingsScreen` → `ThemeOrb`  [AMBIGUOUS]
  CONTEXT.md · relation: conceptually_related_to
- `Emobilize` → `Christian Pratama`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `Design System and Theming` → `Christian Pratama`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `ui/calendar` → `CalendarFormSheet`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: conceptually_related_to
- `ExpandableReminderCard` → `Christian Pratama`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `Christian Pratama` → `ic_emobilize_logo.xml`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `Christian Pratama` → `ic_launcher_foreground.xml`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `Christian Pratama` → `Monogram E`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `Christian Pratama` → `Emobilize Project Report`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `Pale green grid field` → `Round launcher icon (hdpi)`  [AMBIGUOUS]
  app/src/main/res/mipmap-hdpi/ic_launcher_round.webp · relation: references

## Knowledge Gaps
- **92 isolated node(s):** `InvalidName`, `InvalidEmail`, `ShortPassword`, `EmailTaken`, `WrongCredentials` (+87 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 239 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **14 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Emobilize` connect `Emobilize` to `AppRoute`, `CI and Gradle`, `Night app icon`, `Reminder system notes`, `CalendarFormSheet`, `Settings and theme`, `In-memory data layer`, `Typography and layers`?**
  _High betweenness centrality (0.086) - this node is a cross-community bridge._
- **Are the 3 inferred relationships involving `appStyle()` (e.g. with `Mau ubah apa` and `2. Sistem Kerja Kelompok (Working System)`) actually correct?**
  _`appStyle()` has 3 INFERRED edges - model-reasoned connections that need verification._
- **What connects `InvalidName`, `InvalidEmail`, `ShortPassword` to the rest of the system?**
  _92 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Schedule repository` be split into smaller, more focused modules?**
  _Cohesion score 0.05049442457395329 - nodes in this community are weakly interconnected._
- **Why does `appStyle()` connect `Schedule repository` to `Splash and forms`, `ExpandableReminderCard`, `Radial dock menu`, `Add task card`, `Task draft logic`, `Emobilize — Mobile Task & Schedule Management`, `In-memory task store`, `RadialMenu`, `BottomDock`, `TasksScreen.kt`?**
  _High betweenness centrality (0.070) - this node is a cross-community bridge._

### Low-confidence Hints
_AMBIGUOUS edges — the extractor was unsure. Verify before acting on these._

- **What is the exact relationship between `ScheduleUseCases.kt` and `GetAppSettingsUseCase`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._
- **What is the exact relationship between `ScheduleUseCases.kt` and `UpdateAppSettingsUseCase`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._