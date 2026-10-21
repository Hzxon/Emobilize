# Graph Report - Emobilize  (2026-10-17)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 569 nodes · 1624 edges · 31 communities (17 shown, 14 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 13 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `42c9a9cb`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- CalendarScreen.kt
- appStyle
- FabAction
- AuthOutcome
- EmobilizeApp
- TasksViewModel
- Task.kt
- TasksViewModel.kt
- AppSettings
- TaskStatus
- TaskRepository
- RadialMenu
- UpdateCategoryUseCase
- TasksScreen
- .draftToTask
- Category
- SampleData.kt
- ExampleInstrumentedTest.kt
- TasksViewModelFactory
- EventType
- Priority
- AddOption
- ExampleUnitTest.kt
- Offset
- AppMetrics

## God Nodes (most connected - your core abstractions)
1. `TasksViewModel` - 54 edges
2. `appStyle()` - 54 edges
3. `FabAction` - 25 edges
4. `TimeBlock` - 24 edges
5. `AppContainer` - 24 edges
6. `TaskDraft` - 18 edges
7. `AppSettings` - 18 edges
8. `TaskRepository` - 17 edges
9. `Category` - 17 edges
10. `DayAgenda()` - 16 edges

## Surprising Connections (you probably didn't know these)
- `InMemoryTaskRepository` --calls--> `sampleTasks()`  [INFERRED]
  app/src/main/java/ayodong/emobilize/data/repository/InMemoryTaskRepository.kt → app/src/main/java/ayodong/emobilize/data/repository/SampleData.kt
- `InMemoryScheduleRepository` --calls--> `sampleBlocks()`  [INFERRED]
  app/src/main/java/ayodong/emobilize/data/repository/InMemoryScheduleRepository.kt → app/src/main/java/ayodong/emobilize/data/repository/SampleData.kt
- `taskDate()` --calls--> `deadlineToInputDate()`  [INFERRED]
  app/src/main/java/ayodong/emobilize/domain/model/Calendar.kt → app/src/main/java/ayodong/emobilize/domain/model/Task.kt
- `AppContainer` --calls--> `PreferencesAppSettingsRepository`  [EXTRACTED]
  app/src/main/java/ayodong/emobilize/di/AppContainer.kt → app/src/main/java/ayodong/emobilize/data/repository/PreferencesAppSettingsRepository.kt
- `AppSettings` --references--> `CalView`  [EXTRACTED]
  app/src/main/java/ayodong/emobilize/domain/model/AppSettings.kt → app/src/main/java/ayodong/emobilize/domain/model/Calendar.kt

## Import Cycles
- None detected.

## Communities (31 total, 14 thin omitted)

### Community 0 - "CalendarScreen.kt"
Cohesion: 0.07
Nodes (6): at(), categoryColor(), inkOn(), menuColor(), priorityColor(), ShadcnRadius

### Community 1 - "appStyle"
Cohesion: 0.08
Nodes (32): AuthField(), AuthScreen(), SwitchLine(), CategoryEditor(), ChoiceRow(), OptionChip(), SectionLabel(), SettingHeading() (+24 more)

### Community 2 - "FabAction"
Cohesion: 0.08
Nodes (32): sampleBlocks(), CalView, Day, Month, Week, formatBlockTime(), inputToHours(), monthCells() (+24 more)

### Community 3 - "AuthOutcome"
Cohesion: 0.08
Nodes (19): PreferencesAuthRepository, StoredAccount, Account, AuthError, EmailTaken, InvalidEmail, InvalidName, ShortPassword (+11 more)

### Community 4 - "EmobilizeApp"
Cohesion: 0.09
Nodes (9): MainActivity, EmobilizeApp(), SplashScreen(), AppPalette, paletteFrom(), hsl(), ShadcnColors, ShadcnSpace (+1 more)

### Community 8 - "Task.kt"
Cohesion: 0.13
Nodes (16): DateRange, deadlineRank(), deadlineToInputDate(), displayTimeToInput(), filteredTasks(), FilterKey, All, Later (+8 more)

