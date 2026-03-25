package com.rique.sodiumfpscapfix;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.OptionalInt;

public final class FpsCapPersistence {
    private static final Path CONFIG_PATH = Path.of("config", "sodiumfpscapfix.txt");

    private FpsCapPersistence() {
    }

    public static OptionalInt load() {
        if (!Files.isRegularFile(CONFIG_PATH)) {
            return OptionalInt.empty();
        }

        try {
            String text = Files.readString(CONFIG_PATH, StandardCharsets.UTF_8).trim();

            if (text.isEmpty()) {
                return OptionalInt.empty();
            }

            return OptionalInt.of(FpsCapSupport.parseAndClamp(text));
        } catch (IOException | NumberFormatException ignored) {
            return OptionalInt.empty();
        }
    }

    public static void save(int fpsCap) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(
                    CONFIG_PATH,
                    Integer.toString(FpsCapSupport.clamp(fpsCap)),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException ignored) {
        }
    }
}
