# Graph Report - Emobilize  (2026-10-22)

## Corpus Check
- 28 files · ~32,836 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1041 nodes · 2607 edges · 50 communities (39 shown, 11 thin omitted)
- Extraction: 92% EXTRACTED · 8% INFERRED · 1% AMBIGUOUS · INFERRED: 197 edges (avg confidence: 0.9)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Schedule repository
- Splash and forms
- App navigation shell
- Add task card
- Radial dock menu
- Launcher icon art
- Compose previews
- Task draft logic
- Task behavior notes
- Palette colors
- Auth and DI
- Design system tokens
- Auth form fields
- In-memory task store
- App settings prefs
- Task list screen
- Auth account store
- Task domain model
- Light app icon
- Auth view model
- Settings and theme
- Reminder card
- Main activity startup
- Launcher and metrics
- Calendar agenda
- In-memory data layer
- Category preferences
- Dark splash mark
- Sample tasks
- Typography and layers
- Category use cases
- Dock and theme orb
- Routes and navigation
- Settings controls
- CI and Gradle
- Logo composable
- Night app icon
- Reminder system notes
- Instrumented test
- Composition root
- Reminder options
- Add task options
- Graphify report
- Unit test

## God Nodes (most connected - your core abstractions)
1. `TasksViewModel` - 63 edges
2. `FabAction` - 26 edges
3. `AppContainer` - 24 edges
4. `TaskDraft` - 24 edges
5. `TimeBlock` - 23 edges
6. `Emobilize` - 22 edges
7. `AddTaskCard()` - 22 edges
8. `appStyle()` - 21 edges
9. `EmobilizeApp()` - 21 edges
10. `AuthViewModel` - 21 edges

## Surprising Connections (you probably didn't know these)
- `Fitur & Karakteristik Utama:` --references--> `appStyle()`  [INFERRED]
  PROJECT_REPORT.md → app/src/main/java/ayodong/emobilize/ui/theme/Type.kt
- `4. Anggota ke-4 / Christian Pratama (QA & Design System / Component Specialist)` --references--> `ExpandableReminderCard()`  [INFERRED]
  PROJECT_REPORT.md → app/src/main/java/ayodong/emobilize/ui/notifications/ExpandableReminderCard.kt
- `Event` --references--> `Category`  [INFERRED]
  HALAMAN.md → app/src/main/java/ayodong/emobilize/domain/model/Task.kt
- `Kartu pengingat` --references--> `Task`  [INFERRED]
  HALAMAN.md → app/src/main/java/ayodong/emobilize/domain/model/Task.kt
- `Yang tidak boleh` --references--> `TaskDraft`  [INFERRED]
  CONTEXT.md → app/src/main/java/ayodong/emobilize/ui/tasks/TasksViewModel.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
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
- **Left-aligned unequal capsules on charcoal** — app_src_main_res_drawable_night_ic_emobilize_logo_app_rounded_square, app_src_main_res_drawable_night_ic_emobilize_logo_app_top_bar, app_src_main_res_drawable_night_ic_emobilize_logo_app_middle_bar, app_src_main_res_drawable_night_ic_emobilize_logo_app_bottom_bar [EXTRACTED 1.00]
- **Equal Outer Bars and Short Middle Bar Form Letter E** — app_src_main_res_drawable_ic_emobilize_logo_app_top_bar, app_src_main_res_drawable_ic_emobilize_logo_app_middle_bar, app_src_main_res_drawable_ic_emobilize_logo_app_bottom_bar, app_src_main_res_drawable_ic_emobilize_logo_app_letter_e [EXTRACTED 1.00]
- **Light Rounded Tile Centers the Letter E** — app_src_main_res_drawable_ic_emobilize_logo_app_icon, app_src_main_res_drawable_ic_emobilize_logo_app_rounded_square, app_src_main_res_drawable_ic_emobilize_logo_app_light_fill, app_src_main_res_drawable_ic_emobilize_logo_app_letter_e [EXTRACTED 1.00]
- **Dark Logo Composition** — app_src_main_res_drawable_ic_emobilize_logo_dark_logo, app_src_main_res_drawable_ic_emobilize_logo_app_rounded_square, app_src_main_res_drawable_ic_emobilize_logo_dark_dark_fill, app_src_main_res_drawable_ic_emobilize_logo_dark_three_bar_e [EXTRACTED 1.00]
- **Emobilize Light Logo Composition** — app_src_main_res_drawable_ic_emobilize_logo_app_rounded_square, app_src_main_res_drawable_ic_emobilize_logo_app_light_fill, app_src_main_res_drawable_ic_emobilize_logo_light_three_bar_e [EXTRACTED 1.00]
- **Three pill bars form the letter E** — app_src_main_res_drawable_ic_emobilize_mark_dark_letter_e, app_src_main_res_drawable_ic_emobilize_mark_dark_top_bar, app_src_main_res_drawable_ic_emobilize_mark_dark_middle_bar, app_src_main_res_drawable_ic_emobilize_mark_dark_bottom_bar [EXTRACTED 1.00]
- **Dark splash lockup beside MOBILIZE** — app_src_main_res_drawable_ic_emobilize_mark_dark_mark, app_src_main_res_drawable_ic_emobilize_mark_dark_dark_splash, app_src_main_res_drawable_ic_emobilize_mark_dark_mobilize_word [INFERRED 0.85]
- **Light bars on transparency for a dark splash** — app_src_main_res_drawable_ic_emobilize_mark_dark_light_gray_fill, app_src_main_res_drawable_ic_emobilize_mark_dark_transparent_background, app_src_main_res_drawable_ic_emobilize_mark_dark_dark_splash [INFERRED 0.85]
- **Three-bar letter E** — app_src_main_res_drawable_ic_emobilize_mark_light_three_rounded_bars, app_src_main_res_drawable_ic_emobilize_mark_light_dark_charcoal, app_src_main_res_drawable_ic_emobilize_mark_dark_letter_e [EXTRACTED 1.00]
- **Light splash wordmark** — app_src_main_res_drawable_ic_emobilize_mark_light_mark, app_src_main_res_drawable_ic_emobilize_mark_dark_letter_e, app_src_main_res_drawable_ic_emobilize_mark_light_mobilize [INFERRED 0.95]

