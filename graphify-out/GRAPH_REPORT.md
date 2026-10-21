# Graph Report - Emobilize  (2026-10-21)

## Corpus Check
- 47 files · ~25,803 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 23 file(s) not represented in the graph (top: .xml 11, .ttf 4, .properties 3)

## Summary
- 761 nodes · 1989 edges · 33 communities (22 shown, 11 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 116 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `13f6e2e0`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- AddTaskCard.kt
- Calendar Domain Model
- App UI Architecture
- Add Task Card
- Preferences Auth Store
- Launcher Icon Art
- Tasks ViewModel
- RadialDock.kt
- In-Memory Task Store
- Schedule Repository
- App Settings Store
- Use Case Wiring
- Category Preferences
- Theme And Palette
- Task Time Helpers
- CI And Product Docs
- CalendarScreen.kt
- Sample Schedule Data
- Halaman Emobilize
- Instrumented Test
- EventType
- Task Date Filters
- ReminderOption
- Tasks
- Add Card Options
- Unit Test Example
- Priority
- TasksScreen.kt

## God Nodes (most connected - your core abstractions)
1. `appStyle()` - 58 edges
2. `TasksViewModel` - 56 edges
3. `TimeBlock` - 27 edges
4. `AppContainer` - 25 edges
5. `FabAction` - 25 edges
6. `AddTaskCard()` - 21 edges
7. `TaskDraft` - 21 edges
8. `AppSettings` - 20 edges
9. `EmobilizeApp()` - 19 edges
10. `Category` - 18 edges

## Surprising Connections (you probably didn't know these)
- `Categories` --references--> `AppSettings`  [INFERRED]
  HALAMAN.md → app/src/main/java/ayodong/emobilize/domain/model/AppSettings.kt
- `Event` --references--> `Category`  [INFERRED]
  HALAMAN.md → app/src/main/java/ayodong/emobilize/domain/model/Task.kt
- `Kartu pengingat` --references--> `Task`  [INFERRED]
  HALAMAN.md → app/src/main/java/ayodong/emobilize/domain/model/Task.kt
- `Filter` --references--> `deadlineRank()`  [INFERRED]
  HALAMAN.md → app/src/main/java/ayodong/emobilize/domain/model/Task.kt
- `Arsitektur` --references--> `MainActivity`  [INFERRED]
  CONTEXT.md → app/src/main/java/ayodong/emobilize/MainActivity.kt

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
- **Product colors alias chart and destructive tokens** — design_product_colors, design_chart_tokens, design_color_tokens, design_neutral_light, design_neutral_dark [EXTRACTED 1.00]
- **UI ViewModel use case repository data flow** — context_emobilizeapp, context_tasksviewmodel, context_taskusecases, context_scheduleusecases, context_repositories, context_inmemorytaskrepository, context_inmemoryschedulerepository [INFERRED 0.85]

## Communities (33 total, 11 thin omitted)

### Community 1 - "Calendar Domain Model"
Cohesion: 0.06
Nodes (51): taskDate(), taskOccursOn(), displayAngle(), FabAction, Add, Delete, Edit, Update (+43 more)

### Community 2 - "App UI Architecture"
Cohesion: 0.05
Nodes (57): AddTaskCard, AppContainer, AppMetrics, appStyle, BottomDock, CalendarFormSheet, CalendarScreen, EmobilizeApp (+49 more)

### Community 3 - "Add Task Card"
Cohesion: 0.09
Nodes (34): rangeIsValid(), AddTaskCard(), CategoryMark(), CategoryOptions(), OptionButton(), PriorityMark(), PriorityOptions(), ReminderOptions() (+26 more)

### Community 4 - "Preferences Auth Store"
Cohesion: 0.08
Nodes (19): PreferencesAuthRepository, StoredAccount, Account, AuthError, EmailTaken, InvalidEmail, InvalidName, ShortPassword (+11 more)

### Community 5 - "Launcher Icon Art"
Cohesion: 0.06
Nodes (41): White Android Robot Mascot, Bottom-Right Drop Shadow, Android Green Grid Background, HDPI Android Launcher Icon, Android App Launcher Icon, Round launcher icon (hdpi), Android platform, Green circular field (+33 more)

### Community 9 - "In-Memory Task Store"
Cohesion: 0.12
Nodes (7): InMemoryTaskRepository, TaskStatus, Done, Progress, Todo, TaskBoard, TaskRepository

### Community 10 - "Schedule Repository"
Cohesion: 0.12
Nodes (9): InMemoryScheduleRepository, formatBlockTime(), hoursToInput(), inputToHours(), monthCells(), placeSchedule(), replaceBlock(), TimeBlock (+1 more)

### Community 11 - "App Settings Store"
Cohesion: 0.12
Nodes (16): PreferencesAppSettingsRepository, AppSettings, ThemeMode, Dark, Light, System, WeekStart, Monday (+8 more)

### Community 12 - "Use Case Wiring"
Cohesion: 0.17
Nodes (12): AppContainer, AuthViewModelFactory, TasksViewModelFactory, GetCategoriesUseCase, DeleteTimeBlockUseCase, GetScheduleUseCase, PlaceTimeBlockUseCase, AddTaskUseCase (+4 more)

### Community 13 - "Category Preferences"
Cohesion: 0.14
Nodes (4): PreferencesCategoryRepository, Category, CategoryRepository, UpdateCategoryUseCase

### Community 14 - "Theme And Palette"
Cohesion: 0.11
Nodes (9): AppPalette, categoryColor(), inkOn(), paletteFrom(), priorityColor(), hsl(), ShadcnColors, ShadcnSpace (+1 more)

### Community 15 - "Task Time Helpers"
Cohesion: 0.22
Nodes (8): deadlineRank(), deadlineToInputDate(), displayTimeToInput(), isOverdue(), parseDisplayTime(), ScheduleMode, Deadline, Range

### Community 16 - "CI And Product Docs"
Cohesion: 0.14
Nodes (15): Android Launcher Artwork, White Android Robot, Green Squircle Background, Launcher Icon, compileDebugKotlin, Emobilize, Android SDK, :app:assembleDebug (+7 more)

### Community 18 - "Sample Schedule Data"
Cohesion: 0.33
Nodes (6): sampleBlocks(), sampleTasks(), weekOf(), filterKeyFor(), formatInputDate(), isoDate()

### Community 19 - "Halaman Emobilize"
Cohesion: 0.06
Nodes (25): MainActivity, AuthField(), AuthScreen(), SwitchLine(), ExpandableReminderCard(), Add task, Alur yang benar-benar terbuka, Appearance (+17 more)

### Community 21 - "EventType"
Cohesion: 0.14
Nodes (12): EventType, Event, RegularSchedule, Task, Calendar, Agenda, Calendar, Day (+4 more)

### Community 22 - "Task Date Filters"
Cohesion: 0.22
Nodes (8): DateRange, filteredTasks(), FilterKey, All, Later, Today, Tomorrow, Task

### Community 23 - "ReminderOption"
Cohesion: 0.33
Nodes (6): ReminderOption, AtDeadline, None, OneDayBefore, OneHourBefore, TenMinutesBefore

### Community 24 - "Tasks"
Cohesion: 0.40
Nodes (5): Aksi dari radial di tab ini, Filter, Isi layar, Kartu tugas, Tasks

### Community 25 - "Add Card Options"
Cohesion: 0.40
Nodes (5): AddOption, Category, Priority, Reminder, Schedule

### Community 28 - "Priority"
Cohesion: 0.50
Nodes (4): Priority, High, Low, Medium

### Community 29 - "TasksScreen.kt"
Cohesion: 0.09
Nodes (3): AppMetrics, menuColor(), Token tampilan

## Ambiguous Edges - Review These
- `Legacy model package` → `TimeBlock`  [AMBIGUOUS]
  CONTEXT.md · relation: conceptually_related_to
- `menuColor` → `Product color roles`  [AMBIGUOUS]
  CONTEXT.md · relation: conceptually_related_to
- `Pale green grid field` → `Round launcher icon (hdpi)`  [AMBIGUOUS]
  app/src/main/res/mipmap-hdpi/ic_launcher_round.webp · relation: references

## Knowledge Gaps
- **67 isolated node(s):** `InvalidName`, `InvalidEmail`, `ShortPassword`, `EmailTaken`, `WrongCredentials` (+62 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 199 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **11 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `TasksViewModel` connect `Tasks ViewModel` to `Calendar Domain Model`, `Add Task Card`, `App Settings Store`, `Use Case Wiring`, `Category Preferences`, `Task Time Helpers`, `CalendarScreen.kt`, `Sample Schedule Data`, `EventType`, `Task Date Filters`, `Tasks`?**
  _High betweenness centrality (0.092) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `TasksViewModel` (e.g. with `Arsitektur` and `Tasks`) actually correct?**
  _`TasksViewModel` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `InvalidName`, `InvalidEmail`, `ShortPassword` to the rest of the system?**
  _67 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `AddTaskCard.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.0927536231884058 - nodes in this community are weakly interconnected._
- **Why does `appStyle()` connect `Calendar Domain Model` to `AddTaskCard.kt`, `Add Task Card`, `SplashScreen.kt`, `RadialDock.kt`, `CalendarScreen.kt`, `Halaman Emobilize`, `TasksScreen.kt`?**
  _High betweenness centrality (0.069) - this node is a cross-community bridge._

### Low-confidence Hints
_AMBIGUOUS edges — the extractor was unsure. Verify before acting on these._

- **What is the exact relationship between `Legacy model package` and `TimeBlock`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._
- **What is the exact relationship between `menuColor` and `Product color roles`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._