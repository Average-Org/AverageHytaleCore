# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AverageHytaleCore is a Hytale server plugin library that provides utility classes and database abstractions for Hytale plugin developers. It is built with Gradle and compiles to a shadow JAR that bundles dependencies.

## Build and Development Commands

### Build the project
```bash
./gradlew build
```

### Run tests (if added)
```bash
./gradlew test
```

### Run a single test class
```bash
./gradlew test --tests TestClassName
```

### Create a shadow JAR (bundles all dependencies)
```bash
./gradlew shadowJar
```

The shadow JAR output is located in `build/libs/AverageHytaleCore-1.0.2-all.jar`.

### Clean build artifacts
```bash
./gradlew clean
```

## Architecture Overview

### Core Modules

**Database Module** (`models/db/`)
- `DatabaseService.java`: Main abstraction for SQLite database operations using ORMLite. Manages DAOs and connection sources. Plugin developers call `addTable(Class)` to register ORMLite-annotated entity classes, then retrieve DAOs via `getTable(Class)` or `getTypedTable(Class)`.
- `Sqlite4JDatabaseType.java`: Custom ORMLite DatabaseType implementation for sqlite4j driver compatibility. Handles SQLite-specific SQL generation (LIMIT/OFFSET syntax, savepoint limitations).

**Utility Modules** (`util/`)
- `DbUtils.java`: Wrapper around DatabaseService that handles SQLite database file creation and initialization. Calls `PathUtils.getPathForConfig()` to place databases in the mod directory structure.
- `ColorUtils.java`: Parses Minecraft/Hytale color codes (`&` or `§` prefix) and converts them to formatted Hytale Message objects. Supports color codes (0-f), bold (`l`), italic (`o`), reset (`r`), and markdown links in `[text](url)` format. Used throughout the codebase for message formatting.
- `ConfigObjectProvider.java`: Abstract base class for JSON configuration file handling. Subclasses define a POJO config class; this handles serialization/deserialization and file I/O. Uses pretty-printed JSON with a root `config` property.
- `PathUtils.java`: Manages plugin directory pathing. Stores configs and databases in `mods/{pluginName}/` directory. Plugin directory name can be set explicitly or auto-detected from caller's package name via StackWalker.
- `PlayerUtils.java`: Helper for broadcasting messages to all connected players with or without color code parsing.

### Key Design Patterns

1. **ORMLite Integration**: All database persistence uses ORMLite annotations. The `DatabaseService` acts as a central registry of DAOs, requiring explicit `addTable()` calls before use.

2. **Configuration Files**: Plugin configs use JSON files wrapped in a root `config` object. `ConfigObjectProvider` subclasses define the config POJO and handle synchronization with disk.

3. **Path Management**: All plugin-specific files (configs, databases) go into `mods/{pluginName}/` managed by `PathUtils`. The plugin name can be set at startup or auto-detected.

4. **Message Formatting**: Color codes are parsed on-demand by `ColorUtils.parseColorCodes()`, which returns Hytale Message objects. Supports both `&` and `§` prefixes for compatibility.

### Dependencies

- **Hytale Server API** (compileOnly): `com.hypixel.hytale:Server:2026.01.27-734d39026`
- **ORMLite JDBC**: `com.j256.ormlite:ormlite-jdbc:6.1`
- **SQLite4j**: Bundled as `sqlite4j-compiled.jar` (custom JAR in project root)
- **JUnit 5**: For testing

### Build Configuration

- **Gradle Wrapper**: Use `./gradlew` instead of installing Gradle globally
- **Shadow JAR**: Configured to merge service files and exclude problematic SQLite classes (`SQLiteModuleMachineFuncGroup_0.class`)
- **Java Version**: Not explicitly set; check IntelliJ project settings if compilation issues arise

## Recently Modified Files

The following files have staged changes and may need attention:
- `src/main/java/models/db/DatabaseService.java`
- `src/main/java/util/ColorUtils.java`
- `src/main/java/util/ConfigObjectProvider.java`
- `src/main/java/util/DbUtils.java`
- `src/main/java/util/PathUtils.java`
- `src/main/java/util/PlayerUtils.java` (newly added)

Recent commits involved switching from sqlite-jdbc to sqlite4j driver and bundling the JAR to avoid dependency conflicts.