## Communities (50 total, 11 thin omitted)

### Community 0 - "Schedule repository"
Cohesion: 0.05
Nodes (51): InMemoryScheduleRepository, CalView, Day, Month, Week, formatBlockTime(), inputToHours(), monthCells() (+43 more)

### Community 1 - "Splash and forms"
Cohesion: 0.08
Nodes (3): ScheduleMode, Deadline, Range

### Community 2 - "App navigation shell"
Cohesion: 0.05
Nodes (31): AuthScreen(), SwitchLine(), CalendarFormDestination(), EmobilizeApp(), leave(), MainShell(), openTab(), openTasks() (+23 more)

### Community 3 - "Add task card"
Cohesion: 0.09
Nodes (33): AddTaskCard(), CategoryMark(), CategoryOptions(), OptionButton(), PriorityMark(), PriorityOptions(), ReminderOptions(), scheduleChosen() (+25 more)

### Community 5 - "Launcher icon art"
Cohesion: 0.06
Nodes (41): White Android Robot Mascot, Bottom-Right Drop Shadow, Android Green Grid Background, HDPI Android Launcher Icon, Android App Launcher Icon, Round launcher icon (hdpi), Android platform, Green circular field (+33 more)

### Community 6 - "Compose previews"
Cohesion: 0.09
Nodes (6): PreviewBackOwner, PreviewCategoryRepository, PreviewFrame(), PreviewSettingsRepository, previewViewModel(), ScreenPreview()

### Community 7 - "Task draft logic"
Cohesion: 0.10
Nodes (5): filterKeyFor(), isoDate(), TasksViewModel, Calendar, Pilih, edit, hapus

### Community 8 - "Task behavior notes"
Cohesion: 0.11
Nodes (29): AddTaskCard, CalendarFormSheet, deleteSelectedBlock, Task, Task filters, TaskDraft, TaskFormSheet, TasksScreen (+21 more)

### Community 9 - "Palette colors"
Cohesion: 0.10
Nodes (12): AppPalette, at(), categoryColor(), inkOn(), menuColor(), paletteFrom(), priorityColor(), hsl() (+4 more)

### Community 10 - "Auth and DI"
Cohesion: 0.13
Nodes (11): AppContainer, AuthViewModelFactory, TasksViewModelFactory, LoginUseCase, RegisterAccountUseCase, DeleteTimeBlockUseCase, AddTaskUseCase, DeleteTaskUseCase (+3 more)

### Community 11 - "Design system tokens"
Cohesion: 0.13
Nodes (27): TaskCard, Chart tokens, Chip, Neutral color tokens, Dock, Emobilize Design System, Input, Jarak (+19 more)

### Community 13 - "In-memory task store"
Cohesion: 0.11
Nodes (7): InMemoryTaskRepository, TaskStatus, Done, Progress, Todo, TaskBoard, TaskRepository

