# Changelog

## 1.0.30

- FPS values are now written only when the settings are applied, so canceled Sodium changes stay canceled.
- Removed repeated options-file writes while typing in the FPS box.
- Made the custom FPS value file atomic and resistant to truncated or oversized input.
- Fixed the limiter's interrupted and unlimited-state transitions so it does not busy-loop during shutdown or skip timing resets.
- Tightened textbox state cleanup and null handling across the supported version matrix.
