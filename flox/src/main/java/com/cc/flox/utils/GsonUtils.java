package com.cc.flox.utils;

import com.google.gson.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author cc
 * @date 2024/4/20
 */
public class GsonUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH::mm::ss");

    /**
     * INS
     */
    public static final Gson INS = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>) (json, type, context) -> OffsetDateTime.parse(json.getAsString()))
            .registerTypeAdapter(OffsetDateTime.class, (JsonSerializer<OffsetDateTime>) (time, type, context) -> new JsonPrimitive(FORMATTER.format(time)))
            .create();
}