### Community 9 - "TasksViewModel.kt"
Cohesion: 0.19
Nodes (10): InMemoryScheduleRepository, AppContainer, ScheduleRepository, UpdateAppSettingsUseCase, DeleteTimeBlockUseCase, GetScheduleUseCase, PlaceTimeBlockUseCase, DeleteTaskUseCase (+2 more)

### Community 10 - "AppSettings"
Cohesion: 0.15
Nodes (11): PreferencesAppSettingsRepository, AppSettings, ThemeMode, Dark, Light, System, WeekStart, Monday (+3 more)

### Community 11 - "TaskStatus"
Cohesion: 0.15
Nodes (6): InMemoryTaskRepository, TaskStatus, Done, Progress, Todo, StatusHeader()

### Community 12 - "TaskRepository"
Cohesion: 0.20
Nodes (4): TaskBoard, TaskRepository, AddTaskUseCase, UpdateTaskUseCase

### Community 13 - "RadialMenu"
Cohesion: 0.19
Nodes (9): displayAngle(), NavTab, Calendar, Tasks, BottomDock(), DockTab(), FabFace(), actionIcon() (+1 more)

### Community 14 - "UpdateCategoryUseCase"
Cohesion: 0.23
Nodes (3): CategoryRepository, GetCategoriesUseCase, UpdateCategoryUseCase

### Community 15 - "TasksScreen"
Cohesion: 0.38
Nodes (7): ClockIcon(), RoleBadge(), SelectionBanner(), StatusChoice(), TaskCard(), TaskMeta(), TasksScreen()

### Community 18 - "SampleData.kt"
Cohesion: 0.31
Nodes (6): sampleTasks(), hoursToInput(), formatInputDate(), formatInputTime(), isoDate(), taskWhenLabel()

### Community 21 - "EventType"
Cohesion: 0.50
Nodes (4): EventType, Event, RegularSchedule, Task

### Community 22 - "Priority"
Cohesion: 0.50
Nodes (4): Priority, High, Low, Medium

### Community 23 - "AddOption"
Cohesion: 0.50
Nodes (4): AddOption, Category, Priority, Schedule

## Knowledge Gaps
- **39 isolated node(s):** `Dark`, `Light`, `System`, `Monday`, `Sunday` (+34 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 136 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **14 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `TasksViewModel` connect `TasksViewModel` to `CalendarScreen.kt`, `appStyle`, `EmobilizeApp`, `Task.kt`, `TasksViewModel.kt`, `AppSettings`, `TaskRepository`, `UpdateCategoryUseCase`, `TasksScreen`, `.draftToTask`, `SampleData.kt`, `TasksViewModelFactory`?**
  _High betweenness centrality (0.136) - this node is a cross-community bridge._
- **What connects `Dark`, `Light`, `System` to the rest of the system?**
  _39 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `CalendarScreen.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.06923076923076923 - nodes in this community are weakly interconnected._
- **Why does `appStyle()` connect `appStyle` to `CalendarScreen.kt`, `FabAction`, `EmobilizeApp`, `RadialDock.kt`, `RadialMenu.kt`, `TaskStatus`, `RadialMenu`, `TasksScreen`?**
  _High betweenness centrality (0.099) - this node is a cross-community bridge._
- **Should `appStyle` be split into smaller, more focused modules?**
  _Cohesion score 0.08333333333333333 - nodes in this community are weakly interconnected._
- **Why does `Category` connect `Category` to `CalendarScreen.kt`, `Task.kt`, `TasksViewModel.kt`, `TaskStatus`, `TaskRepository`, `UpdateCategoryUseCase`, `SampleData.kt`?**
  _High betweenness centrality (0.052) - this node is a cross-community bridge._
- **Should `FabAction` be split into smaller, more focused modules?**
  _Cohesion score 0.08418367346938775 - nodes in this community are weakly interconnected._