### Community 14 - "App settings prefs"
Cohesion: 0.13
Nodes (15): PreferencesAppSettingsRepository, AppSettings, ThemeMode, Dark, Light, System, WeekStart, Monday (+7 more)

### Community 15 - "Task list screen"
Cohesion: 0.17
Nodes (9): formatInputTime(), ClockIcon(), RoleBadge(), SelectionBanner(), StatusChoice(), StatusHeader(), TaskCard(), TaskMeta() (+1 more)

### Community 16 - "Auth account store"
Cohesion: 0.14
Nodes (13): PreferencesAuthRepository, StoredAccount, Account, AuthError, EmailTaken, InvalidEmail, InvalidName, ShortPassword (+5 more)

### Community 17 - "Task domain model"
Cohesion: 0.13
Nodes (18): Category, DateRange, deadlineRank(), deadlineToInputDate(), displayTimeToInput(), EventType, Event, RegularSchedule (+10 more)

### Community 18 - "Light app icon"
Cohesion: 0.14
Nodes (19): Android Launcher Icon, Long Bottom Bar, Charcoal Bar Color, Gray Edge Rim, Emobilize App Icon, Shared Left Alignment, Letter E Mark, Light Gray Fill (+11 more)

### Community 19 - "Auth view model"
Cohesion: 0.15
Nodes (5): isEmail(), AuthPage, Login, Register, AuthViewModel

### Community 20 - "Settings and theme"
Cohesion: 0.19
Nodes (19): LocalPalette, NeutralDark, NeutralLight, SettingsScreen, Shadcn scheme, AppSettings, Calendar settings, deadlineRank (+11 more)

### Community 21 - "Reminder card"
Cohesion: 0.13
Nodes (8): Priority, High, Low, Medium, ExpandableReminderCard(), PreviewDeadlineDarkExpanded(), Android Studio, @Preview

### Community 22 - "Main activity startup"
Cohesion: 0.13
Nodes (5): MainActivity, Arsitektur, Auth, Kartu pengingat, Yang ada di kode tetapi tidak dipasang

### Community 23 - "Launcher and metrics"
Cohesion: 0.13
Nodes (12): Android Launcher Artwork, White Android Robot, Green Squircle Background, Launcher Icon, AppFont, AppMetrics, Offline Kotlin compile, Emobilize (+4 more)

### Community 24 - "Calendar agenda"
Cohesion: 0.17
Nodes (15): AgendaRow, CalendarScreen, DayAgenda, EventType, Agenda, Calendar, Calendar task card, Day (+7 more)

### Community 26 - "In-memory data layer"
Cohesion: 0.25
Nodes (15): In-memory data, SampleData, In-memory repositories, Android, Android SDK 37, Local Compile Verification, Data/DI Layer, Git (+7 more)

### Community 28 - "Category preferences"
Cohesion: 0.18
Nodes (7): PreferencesCategoryRepository, Konteks Emobilize, Model, Prompt yang bisa ditempel, Struktur folder, Token tampilan, Yang tidak boleh

### Community 29 - "Dark splash mark"
Cohesion: 0.19
Nodes (14): Full-Width Bottom Bar, Dark Splash, Stylized Letter E, Light Gray Bar Fill, Emobilize Dark Mark, Short Left-Aligned Middle Bar, MOBILIZE Word, Full-Width Top Bar (+6 more)

### Community 30 - "Sample tasks"
Cohesion: 0.19
Nodes (8): sampleBlocks(), sampleTasks(), FilterKey, All, Later, Today, Tomorrow, formatInputDate()

### Community 31 - "Typography and layers"
Cohesion: 0.26
Nodes (11): Montserrat, appStyle(), DayAgenda, Design System and Theming, Domain Layer, domain/usecase, ui/calendar, UI Layer (+3 more)

### Community 32 - "Category use cases"
Cohesion: 0.23
Nodes (3): CategoryRepository, GetCategoriesUseCase, UpdateCategoryUseCase

### Community 33 - "Dock and theme orb"
Cohesion: 0.21
Nodes (11): BottomDock, EmobilizeApp, FabFace, menuColor, RadialMenu, ThemeOrb, Application shell, Auth error messages (+3 more)

### Community 34 - "Routes and navigation"
Cohesion: 0.25
Nodes (10): AppRoute, AppRoute.AddTask, AppRoute.Settings, AppRoute.Tasks, Task CRUD, Louis Alexander Andreas (LouisAlexanderAndreas), NavHost, RadialDock (+2 more)

