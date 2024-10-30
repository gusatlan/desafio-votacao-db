package br.com.cooperativa.votacao.domain.persist

import br.com.cooperativa.votacao.domain.dto.VoteType
import br.com.cooperativa.votacao.util.fromJson
import br.com.cooperativa.votacao.util.toJson
import br.com.cooperativa.votacao.utils.buildVote
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class VotePersistTest {

    @Test
    fun `should be equals`() {
        val obj1 = buildVote(id = "1")
        val obj2 = buildVote(id = "1")

        assertEquals(obj1, obj2)
        assertEquals(obj1.id, obj2.id)
        assertEquals(obj1.vote, obj2.vote)
    }

    @Test
    fun `should not be equals`() {
        val obj1 = buildVote(id = "1")
        val obj2 = buildVote(id = "2", vote = VoteType.NO)

        assertNotEquals(obj1, obj2)
        assertNotEquals(obj1.id, obj2.id)
        assertNotEquals(obj1.vote, obj2.vote)
    }

    @Test
    fun `should marshall and unmarshall`() {
        val obj = buildVote()
        val json = toJson(obj)
        val unmarshall = fromJson(value = json, clazz = VotePersist::class.java)

        assertEquals(obj, unmarshall)
        assertEquals(obj.id, unmarshall.id)
        assertEquals(obj.vote, unmarshall.vote)
    }
}