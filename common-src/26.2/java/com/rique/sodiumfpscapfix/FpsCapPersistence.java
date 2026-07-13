package com.rique.sodiumfpscapfix;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.OptionalInt;

public final class FpsCapPersistence {
    private static final Path CONFIG_PATH = Path.of("config", "sodiumfpscapfix.txt");
    private static final long MAX_CONFIG_BYTES = 64L;

    private FpsCapPersistence() {
    }

    public static OptionalInt load() {
        if (!Files.isRegularFile(CONFIG_PATH)) {
            return OptionalInt.empty();
        }

        try {
            if (Files.size(CONFIG_PATH) > MAX_CONFIG_BYTES) {
                return OptionalInt.empty();
            }

            String text = Files.readString(CONFIG_PATH, StandardCharsets.UTF_8).trim();

            if (!text.isEmpty() && text.charAt(0) == '\uFEFF') {
                text = text.substring(1).trim();
            }

            if (text.isEmpty()) {
                return OptionalInt.empty();
            }

            return OptionalInt.of(FpsCapSupport.parseAndClamp(text));
        } catch (IOException | NumberFormatException ignored) {
            return OptionalInt.empty();
        }
    }

    public static synchronized void save(int fpsCap) {
        Path temporaryPath = null;

        try {
            Path configDirectory = CONFIG_PATH.getParent();
            Files.createDirectories(configDirectory);
            temporaryPath = Files.createTempFile(configDirectory, "sodiumfpscapfix-", ".tmp");
            Files.writeString(
                    temporaryPath,
                    Integer.toString(FpsCapSupport.clamp(fpsCap)),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            try {
                Files.move(temporaryPath, CONFIG_PATH, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporaryPath, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ignored) {
        } finally {
            if (temporaryPath != null) {
                try {
                    Files.deleteIfExists(temporaryPath);
                } catch (IOException ignored) {
                }
            }
        }
    }
}
