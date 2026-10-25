#!/usr/bin/env bash
# Checks that the committed build setup is the same for every teammate.
# Run it from the repo root before you push: bash scripts/check-config.sh
# CI runs the same script, so a failure here also fails the pull request.

set -u
cd "$(git rev-parse --show-toplevel)" || exit 1

failures=0
pass() { printf 'PASS  %s\n' "$1"; }
fail() { printf 'FAIL  %s\n' "$1"; failures=$((failures + 1)); }
hint() { printf '      %s\n' "$@"; }

# 1. Files that only make sense on one laptop must not be committed.
local_only=$(git ls-files | grep -E \
  '(^|/)local\.properties$|\.iml$|(^|/)\.gradle/|(^|/)build/|^\.idea/(workspace|misc|deploymentTargetSelector|deviceManager)\.xml$|^\.idea/(caches|libraries)/' || true)
if [ -z "$local_only" ]; then
  pass "No per-machine files are committed"
else
  fail "Per-machine files are committed:"
  hint $local_only "Remove them with: git rm --cached <file>"
fi

# 2. Build files must not point at a path or JDK on one laptop.
build_files="gradle.properties settings.gradle.kts build.gradle.kts app/build.gradle.kts gradle/wrapper/gradle-wrapper.properties"
machine_paths=$(grep -n -E \
  'org\.gradle\.java\.home|sdk\.dir|ndk\.dir|/Users/|/home/|(^|[^A-Za-z])[A-Za-z]\\?:[\\/]' $build_files || true)
if [ -z "$machine_paths" ]; then
  pass "Build files have no machine-specific paths"
else
  fail "Build files contain machine-specific settings:"
  hint "$machine_paths" "Put SDK and JDK paths in local.properties or your user gradle.properties instead."
fi

# 3. Everyone must download the same Gradle, checked by its SHA-256.
wrapper=gradle/wrapper/gradle-wrapper.properties
if grep -q -E '^distributionUrl=https\\://services\.gradle\.org/distributions/gradle-[0-9.]+-bin\.zip$' "$wrapper" \
  && grep -q -E '^distributionSha256Sum=[0-9a-f]{64}$' "$wrapper"; then
  pass "Gradle wrapper is pinned: $(sed -n 's/^distributionUrl=.*gradle-\(.*\)-bin\.zip$/\1/p' "$wrapper")"
else
  fail "$wrapper must set an official -bin.zip distributionUrl and a distributionSha256Sum"
  hint "Regenerate it with: ./gradlew wrapper --gradle-version <version> --gradle-distribution-sha256-sum <sum>"
fi

# 4. Gradle must run on the same JDK everywhere (downloaded if missing).
daemon_jvm=gradle/gradle-daemon-jvm.properties
if [ -f "$daemon_jvm" ] && grep -q -E '^toolchainVersion=[0-9]+$' "$daemon_jvm"; then
  pass "Gradle daemon JDK is pinned: $(sed -n 's/^toolchainVersion=//p' "$daemon_jvm")"
else
  fail "$daemon_jvm must set toolchainVersion"
  hint "Regenerate it with: ./gradlew updateDaemonJvm --jvm-version <version>"
fi

# 5. Library and plugin versions live only in gradle/libs.versions.toml.
hardcoded=$(grep -n -E \
  '"[A-Za-z0-9_.-]+:[A-Za-z0-9_.-]+:[0-9][^"]*"|id\("[^"]+"\) version' build.gradle.kts app/build.gradle.kts || true)
if [ -z "$hardcoded" ]; then
  pass "Dependency and plugin versions come from libs.versions.toml"
else
  fail "Hard-coded versions found in build files:"
  hint "$hardcoded" "Add the version to gradle/libs.versions.toml and use libs.<alias>."
fi

# 6. gradlew must stay executable with LF endings; gradlew.bat with CRLF.
gradlew_mode=$(git ls-files -s gradlew | cut -c1-6)
gradlew_eol=$(git check-attr eol gradlew | sed 's/.*: //')
bat_eol=$(git check-attr eol gradlew.bat | sed 's/.*: //')
if [ "$gradlew_mode" = "100755" ] && [ "$gradlew_eol" = "lf" ] && [ "$bat_eol" = "crlf" ] \
  && ! grep -q $'\r' gradlew; then
  pass "gradlew is executable with LF endings, gradlew.bat uses CRLF"
else
  fail "Wrapper scripts have the wrong mode or line endings (mode $gradlew_mode, gradlew $gradlew_eol, gradlew.bat $bat_eol)"
  hint "Fix with: git update-index --chmod=+x gradlew && git add --renormalize ."
fi

echo
if [ "$failures" -eq 0 ]; then
  echo "All configuration checks passed."
else
  echo "$failures configuration check(s) failed."
  exit 1
fi
