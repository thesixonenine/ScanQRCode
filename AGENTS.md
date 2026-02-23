# AGENTS.md

Guidelines for AI coding agents working in this Android Kotlin project.

## Project Overview

- **Type**: Android application (Kotlin + Jetpack Compose)
- **Namespace**: `io.github.thesixonenine.scanqrcode`
- **Build System**: Gradle with Kotlin DSL, version catalog
- **Min SDK**: 31 (Android 12) | **Target SDK**: 36 | **Java**: 21
- **UI Framework**: Jetpack Compose with Material 3
- **Kotlin Style**: Official (per `gradle.properties`)

## Build Commands

```bash
./gradlew build              # Build the project
./gradlew assembleDebug      # Build debug APK
./gradlew assembleRelease    # Build release APK
./gradlew clean              # Clean build
```

## Test Commands

```bash
# Run all unit tests
./gradlew test

# Run unit tests for debug variant
./gradlew testDebugUnitTest

# Run a single test class
./gradlew test --tests "io.github.thesixonenine.scanqrcode.ExampleUnitTest"

# Run a single test method
./gradlew test --tests "io.github.thesixonenine.scanqrcode.ExampleUnitTest.addition_isCorrect"

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run a single instrumented test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=io.github.thesixonenine.scanqrcode.ExampleInstrumentedTest

# Test report: app/build/reports/tests/testDebugUnitTest/index.html
```

## Lint Commands

```bash
./gradlew lint    # Report: app/build/reports/lint-results-debug.html
```

## Code Style Guidelines

### Kotlin Style

- Follow official Kotlin conventions: https://kotlinlang.org/docs/coding-conventions.html
- Indentation: 4 spaces (no tabs)
- Maximum line length: 100 characters
- Use trailing commas in multiline collections and parameter lists

### Imports

- Organize alphabetically within groups
- Group order: Android/AndroidX → Compose → Third-party → Java/Kotlin stdlib → Project
- Avoid wildcard imports (except in test files)
- Remove unused imports

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Packages | lowercase dot-separated | `io.github.thesixonenine.scanqrcode` |
| Classes | PascalCase | `MainActivity`, `ScanResult` |
| Functions | camelCase | `processQrCode`, `calculateResult` |
| Properties | camelCase | `qrContent`, `hasCameraPermission` |
| Constants | SCREAMING_SNAKE_CASE | `MAX_RETRY_COUNT`, `TAG` |
| Composable functions | PascalCase | `MainScreen`, `DisplayScreen` |

### File Organization

```kotlin
package io.github.thesixonenine.scanqrcode.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import io.github.thesixonenine.scanqrcode.ui.theme.ScanQRCodeTheme

class MainActivity : ComponentActivity() {
    // Companion object (constants)
    // Properties
    // Lifecycle methods (onCreate, etc.)
    // Public methods
    // Private methods
}
```

### Types and Nullability

- Prefer `val` over `var` for immutability
- Use nullable types explicitly with `?` (e.g., `String?`)
- Use safe calls `?.` and elvis operator `?:` for null handling
- Avoid `!!` operator; prefer safe alternatives
- Use `remember { mutableStateOf() }` for Compose state

### Compose Conventions

- Use `ComponentActivity` with `setContent` (not `AppCompatActivity`)
- Wrap content in `ScanQRCodeTheme { }` for theming
- Use Material 3 components (`MaterialTheme`, `Scaffold`, `Button`, etc.)
- State hoisting: pass state down, events up
- Use `LaunchedEffect` for side effects

### Error Handling

- Use `Result<T>` or sealed classes for operation outcomes
- Log errors with Android's `Log` class using appropriate levels
- Handle all caught exceptions; never silently swallow

### Logging

```kotlin
import android.util.Log

private const val TAG = "MainActivity"

Log.d(TAG, "Debug message")
Log.e(TAG, "Error message", exception)
```

### Resource Files

- Layouts (if needed): `activity_`, `fragment_`, `dialog_`, `item_`
- Strings: `action_`, `label_`, `message_`, `error_`
- Dimensions: `margin_`, `padding_`, `text_size_`
- Colors: Use semantic names (`colorPrimary`, not `blue500`)

### Dependencies

- Define versions in `gradle/libs.versions.toml` under `[versions]`
- Add library reference under `[libraries]`
- Reference in `app/build.gradle.kts` as `libs.xxx`
- Use `implementation` / `testImplementation` / `androidTestImplementation`
- Use Compose BOM for Compose library versions

### Testing Conventions

- Unit tests: `app/src/test/java/`
- Instrumented tests: `app/src/androidTest/java/`
- Test class naming: `<ClassName>Test`
- Use JUnit 4; Espresso for UI tests

## Project Structure

```
app/src/
├── main/java/io/github/thesixonenine/scanqrcode/
│   ├── ui/                    # Activities, Composables, ViewModels
│   │   └── theme/             # Color, Theme definitions
│   ├── data/                  # Repositories, models
│   └── util/                  # Utility classes
├── test/java/                 # Unit tests
└── androidTest/java/          # Instrumented tests
```

## Common Tasks

### Adding a Dependency

1. Add version to `gradle/libs.versions.toml` under `[versions]`
2. Add library under `[libraries]`
3. Add to `app/build.gradle.kts` dependencies

### Adding a New Screen

1. Create Activity extending `ComponentActivity`
2. Use `setContent { ScanQRCodeTheme { } }` wrapper
3. Add to `AndroidManifest.xml`
4. Create Composable functions for UI

## Notes

- QR code scanning app using ZXing library (`zxing-android-embedded`)
- Dark mode supported via `isSystemInDarkTheme()` and Dynamic Colors
- Camera permission required at runtime
