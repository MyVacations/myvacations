# MyVacations

## Project Overview

MyVacations is a Kotlin Multiplatform application that allows users to plan, manage and track vacations and trips.

Supported platforms:
- Android
- iOS

## Tech Stack

- Kotlin Multiplatform
- Compose Multiplatform
- Material 3
- SQLDelight
- Koin
- Kotlin Serialization

## Architecture

- MVVM
- Clean Architecture
- Repository Pattern
- UI State pattern
- Dependency Injection with Koin

## Project Structure

- shared/: Shared business logic and UI
- androidApp/: Android application
- iosApp/: iOS application

## Coding Guidelines

- Follow Kotlin official coding conventions.
- Prefer immutable state.
- Avoid duplicated code.
- Keep composables stateless whenever possible.
- Business logic must not be inside composables.
- Use Material 3 components.
- Keep code readable over clever.
- Use Kotlin idioms.

## Testing

- Create unit tests whenever business logic changes.
- Prefer deterministic tests.
- Do not introduce flaky tests.

## Android

- Follow Android official best practices.
- Prefer Jetpack recommendations.
- Use lifecycle-aware APIs.
- Optimize Compose recomposition.

## SQLDelight

- Database changes must include migrations when required.
- Keep queries simple and maintainable.

## Before making changes

Always:

1. Understand the existing architecture.
2. Reuse existing components when possible.
3. Keep the project consistent.
4. Explain significant architectural changes before applying them.