# Changelog

## 1.0.32

- Fixed clicks, Tab focus, Enter, and Escape handling in every Sodium FPS textbox so Apply, Done, and back navigation keep working.
- Fixed clicks into the FPS textbox leaving Sodium's search field active and sending number input to both boxes.
- Fixed valid FPS values being treated as unsaved again immediately after applying, which could leave the settings screen stuck.
- Fixed Minecraft 26.x rounding typed FPS values down to the nearest 10 when applying or saving settings.
- Fixed NeoForge 1.21.1 failing during startup when Sodium's FPS option was initialized.
- Fixed NeoForge 1.21.1 rounding typed FPS limits down to the nearest multiple of ten.
- Added a labeled, aligned FPS textbox to the vanilla video settings screen when Sodium is not installed.
- Stopped optional Sodium integrations from logging missing-class warnings when the game is running without Sodium.
- Kept NeoForge's optional Sodium detection working across both the older and current FML loader APIs.
- Updated compatibility checks to the current stable Sodium releases for every supported Minecraft version.
- Updated Reese's Sodium Options compatibility on Fabric 1.21.11 to 2.2.3.
- Included the Unlicense text in every published jar.

## 1.0.31

- Fixed number input in the FPS cap box on Fabric 26.2 when Reese's Sodium Options is installed.
- Stopped the FPS box from swallowing Apply, Done, Enter, or Escape navigation.
- Updated the Fabric 26.2 Sodium compatibility target to 0.9.1.

## 1.0.30

- FPS values are now written only when the settings are applied, so canceled Sodium changes stay canceled.
- Removed repeated options-file writes while typing in the FPS box.
- Made the custom FPS value file atomic and resistant to truncated or oversized input.
- Fixed the limiter's interrupted and unlimited-state transitions so it does not busy-loop during shutdown or skip timing resets.
- Tightened textbox state cleanup and null handling across the supported version matrix.
