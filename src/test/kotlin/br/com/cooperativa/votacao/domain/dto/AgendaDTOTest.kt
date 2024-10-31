package br.com.cooperativa.votacao.domain.dto

import br.com.cooperativa.votacao.mapper.transform
import br.com.cooperativa.votacao.util.duration
import br.com.cooperativa.votacao.util.fromJson
import br.com.cooperativa.votacao.util.toJson
import br.com.cooperativa.votacao.utils.buildAgenda
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AgendaDTOTest {

    @Test
    fun `should map AgendaPersist to AgendaDTO`() {
        val persist = buildAgenda()
        val dto = persist.transform()

        assertEquals(persist.id, dto.id)
        assertEquals(persist.topic, dto.topic)
        assertEquals(persist.description, dto.description)

        assertEquals(persist.begin, dto.begin)
        assertEquals(persist.begin.duration(persist.end).seconds, dto.durationInSeconds)
    }

    @Test
    fun `should marshall and unmarshall`() {
        val obj = buildAgenda().transform()
        val json = toJson(obj)
        val unmarshall = fromJson(value = json, clazz = AgendaDTO::class.java)

        assertEquals(obj.id, unmarshall.id)
        assertEquals(obj.topic, unmarshall.topic)
        assertEquals(obj.description, unmarshall.description)

        assertEquals(obj.begin, unmarshall.begin)
        assertEquals(obj.durationInSeconds, unmarshall.durationInSeconds)
    }
}
