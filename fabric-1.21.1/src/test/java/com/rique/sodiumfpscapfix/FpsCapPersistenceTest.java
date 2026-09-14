package com.rique.sodiumfpscapfix;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FpsCapPersistenceTest {
    private static final Path CONFIG_PATH = Path.of("config", "sodiumfpscapfix.txt");

    @BeforeEach
    void cleanConfig() throws IOException {
        Files.deleteIfExists(CONFIG_PATH);
    }

    @Test
    void loadReturnsEmptyWhenFileMissing() {
        assertTrue(FpsCapPersistence.load().isEmpty());
    }

    @Test
    void saveThenLoadRoundTrips() {
        FpsCapPersistence.save(1217);
        assertEquals(1217, FpsCapPersistence.load().orElseThrow());
    }

    @Test
    void saveClampsOutOfRangeValues() throws IOException {
        FpsCapPersistence.save(5);
        assertEquals("10", Files.readString(CONFIG_PATH, StandardCharsets.UTF_8));

        FpsCapPersistence.save(2_000_000);
        assertEquals("1000000", Files.readString(CONFIG_PATH, StandardCharsets.UTF_8));
    }

    @Test
    void loadSurvivesMalformedContent() throws IOException {
        Files.createDirectories(CONFIG_PATH.getParent());
        Files.writeString(CONFIG_PATH, "garbage", StandardCharsets.UTF_8);
        assertTrue(FpsCapPersistence.load().isEmpty());

        Files.writeString(CONFIG_PATH, "-50", StandardCharsets.UTF_8);
        assertTrue(FpsCapPersistence.load().isEmpty());
    }

    @Test
    void loadToleratesWhitespaceAndBom() throws IOException {
        Files.createDirectories(CONFIG_PATH.getParent());
        Files.writeString(CONFIG_PATH, "  120 \n", StandardCharsets.UTF_8);
        assertEquals(120, FpsCapPersistence.load().orElseThrow());

        Files.writeString(CONFIG_PATH, "﻿75", StandardCharsets.UTF_8);
        assertEquals(75, FpsCapPersistence.load().orElseThrow());
    }

    @Test
    void loadRejectsOversizedFile() throws IOException {
        Files.createDirectories(CONFIG_PATH.getParent());
        Files.writeString(CONFIG_PATH, "1".repeat(128), StandardCharsets.UTF_8);
        assertTrue(FpsCapPersistence.load().isEmpty());
    }

    @Test
    void loadClampsStoredValue() throws IOException {
        Files.createDirectories(CONFIG_PATH.getParent());
        Files.writeString(CONFIG_PATH, "9", StandardCharsets.UTF_8);
        assertEquals(10, FpsCapPersistence.load().orElseThrow());
    }
}
