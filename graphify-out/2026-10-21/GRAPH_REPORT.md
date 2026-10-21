# Graph Report - Emobilize  (2026-10-21)

## Corpus Check
- 16 files · ~23,288 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 721 nodes · 1907 edges · 33 communities (20 shown, 13 thin omitted)
- Extraction: 95% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 85 edges (avg confidence: 0.9)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Compose Screen Layer
- Calendar Domain Model
- App UI Architecture
- Add Task Card
- Preferences Auth Store
- Launcher Icon Art
- Tasks ViewModel
- In-Memory Task Store
- Schedule Repository
- App Settings Store
- Use Case Wiring
- Category Preferences
- Theme And Palette
- Task Time Helpers
- CI And Product Docs
- Radial Menu Drawing
- Sample Schedule Data
- Main Activity Entry
- Instrumented Test
- Draft Task Mapping
- Task Date Filters
- Form Validation
- Bottom Dock Tabs
- Add Card Options
- Unit Test Example
- Schedule Mode
- Layout Metrics

## God Nodes (most connected - your core abstractions)
1. `TasksViewModel` - 55 edges
2. `appStyle()` - 53 edges
3. `TimeBlock` - 25 edges
4. `FabAction` - 25 edges
5. `AppContainer` - 25 edges
6. `TaskDraft` - 20 edges
7. `AddTaskCard()` - 19 edges
8. `AppSettings` - 18 edges
9. `TaskRepository` - 17 edges
10. `Category` - 17 edges

## Surprising Connections (you probably didn't know these)
- `Model` --references--> `TimeBlock`  [INFERRED]
  CONTEXT.md → app/src/main/java/ayodong/emobilize/domain/model/Calendar.kt
- `Calendar` --references--> `EventType`  [INFERRED]
  CONTEXT.md → app/src/main/java/ayodong/emobilize/domain/model/Task.kt
- `Yang tidak boleh` --references--> `TaskDraft`  [INFERRED]
  CONTEXT.md → app/src/main/java/ayodong/emobilize/ui/tasks/TasksViewModel.kt
- `Mau ubah apa` --references--> `AppMetrics`  [INFERRED]
  CONTEXT.md → app/src/main/java/ayodong/emobilize/ui/theme/Metrics.kt
- `Token tampilan` --references--> `AppMetrics`  [INFERRED]
  CONTEXT.md → app/src/main/java/ayodong/emobilize/ui/theme/Metrics.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **CI check pipeline** — github_workflows_ci_check, github_workflows_ci_resolvecidependencies, github_workflows_ci_testdebugunittest, github_workflows_ci_assembledebug [EXTRACTED 1.00]
