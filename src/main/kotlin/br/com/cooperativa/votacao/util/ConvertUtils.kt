package br.com.cooperativa.votacao.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

val logger = createLogger()

class ZonedDateTimeDeserializer : JsonDeserializer<ZonedDateTime>() {
    @Throws(IOException::class)
    override fun deserialize(jsonParser: JsonParser?, deserializationContext: DeserializationContext?): ZonedDateTime {
        return ZonedDateTime.parse(
            jsonParser?.text,
            DateTimeFormatter.ISO_ZONED_DATE_TIME
        )
    }
}

fun buildMapper(): ObjectMapper {
    val mapper = ObjectMapper()
    val module = JavaTimeModule()
        .addSerializer(
            ZonedDateTime::class.java, ZonedDateTimeSerializer(
                DateTimeFormatter.ISO_ZONED_DATE_TIME
            )
        )
        .addDeserializer(ZonedDateTime::class.java, ZonedDateTimeDeserializer())

    mapper
        .registerModule(module)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)

    return mapper
}

fun cleanCode(text: String?): String {
    return if (text != null) {
        val pattern = "\\D".toRegex()
        pattern.replace(text.trim(), "")
    } else {
        ""
    }
}

fun cleanCodeText(text: String?): String {
    return if (text != null) {
        val pattern = "\\W".toRegex()
        pattern.replace(text.trim(), "").lowercase()
    } else {
        ""
    }
}

fun <T : Any> toJson(value: T, mapper: ObjectMapper = buildMapper()): String = mapper.writeValueAsString(value)

fun <T : Any> fromJson(value: String, clazz: Class<T>, mapper: ObjectMapper = buildMapper()): T =
    mapper.readValue(value, clazz)

fun <T : Any> fromJsonList(value: String, clazz: Class<T>, mapper: ObjectMapper = buildMapper()): ArrayList<T> {
    val listType: CollectionType = mapper.typeFactory.constructCollectionType(
        ArrayList::class.java, clazz
    )
    val items: ArrayList<T> = mapper.readValue(value, listType)
    return items
}

fun createId() = cleanCodeText(UUID.randomUUID().toString().trim().lowercase())

fun <T : Any> createLogger(clazz: Class<T>? = null): Logger {
    return if (clazz != null) {
        LoggerFactory.getLogger(clazz)
    } else {
        LoggerFactory.getLogger(object {}::class.java)
    }
}

fun createLogger(): Logger = createLogger(object {}::class.java)
