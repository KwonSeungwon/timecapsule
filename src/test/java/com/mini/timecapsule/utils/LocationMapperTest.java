package com.mini.timecapsule.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationMapperTest {

    @ParameterizedTest
    @CsvSource({
            "0, 0, BEACH",
            "499, 499, BEACH",
            "500, 0, MOUNTAIN",
            "999, 499, MOUNTAIN",
            "0, 500, DESERT",
            "499, 999, DESERT",
            "500, 500, FOREST",
            "1000, 1000, FOREST"
    })
    @DisplayName("Should map coordinates to correct biome")
    void shouldMapCoordinatesToBiome(int x, int y, String expectedBiome) {
        assertEquals(expectedBiome, LocationMapper.getBiome(x, y));
    }

    @ParameterizedTest
    @CsvSource({
            "BEACH, 고요한 바다",
            "MOUNTAIN, 바람의 능선",
            "DESERT, 붉은 사막",
            "FOREST, 신비한 숲",
            "UNKNOWN, 알 수 없는 곳"
    })
    @DisplayName("Should map biome ID to localized name")
    void shouldMapBiomeIdToName(String biomeId, String expectedName) {
        assertEquals(expectedName, LocationMapper.getBiomeName(biomeId));
    }
}
