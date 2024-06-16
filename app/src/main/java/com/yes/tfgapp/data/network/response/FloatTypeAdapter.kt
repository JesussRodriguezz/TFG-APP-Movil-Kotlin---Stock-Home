package com.yes.tfgapp.data.network.response

import com.google.gson.*
import java.lang.reflect.Type

class FloatTypeAdapter : JsonDeserializer<Float?>, JsonSerializer<Float?> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Float? {
        return try {
            if (json == null || json.asString.isEmpty()) {
                -1.0f
            } else {
                json.asFloat
            }
        } catch (e: NumberFormatException) {
            -1.0f
        }
    }

    override fun serialize(src: Float?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src)
    }
}