- **UI ViewModel use case repository data flow** — context_emobilizeapp, context_tasksviewmodel, context_taskusecases, context_scheduleusecases, context_repositories, context_inmemorytaskrepository, context_inmemoryschedulerepository [INFERRED 0.85]
- **Product colors alias chart and destructive tokens** — design_product_colors, design_chart_tokens, design_color_tokens, design_neutral_light, design_neutral_dark [EXTRACTED 1.00]
- **Launcher Icon Visual Composition** — app_src_main_res_mipmap_hdpi_ic_launcher_icon, app_src_main_res_mipmap_hdpi_ic_launcher_android_robot, app_src_main_res_mipmap_hdpi_ic_launcher_green_grid, app_src_main_res_mipmap_hdpi_ic_launcher_rounded_square, app_src_main_res_mipmap_hdpi_ic_launcher_drop_shadow [EXTRACTED 1.00]
- **Composed round Android launcher mark** — app_src_main_res_mipmap_hdpi_ic_launcher_round, app_src_main_res_mipmap_xhdpi_ic_launcher_android_robot, app_src_main_res_mipmap_hdpi_ic_launcher_round_green_disk [EXTRACTED 1.00]
- **Launcher Icon Composition** — app_src_main_res_mipmap_mdpi_ic_launcher_launcher_icon, app_src_main_res_mipmap_mdpi_ic_launcher_android_robot, app_src_main_res_mipmap_mdpi_ic_launcher_green_squircle [EXTRACTED 1.00]
- **Round Android launcher glyph** — app_src_main_res_mipmap_mdpi_ic_launcher_round_icon, app_src_main_res_mipmap_hdpi_ic_launcher_android_robot, app_src_main_res_mipmap_mdpi_ic_launcher_round_green_disc, app_src_main_res_mipmap_mdpi_ic_launcher_round_round_crop [EXTRACTED 1.00]
- **Launcher Icon Composition** — app_src_main_res_mipmap_xhdpi_ic_launcher, app_src_main_res_mipmap_xhdpi_ic_launcher_android_robot, app_src_main_res_mipmap_xhdpi_ic_launcher_green_grid, app_src_main_res_mipmap_xhdpi_ic_launcher_rounded_square, app_src_main_res_mipmap_hdpi_ic_launcher_drop_shadow [EXTRACTED 1.00]
- **Round launcher icon composition** — app_src_main_res_mipmap_xhdpi_ic_launcher_android_robot, app_src_main_res_mipmap_mdpi_ic_launcher_round_green_disc, app_src_main_res_mipmap_xhdpi_ic_launcher_round_grid_pattern [EXTRACTED 1.00]
- **Launcher Icon Composition** — app_src_main_res_mipmap_xxhdpi_ic_launcher_android_robot, app_src_main_res_mipmap_xhdpi_ic_launcher_green_grid, app_src_main_res_mipmap_xxhdpi_ic_launcher_material_long_shadow, app_src_main_res_mipmap_xhdpi_ic_launcher_rounded_square [EXTRACTED 1.00]
- **Round launcher glyph composition** — app_src_main_res_mipmap_xxhdpi_ic_launcher_round_android_robot, app_src_main_res_mipmap_xxhdpi_ic_launcher_round_green_circular_field, app_src_main_res_mipmap_xxhdpi_ic_launcher_round_white_grid_overlay, app_src_main_res_mipmap_xxhdpi_ic_launcher_round_long_shadow [EXTRACTED 1.00]
- **Launcher Icon Visual Composition** — app_src_main_res_mipmap_xxxhdpi_ic_launcher_icon, app_src_main_res_mipmap_xxxhdpi_ic_launcher_android_robot, app_src_main_res_mipmap_hdpi_ic_launcher_green_grid, app_src_main_res_mipmap_hdpi_ic_launcher_rounded_square, app_src_main_res_mipmap_xxxhdpi_ic_launcher_long_shadow [EXTRACTED 1.00]
- **Round Android launcher icon composition** — app_src_main_res_mipmap_xxxhdpi_ic_launcher_android_robot, app_src_main_res_mipmap_xxxhdpi_ic_launcher_round_green_grid, app_src_main_res_mipmap_xxhdpi_ic_launcher_round_circular_mask, app_src_main_res_mipmap_hdpi_ic_launcher_drop_shadow [EXTRACTED 1.00]

## Communities (33 total, 13 thin omitted)

### Community 0 - "Compose Screen Layer"
Cohesion: 0.06
Nodes (9): formatInputTime(), ExpandableReminderCard(), SplashScreen(), at(), categoryColor(), inkOn(), menuColor(), priorityColor() (+1 more)

### Community 1 - "Calendar Domain Model"
Cohesion: 0.07
Nodes (46): CalView, Day, Month, Week, taskDate(), taskOccursOn(), FabAction, Add (+38 more)

### Community 2 - "App UI Architecture"
Cohesion: 0.05
Nodes (57): AddTaskCard, AppContainer, AppMetrics, appStyle, BottomDock, CalendarFormSheet, CalendarScreen, EmobilizeApp (+49 more)

### Community 3 - "Add Task Card"
Cohesion: 0.09
Nodes (33): AddTaskCard(), CategoryMark(), CategoryOptions(), OptionButton(), PriorityMark(), PriorityOptions(), scheduleChosen(), ScheduleOptions() (+25 more)

### Community 4 - "Preferences Auth Store"
Cohesion: 0.08
Nodes (19): PreferencesAuthRepository, StoredAccount, Account, AuthError, EmailTaken, InvalidEmail, InvalidName, ShortPassword (+11 more)

### Community 5 - "Launcher Icon Art"
Cohesion: 0.06
Nodes (41): White Android Robot Mascot, Bottom-Right Drop Shadow, Android Green Grid Background, HDPI Android Launcher Icon, Android App Launcher Icon, Round launcher icon (hdpi), Android platform, Green circular field (+33 more)

### Community 6 - "Tasks ViewModel"
Cohesion: 0.11
Nodes (3): inputToHours(), TasksViewModel, Calendar

### Community 9 - "In-Memory Task Store"
Cohesion: 0.11
Nodes (8): InMemoryTaskRepository, TaskStatus, Done, Progress, Todo, TaskBoard, TaskRepository, StatusHeader()

### Community 10 - "Schedule Repository"
Cohesion: 0.13
Nodes (8): InMemoryScheduleRepository, formatBlockTime(), hoursToInput(), monthCells(), placeSchedule(), replaceBlock(), TimeBlock, ScheduleRepository

