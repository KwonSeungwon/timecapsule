package com.mini.timecapsule.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

public abstract class UJsons {
    public UJsons() {
    }

    public static boolean isValid(String json) {
        return isValid(json, PropertyMap.class);
    }

    public static boolean isValid(String json, Class<?> type) {
        try {
            (new ObjectMapper()).readValue(json, type);
            return true;
        } catch (IOException var3) {
            return false;
        }
    }

    public static <T> T parse(String json, Class<T> type) {
        try {
            return UStrings.isValid(json) && !"null".equalsIgnoreCase(json) ? (new ObjectMapper()).readValue(json, type) : null;
        } catch (IOException var3) {
            return null;
        }
    }

    public static PropertyMap parse(String json) {
        PropertyMap map = (PropertyMap)parse(json, PropertyMap.class);
        return map != null ? map : new PropertyMap();
    }


    public static <T> T read(File file, Class<T> type) {
        try {
            return (new ObjectMapper()).readValue(file, type);
        } catch (IOException var3) {
            return null;
        }
    }

    public static PropertyMap read(File file) {
        return safe((PropertyMap)read(file, PropertyMap.class));
    }

    public static <T> T read(InputStream input, Class<T> type) {
        try {
            return (new ObjectMapper()).readValue(input, type);
        } catch (IOException var3) {
            return null;
        }
    }

    public static PropertyMap read(InputStream input) {
        return safe((PropertyMap)read(input, PropertyMap.class));
    }

    public static <T> T readResource(String pathInResources, Class<T> type) {
        return read(type.getResourceAsStream(pathInResources), type);
    }

    public static PropertyMap readResource(String pathInResources) {
        return (PropertyMap)readResource(pathInResources, PropertyMap.class);
    }

    private static PropertyMap safe(PropertyMap map) {
        return map != null ? map : new PropertyMap();
    }

    public static String stringify(Object value) {
        try {
            return (new ObjectMapper()).writeValueAsString(value);
        } catch (Exception var2) {
            return "{}";
        }
    }
}
