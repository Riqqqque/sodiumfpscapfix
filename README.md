# Sodium FPS Cap Fix

Sodium FPS Cap Fix adds a textbox for Minecraft's FPS cap and removes the vanilla 260 FPS ceiling so you can type any cap you want. It still replaces Sodium's FPS cap control when Sodium is installed, but Sodium is no longer required.

## Supported targets

- Fabric 1.20.1
- Fabric 1.21.1
- Fabric 1.21.11
- Fabric 26.1.2
- Fabric 26.2
- NeoForge 1.21.1
- NeoForge 1.21.11
- NeoForge 26.1.2
- NeoForge 26.2

## Build

Run the main multi-version build with:

```powershell
.\gradlew.bat buildAll
```

Build the 26.1.2 targets with:

```powershell
.\mc-26.1.2-build\gradlew.bat buildAll
```

Build the 26.2 targets with:

```powershell
.\mc-26.2-build\gradlew.bat buildAll
```

The project expects the Sodium dependency jars in the `deps/` folder.

## Releases

After building, the main jars are collected into `modrinth-upload/` so they're all in one place for publishing.

Release changes are listed in [CHANGELOG.md](CHANGELOG.md).