### Community 35 - "Settings controls"
Cohesion: 0.38
Nodes (7): CategoryEditor(), ChoiceRow(), OptionChip(), SectionLabel(), SettingHeading(), SettingsCard(), SettingsScreen()

### Community 36 - "CI and Gradle"
Cohesion: 0.24
Nodes (9): Android SDK, :app:assembleDebug, check, CI, dependency-graph, Gradle dependency submission, JDK 25 Temurin, :app:resolveCiDependencies (+1 more)

### Community 38 - "Night app icon"
Cohesion: 0.39
Nodes (7): Bottom long light capsule, Emobilize night app icon, Short middle light capsule, Android night drawable, Charcoal rounded-square tile, Stemless three-bar letter E, Top long light capsule

### Community 39 - "Reminder system notes"
Cohesion: 0.39
Nodes (7): ExpandableReminderCard, Christian Pratama, ic_emobilize_logo.xml, ic_launcher_foreground.xml, Monogram E, Push Notification and Reminder System, Reminder Option (None, At deadline, 10 minutes before, 1 hour before, 1 day before)

### Community 41 - "Composition root"
Cohesion: 0.48
Nodes (5): AppContainer, MainActivity, TasksViewModel, TasksViewModelFactory, God Node

### Community 42 - "Reminder options"
Cohesion: 0.33
Nodes (6): ReminderOption, AtDeadline, None, OneDayBefore, OneHourBefore, TenMinutesBefore

### Community 43 - "Add task options"
Cohesion: 0.40
Nodes (5): AddOption, Category, Priority, Reminder, Schedule

### Community 44 - "Graphify report"
Cohesion: 0.70
Nodes (5): Emobilize Project Report, graph.json, GRAPH_REPORT.md, Graphify, No Circular Imports

## Ambiguous Edges - Review These
- `ScheduleUseCases.kt` → `GetAppSettingsUseCase`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: conceptually_related_to
- `ScheduleUseCases.kt` → `UpdateAppSettingsUseCase`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: conceptually_related_to
- `TaskUseCases.kt` → `UpdateCategoryUseCase`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: conceptually_related_to
- `Pale green grid field` → `Round launcher icon (hdpi)`  [AMBIGUOUS]
  app/src/main/res/mipmap-hdpi/ic_launcher_round.webp · relation: references
- `Emobilize` → `Christian Pratama`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `CalendarFormSheet` → `ui/calendar`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: conceptually_related_to
- `SettingsScreen` → `ThemeOrb`  [AMBIGUOUS]
  CONTEXT.md · relation: conceptually_related_to
- `ExpandableReminderCard` → `Christian Pratama`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `Previews.kt` → `Christian Pratama`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `Emobilize Project Report` → `Christian Pratama`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `Monogram E` → `Christian Pratama`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `Design System and Theming` → `Christian Pratama`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `Christian Pratama` → `ic_emobilize_logo.xml`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references
- `Christian Pratama` → `ic_launcher_foreground.xml`  [AMBIGUOUS]
  PROJECT_REPORT.md · relation: references

## Knowledge Gaps
- **92 isolated node(s):** `Dark`, `Light`, `System`, `Monday`, `Sunday` (+87 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 247 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **11 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `TasksViewModel` connect `Task draft logic` to `App navigation shell`, `Add task card`, `Compose previews`, `Auth and DI`, `In-memory task store`, `Task list screen`, `Task domain model`, `Main activity startup`, `Sample tasks`?**
  _High betweenness centrality (0.090) - this node is a cross-community bridge._
- **Are the 4 inferred relationships involving `TasksViewModel` (e.g. with `Arsitektur` and `Tasks`) actually correct?**
  _`TasksViewModel` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Dark`, `Light`, `System` to the rest of the system?**
  _92 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Schedule repository` be split into smaller, more focused modules?**
  _Cohesion score 0.05054945054945055 - nodes in this community are weakly interconnected._
- **Why does `Emobilize` connect `Launcher and metrics` to `Routes and navigation`, `CI and Gradle`, `Night app icon`, `Reminder system notes`, `Task behavior notes`, `Graphify report`, `Settings and theme`, `In-memory data layer`, `Typography and layers`?**
  _High betweenness centrality (0.074) - this node is a cross-community bridge._

### Low-confidence Hints
_AMBIGUOUS edges — the extractor was unsure. Verify before acting on these._

- **What is the exact relationship between `ScheduleUseCases.kt` and `GetAppSettingsUseCase`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._
- **What is the exact relationship between `ScheduleUseCases.kt` and `UpdateAppSettingsUseCase`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._