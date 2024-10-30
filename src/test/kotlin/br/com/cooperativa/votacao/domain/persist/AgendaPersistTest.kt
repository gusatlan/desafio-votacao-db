package br.com.cooperativa.votacao.domain.persist

import br.com.cooperativa.votacao.domain.dto.VoteType
import br.com.cooperativa.votacao.util.fromJson
import br.com.cooperativa.votacao.util.toJson
import br.com.cooperativa.votacao.util.validateList
import br.com.cooperativa.votacao.util.zonedNow
import br.com.cooperativa.votacao.utils.buildAgenda
import br.com.cooperativa.votacao.utils.buildVote
import br.com.cooperativa.votacao.utils.buildVotes
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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

    @Test
    fun `should be valid`() {
        val obj = buildAgenda()

        assertTrue(validateList(obj).isEmpty())
    }

    @Test
    fun `should not be valid`() {
        val obj = buildAgenda(id = "")

        assertFalse(validateList(obj).isEmpty())
    }

    @Test
    fun `should add vote`() {
        val quantity = 2
        val obj1 = buildAgenda(begin = zonedNow(), durationInSeconds = 300, votes = buildVotes(quantity))
        val obj2 = AgendaPersist.addVote(obj1, buildVote())

        assertEquals(obj1, obj2)
        assertEquals(quantity, obj1.votes.size)
        assertNotEquals(quantity, obj2.votes.size)
    }

    @Test
    fun `should compute votes`() {
        val quantity = 10
        val obj = buildAgenda(votes = buildVotes(quantity))
        val voteYes = obj.votes.stream().filter { it.vote == VoteType.YES }.count()
        val voteNo = obj.votes.stream().filter { it.vote == VoteType.NO }.count()
        val voteNull = obj.votes.stream().filter { it.vote == VoteType.NOT_SELECTED }.count()

        assertEquals(0, voteNull)
        assertEquals(quantity/2L, voteYes)
        assertEquals(quantity/2L, voteNo)

        assertEquals(voteYes, obj.summary[VoteType.YES]!!.toLong())
        assertEquals(voteNo, obj.summary[VoteType.NO]!!.toLong())
    }
}