### Community 11 - "App Settings Store"
Cohesion: 0.14
Nodes (11): PreferencesAppSettingsRepository, AppSettings, ThemeMode, Dark, Light, System, WeekStart, Monday (+3 more)

### Community 12 - "Use Case Wiring"
Cohesion: 0.20
Nodes (12): AppContainer, AuthViewModelFactory, TasksViewModelFactory, UpdateAppSettingsUseCase, DeleteTimeBlockUseCase, GetScheduleUseCase, PlaceTimeBlockUseCase, AddTaskUseCase (+4 more)

### Community 13 - "Category Preferences"
Cohesion: 0.15
Nodes (5): PreferencesCategoryRepository, Category, CategoryRepository, GetCategoriesUseCase, UpdateCategoryUseCase

### Community 14 - "Theme And Palette"
Cohesion: 0.14
Nodes (6): AppPalette, paletteFrom(), hsl(), ShadcnColors, ShadcnSpace, EmobilizeTheme()

### Community 15 - "Task Time Helpers"
Cohesion: 0.19
Nodes (12): DateRange, deadlineRank(), deadlineToInputDate(), displayTimeToInput(), EventType, Event, RegularSchedule, Task (+4 more)

### Community 16 - "CI And Product Docs"
Cohesion: 0.14
Nodes (15): Android Launcher Artwork, White Android Robot, Green Squircle Background, Launcher Icon, compileDebugKotlin, Emobilize, Android SDK, :app:assembleDebug (+7 more)

### Community 17 - "Radial Menu Drawing"
Cohesion: 0.18
Nodes (8): displayAngle(), NavTab, Calendar, Tasks, FabFace(), actionIcon(), RadialMenu(), sectorPath()

### Community 18 - "Sample Schedule Data"
Cohesion: 0.27
Nodes (8): sampleBlocks(), sampleTasks(), weekOf(), isoDate(), Priority, High, Low, Medium

### Community 22 - "Task Date Filters"
Cohesion: 0.33
Nodes (5): FilterKey, All, Later, Today, Tomorrow

### Community 25 - "Add Card Options"
Cohesion: 0.50
Nodes (4): AddOption, Category, Priority, Schedule

### Community 28 - "Schedule Mode"
Cohesion: 0.67
Nodes (3): ScheduleMode, Deadline, Range

## Ambiguous Edges - Review These
- `TimeBlock` → `Legacy model package`  [AMBIGUOUS]
  CONTEXT.md · relation: conceptually_related_to
- `menuColor` → `Product color roles`  [AMBIGUOUS]
  CONTEXT.md · relation: conceptually_related_to
- `Round launcher icon (hdpi)` → `Pale green grid field`  [AMBIGUOUS]
  app/src/main/res/mipmap-hdpi/ic_launcher_round.webp · relation: references

## Knowledge Gaps
- **61 isolated node(s):** `Deadline`, `Range`, `Dark`, `Light`, `System` (+56 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 179 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **13 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `TasksViewModel` connect `Tasks ViewModel` to `Compose Screen Layer`, `Calendar Domain Model`, `Add Task Card`, `Preferences Auth Store`, `Schedule Repository`, `App Settings Store`, `Use Case Wiring`, `Category Preferences`, `Task Time Helpers`, `Draft Task Mapping`, `Task Date Filters`, `Form Validation`?**
  _High betweenness centrality (0.090) - this node is a cross-community bridge._
- **What connects `Deadline`, `Range`, `Dark` to the rest of the system?**
  _61 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Compose Screen Layer` be split into smaller, more focused modules?**
  _Cohesion score 0.0567741935483871 - nodes in this community are weakly interconnected._
- **Why does `appStyle()` connect `Calendar Domain Model` to `Compose Screen Layer`, `Add Task Card`, `Dock Geometry`, `Radial Menu Interaction`, `In-Memory Task Store`, `Radial Menu Drawing`, `Bottom Dock Tabs`?**
  _High betweenness centrality (0.062) - this node is a cross-community bridge._
- **Should `Calendar Domain Model` be split into smaller, more focused modules?**
  _Cohesion score 0.06760316066725197 - nodes in this community are weakly interconnected._

### Low-confidence Hints
_AMBIGUOUS edges — the extractor was unsure. Verify before acting on these._

- **What is the exact relationship between `TimeBlock` and `Legacy model package`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._
- **What is the exact relationship between `menuColor` and `Product color roles`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._