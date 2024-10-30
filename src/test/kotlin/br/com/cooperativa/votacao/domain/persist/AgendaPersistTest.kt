package br.com.cooperativa.votacao.domain.persist

import br.com.cooperativa.votacao.util.fromJson
import br.com.cooperativa.votacao.util.toJson
import br.com.cooperativa.votacao.utils.buildAgenda
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AgendaPersistTest {

    @Test

    fun `should be equals`() {
        val obj1 = buildAgenda(id = "1")
        val obj2 = buildAgenda(id = "1")

        assertEquals(obj1, obj2)

        assertEquals(obj1.id, obj2.id)
        assertEquals(obj1.topic, obj2.topic)
        assertEquals(obj1.description, obj2.description)
    }

    @Test
    fun `should not be equals`() {
        val obj1 = buildAgenda()
        val obj2 = buildAgenda()

        assertNotEquals(obj1, obj2)

        assertNotEquals(obj1.id, obj2.id)
        assertEquals(obj1.topic, obj2.topic)
        assertEquals(obj1.description, obj2.description)
        assertNotEquals(obj1.beginDate, obj1.endDate)
    }

    @Test
    fun `should marshall and unmarshall`() {
        val obj = buildAgenda()
        val json = toJson(obj)
        val unmarshall = fromJson(value = json, clazz = AgendaPersist::class.java)

        assertEquals(obj, unmarshall)
        assertEquals(obj.beginDate, unmarshall.beginDate)
        assertEquals(obj.endDate, unmarshall.endDate)
    }

}