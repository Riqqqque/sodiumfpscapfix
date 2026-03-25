# Sodium FPS Cap Fix

Sodium FPS Cap Fix replaces Sodium's FPS cap slider with a textbox and removes the vanilla 260 FPS ceiling so you can type any cap you want.

## Supported targets

- Fabric 1.20.1
- Fabric 1.21.1
- Fabric 1.21.11
- NeoForge 1.21.1
- NeoForge 1.21.11

## Build

Run:

```powershell
.\gradlew.bat buildAll
```

The project expects the Sodium dependency jars in the `deps/` folder.

## Releases

After building, the main jars are collected into `modrinth-upload/` so they're all in one place for publishing.
