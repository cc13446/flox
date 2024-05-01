package com.cc.flox.utils;

import com.google.gson.*;

import java.time.OffsetDateTime;

import static com.cc.flox.utils.FormatUtils.YYYY_MM_DD_HH_MM_SS;

/**
 * @author cc
 * @date 2024/4/20
 */
public class GsonUtils {

    /**
     * INS
     */
    public static final Gson INS = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>) (json, type, context) -> OffsetDateTime.parse(json.getAsString()))
            .registerTypeAdapter(OffsetDateTime.class, (JsonSerializer<OffsetDateTime>) (time, type, context) -> new JsonPrimitive(YYYY_MM_DD_HH_MM_SS.format(time)))
            .create();
}
