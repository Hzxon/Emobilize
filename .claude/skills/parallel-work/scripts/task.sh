#!/usr/bin/env bash
# Task worktree helper for parallel work on this repo.
#
#   task.sh status            where am I, am I isolated, which other tasks exist
#   task.sh new <slug> [base] create ../<repo>.wt/<slug> on branch task/<slug> from base (default: main)
#   task.sh claim             push the current task branch so teammates see it is taken
#   task.sh check             list files this branch changed that another task/* branch also changed
#   task.sh done <slug>       remove the worktree after its branch is merged
#
# Every command is safe to rerun. Nothing here deletes a branch.

set -euo pipefail

MAIN_BRANCH="${MAIN_BRANCH:-main}"

die() { printf 'task.sh: %s\n' "$*" >&2; exit 1; }

repo_name() { basename "$(main_root)"; }

# The checkout that owns .git, even when run from inside a worktree.
main_root() {
  local common
  common="$(git rev-parse --git-common-dir)"
  common="$(cd "$common" && pwd)"
  dirname "$common"
}

wt_dir() { printf '%s/%s.wt/%s' "$(dirname "$(main_root)")" "$(repo_name)" "$1"; }

in_main_checkout() {
  [ "$(git rev-parse --show-toplevel)" = "$(main_root)" ]
}

base_ref() {
  if git show-ref --verify --quiet "refs/remotes/origin/$MAIN_BRANCH"; then
    printf 'origin/%s' "$MAIN_BRANCH"
  else
    printf '%s' "$MAIN_BRANCH"
  fi
}

changed_files() {
  # Files a branch changed since it left main.
  local ref="$1" base
  base="$(git merge-base "$(base_ref)" "$ref")" || return 0
  git diff --name-only "$base" "$ref"
}

cmd_status() {
  local top branch
  top="$(git rev-parse --show-toplevel)"
  branch="$(git rev-parse --abbrev-ref HEAD)"
  printf 'folder:  %s\nbranch:  %s\n' "$top" "$branch"
  if in_main_checkout; then
    printf 'role:    main checkout (merge here, do not edit here)\n'
  else
    printf 'role:    task worktree (isolated)\n'
  fi
  printf '\nworktrees:\n'
  git worktree list
  printf '\ntask branches:\n'
  git for-each-ref --format='  %(refname:short)' 'refs/heads/task/*' 'refs/remotes/origin/task/*' | sort -u
  local dirty
  dirty="$(git status --porcelain | wc -l | tr -d ' ')"
  printf '\nuncommitted files here: %s\n' "$dirty"
  if in_main_checkout && [ "$dirty" != "0" ]; then
    printf 'warning: edits in the main checkout are not isolated. Move them to a task worktree.\n'
  fi
}

cmd_new() {
  local slug="${1:-}" base="${2:-$MAIN_BRANCH}" dir branch
  [ -n "$slug" ] || die "usage: task.sh new <slug> [base]"
  [[ "$slug" =~ ^[a-z0-9][a-z0-9-]*$ ]] || die "slug must be lowercase letters, digits, hyphens"
  branch="task/$slug"
  dir="$(wt_dir "$slug")"
  if git remote get-url origin >/dev/null 2>&1; then
    git fetch --quiet origin || printf 'task.sh: fetch failed, using local refs\n' >&2
  fi
  if git show-ref --verify --quiet "refs/remotes/origin/$branch"; then
    die "origin/$branch already exists: someone claimed this task. Pick another slug or talk to them."
  fi
  if [ -d "$dir" ]; then
    printf 'worktree already exists: %s\n' "$dir"
  else
    mkdir -p "$(dirname "$dir")"
    if git show-ref --verify --quiet "refs/heads/$branch"; then
      git worktree add "$dir" "$branch"
    else
      git worktree add -b "$branch" "$dir" "$base"
    fi
    [ -f "$(main_root)/local.properties" ] && cp "$(main_root)/local.properties" "$dir/"
  fi
  printf '\nready: %s on %s\nnext:  cd %s\n' "$dir" "$branch" "$dir"
}

cmd_claim() {
  local branch
  branch="$(git rev-parse --abbrev-ref HEAD)"
  [[ "$branch" == task/* ]] || die "not on a task/* branch ($branch)"
  git remote get-url origin >/dev/null 2>&1 || die "no origin remote, nothing to claim against"
  git push --quiet -u origin "$branch"
  printf 'claimed: origin/%s\n' "$branch"
}

cmd_check() {
  local branch mine others overlap=0 ref theirs common
  branch="$(git rev-parse --abbrev-ref HEAD)"
  [[ "$branch" == task/* ]] || die "not on a task/* branch ($branch)"
  if git remote get-url origin >/dev/null 2>&1; then
    git fetch --quiet origin || true
  fi
  mine="$( { changed_files HEAD; git status --porcelain | awk '{print $2}'; } | sort -u)"
  [ -n "$mine" ] || { printf 'no changes on %s yet\n' "$branch"; return 0; }
  others="$(git for-each-ref --format='%(refname:short)' 'refs/heads/task/*' 'refs/remotes/origin/task/*' \
    | grep -v -x -e "$branch" -e "origin/$branch" | sort -u)"
  for ref in $others; do
    theirs="$(changed_files "$ref" | sort -u)"
    common="$(comm -12 <(printf '%s\n' "$mine") <(printf '%s\n' "$theirs"))"
    if [ -n "$common" ]; then
      overlap=1
      printf 'overlap with %s:\n' "$ref"
      printf '%s\n' "$common" | sed 's/^/  /'
    fi
  done
  if [ "$overlap" = "0" ]; then
    printf 'no overlap with other task branches\n'
  else
    printf '\nThese files are also changed on another task. Talk before you merge; the second PR takes the conflict.\n'
    return 1
  fi
}

cmd_done() {
  local slug="${1:-}" dir
  [ -n "$slug" ] || die "usage: task.sh done <slug>"
  dir="$(wt_dir "$slug")"
  [ -d "$dir" ] || die "no worktree at $dir"
  if [ -n "$(git -C "$dir" status --porcelain)" ]; then
    die "$dir has uncommitted changes. Commit or discard them first."
  fi
  git worktree remove "$dir"
  printf 'removed %s (branch task/%s kept)\n' "$dir" "$slug"
}

case "${1:-}" in
  status) cmd_status ;;
  new)    shift; cmd_new "$@" ;;
  claim)  cmd_claim ;;
  check)  cmd_check ;;
  done)   shift; cmd_done "$@" ;;
  *)      sed -n '2,10p' "$0"; exit 1 ;;
esac
