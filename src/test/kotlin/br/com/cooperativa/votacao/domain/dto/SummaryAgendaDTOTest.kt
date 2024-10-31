package br.com.cooperativa.votacao.domain.dto

import br.com.cooperativa.votacao.mapper.toSummary
import br.com.cooperativa.votacao.util.fromJson
import br.com.cooperativa.votacao.util.toJson
import br.com.cooperativa.votacao.utils.buildAgenda
import br.com.cooperativa.votacao.utils.buildVotes
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import kotlin.streams.asStream
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SummaryAgendaDTOTest {

    private fun countEven(size: Int): Long {
        return (1..size).asSequence().asStream().filter { it % 2 == 0 }.count()
    }

    @Test
    fun `should be equals transform`() {
        val obj1 = buildAgenda(id = "A1").toSummary()
        val obj2 = buildAgenda(id = "a1").toSummary()

        assertEquals("a1", obj1.id)
        assertEquals("a1", obj2.id)
        assertEquals(obj1, obj2)
        assertEquals(obj1.id, obj2.id)

        assertEquals(obj1.topic, obj2.topic)
        assertEquals(obj1.description, obj2.description)
        assertEquals(obj1.begin, obj2.begin)
        assertEquals(obj1.end, obj2.end)
    }

    @Test
    fun `should not be equals transform`() {
        val obj1 = buildAgenda(id = "A1").toSummary()
        val obj2 = buildAgenda(id = "B1").toSummary()

        assertEquals("a1", obj1.id)
        assertEquals("b1", obj2.id)

        assertNotEquals(obj1, obj2)
        assertNotEquals(obj1.id, obj2.id)
    }

    @Test
    fun `should marshall and unmarshall`() {
        val quantity = 2
        val obj = buildAgenda(votes = buildVotes(quantity = quantity)).toSummary()
        val json = toJson(obj)
        val unmarshall = fromJson(value = json, clazz = SummaryAgendaDTO::class.java)

        assertEquals(obj, unmarshall)
        assertEquals(obj.id, unmarshall.id)
        assertEquals(obj.topic, unmarshall.topic)
        assertEquals(obj.description, unmarshall.description)
        assertEquals(obj.begin, unmarshall.begin)
        assertEquals(obj.end, unmarshall.end)
        assertEquals(obj.durationInSeconds, unmarshall.durationInSeconds)
        assertEquals(obj.summary.size, unmarshall.summary.size)
        assertEquals(quantity, unmarshall.summary.size)
    }

    @Test
    fun `should compute votes`() {
        val quantity = 1001
        val voteYes = countEven(quantity)
        val voteNo = quantity - voteYes
        val obj = buildAgenda(votes = buildVotes(quantity = quantity)).toSummary()

        assertEquals(voteYes, obj.summary[VoteType.YES])
        assertEquals(voteNo, obj.summary[VoteType.NO])
        assertNull(obj.summary[VoteType.NOT_SELECTED])
    }
}
