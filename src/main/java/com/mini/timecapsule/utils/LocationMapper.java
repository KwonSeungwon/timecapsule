package com.mini.timecapsule.utils;

public class LocationMapper {
    /**
     * Maps coordinates (x, y) to biomes.
     * Coordinate system assumed to be 1000x1000.
     * x < 500, y < 500: BEACH
     * x >= 500, y < 500: MOUNTAIN
     * x < 500, y >= 500: DESERT
     * otherwise: FOREST
     */
    public static String getBiome(int x, int y) {
        if (x < 500 && y < 500) return "BEACH";
        if (x >= 500 && y < 500) return "MOUNTAIN";
        if (x < 500 && y >= 500) return "DESERT";
        return "FOREST";
    }

    /**
     * Maps biome IDs to localized names.
     */
    public static String getBiomeName(String biome) {
        if (biome == null) return "알 수 없는 곳";
        
        switch (biome) {
            case "BEACH": return "고요한 바다";
            case "MOUNTAIN": return "바람의 능선";
            case "DESERT": return "붉은 사막";
            case "FOREST": return "신비한 숲";
            default: return "알 수 없는 곳";
        }
    }
}
