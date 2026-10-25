---
name: parallel-work
description: How to work on this repo when other Claude sessions or teammates are editing it at the same time, so edits never silently overwrite each other and every change reaches main through its own branch. Use this skill at the start of every task in this repo before the first file edit, and whenever the user or the situation mentions another session, a teammate, a worktree, a branch, a pull request, a merge, a conflict, "who is working on", or files in git status that you did not change. Also use it when a build fails in a file you never touched.
---

# Parallel work

Several writers share this repository at once: more than one Claude session on one laptop, and teammates on their own laptops. Git only protects writers who sit on different branches in different folders. This skill makes sure you are one of those, and tells you what to do when you are not.

## Why a shared folder is worse than a conflict

Two sessions editing one checkout never get a merge conflict, because there is only one branch. They fail in three quieter ways:

- The last write wins. Session A reads `MainShell.kt`, thinks, writes it back. B's lines from a minute ago are gone. Nothing warns.
- Builds lie. Gradle compiles the whole folder, so A's test run includes B's half-written file. A's red or green is about B's code.
- Commits sweep. `git add -A` or `git commit -a` from B takes A's uncommitted files into B's commit under B's message.

A merge conflict is the good outcome: git stops, shows both versions, you choose once. The process below turns every overlap into that.

## Before the first edit

Run the helper from the repo root and read its output:

```bash
.claude/skills/parallel-work/scripts/task.sh status
```

- `role: task worktree (isolated)`: you are fine. Continue.
- `role: main checkout`: you share this folder with everyone. Do not edit here. Create a worktree for the task (next section) and work there. If the app already gave this session a worktree, you will see it in the `worktrees` list and be inside it.
- `uncommitted files here` is not zero and you did not make them: another writer is mid-task in this folder. Do not stage, revert, or fix those files. Report them to the user and stop until you have an isolated folder.

## Start a task

One task, one branch, one folder. Pick a short lowercase slug that names the task, not the person.

```bash
.claude/skills/parallel-work/scripts/task.sh new <slug>
```

This fetches `origin`, refuses if `origin/task/<slug>` already exists (someone claimed it), creates `../Emobilize.wt/<slug>` on branch `task/<slug>` from `main`, and copies `local.properties` so Gradle works. Then move into that folder for the rest of the task. Each worktree has its own `build/` and its own `git status`; the Gradle cache in `~/.gradle` is shared, so dependencies are not downloaded twice.

Push the branch as soon as the first commit exists, so teammates see the claim:

```bash
.claude/skills/parallel-work/scripts/task.sh claim
```

The branch on `origin` is the claim list. It needs no extra file and nobody forgets it, because the branch is needed anyway.

## While working

- Edit only the folder the task names. The scope rules are in [AGENTS.md](../../../AGENTS.md): feature work stays in `feature/<x>/`, and `core/`, Gradle, the manifest, `AndroidManifest.xml`, `strings.xml`, `themes.xml`, `DESIGN.md` and `AGENTS.md` belong to the core owner. If the task needs a change there, stop and report what is needed instead of editing it. A hotspot edit goes in its own small PR from the core owner and merges before the feature.
- A file in `git status` that you did not change is someone else's. Leave it alone and mention it.
- Rebuild in your own worktree, never in the main checkout.
- Keep the task small enough to merge today. Long branches are where conflicts grow.

## Before you commit

1. Check for overlap with every other task branch, local and on `origin`:

   ```bash
   .claude/skills/parallel-work/scripts/task.sh check
   ```

   An overlap on day one is a conversation. Tell the user which files and which branch, and ask whether to continue, split, or wait. An overlap found at merge time is wasted work.

2. Run the gate in your worktree:

   ```bash
   ./gradlew :app:compileDebugKotlin :app:testDebugUnitTest
   ```

3. Stage by path. Never `git add -A`, `git add .`, or `git commit -a`. Those are how another writer's files end up in your commit.

   ```bash
   git add <the files you changed>
   ```

4. Write the commit message with the `git-commit-message` skill when it is available.

## Finish and merge

1. Rebase or merge `main` into the task branch first, then rerun the gate. In this app, `sync_with_base_branch` does the fetch and merge for a worktree session.
2. Push and open a PR to `main`. Merge order: core PRs first, then feature PRs oldest first, one at a time. After each core merge, feature branches sync again.
3. If the merge reports a conflict, you are the second writer on those lines. Open the file, resolve each `<<<<<<<` block by keeping the right combination, rerun the gate, commit, push. Minutes if both PRs are small.
4. After the merge:

   ```bash
   .claude/skills/parallel-work/scripts/task.sh done <slug>
   ```

   The branch stays in history; only the folder goes.

## Teammates on other machines

A teammate's clone is a worktree one level up: a separate folder on a separate disk, with `origin` on GitHub as the shared point instead of the local `.git`. The process is identical. Pull `main` before `task.sh new`. Claim early. Check overlap against `origin/task/*`. Merge through a PR. Protect `main` on GitHub so nothing lands without the gate.

## When two tasks touch the same feature

Find the case before deciding anything:

| Case | What happens | What to do |
| --- | --- | --- |
| Different files | Git merges both | Build after the merge. Git merged text, not meaning. |
| Same file, different lines | Git merges both hunks | Same. |
| Same lines | Conflict markers | Second PR resolves, reruns the gate. |
| Same work done twice | Git cannot help | Keep the branch closer to done, cherry-pick the rest, discard the other. Split what remains as `data/` versus `ui/` with a `Fake<X>Repository`, the way AGENTS.md describes two people on one feature. |

Worktrees make overlap visible. Ownership rules make it rare. Both are needed; neither replaces the other.

## If you are already in a shared folder with changes

This happens when a session started before the worktree habit. Do not try to untangle it in place. Make a worktree, then move only your own files:

```bash
.claude/skills/parallel-work/scripts/task.sh new <slug>
```

```bash
git diff -- <your files> | git -C ../Emobilize.wt/<slug> apply
```

Then `git checkout -- <your files>` in the shared folder, after confirming the patch applied. The other writer's files never move.
