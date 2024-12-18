package br.com.cooperativa.votacao.domain.dto

import br.com.cooperativa.votacao.mapper.transform
import br.com.cooperativa.votacao.util.cleanCodeText
import br.com.cooperativa.votacao.util.fromJson
import br.com.cooperativa.votacao.util.toJson
import br.com.cooperativa.votacao.util.validate
import br.com.cooperativa.votacao.utils.buildVote
import br.com.cooperativa.votacao.utils.getInvalidCpf
import br.com.cooperativa.votacao.utils.getValidCpf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VoteDTOTest {

    @Test
    fun `should transform`() {
        val persist = buildVote()
        val dto = persist.transform(agendaId = "1")

        assertEquals(persist.id, dto.id)
        assertEquals(persist.vote, VoteType.ofDescription(dto.vote))
        assertEquals(persist.createdAt, dto.createdAt)
    }

    @Test
    fun `should marshall and unmarshall`() {
        val obj = buildVote().transform(agendaId = "1")
        val json = toJson(obj)
        val unmarshall = fromJson(value = json, clazz = VoteDTO::class.java)

        assertEquals(obj.id, unmarshall.id)
        assertEquals(obj.agendaId, unmarshall.agendaId)
        assertEquals(obj.vote, unmarshall.vote)
        assertEquals(obj.createdAt, unmarshall.createdAt)
    }

    @Test
    fun `should be valid`() {
        assertTrue(validate(buildVote(id = getValidCpf()).transform(agendaId = "1")).isEmpty())
    }

    @Test
    fun `should be invalid`() {
        val obj = buildVote(id = "", vote = VoteType.NOT_SELECTED).transform(agendaId = "")

        assertEquals(3, validate(obj).size)
        assertFalse(obj.isValid())
    }

    @Test
    fun `should CPF be valid`() {
        val cpf = getValidCpf()
        val obj = buildVote(id = cpf).transform(agendaId = "1")

        assertEquals(cleanCodeText(cpf), obj.id)
        assertTrue(validate(obj).isEmpty())
    }

    @Test
    fun `should CPF not be valid`() {
        val cpf = getInvalidCpf()
        val obj = buildVote(id = cpf).transform(agendaId = "1")

        assertEquals(cleanCodeText(cpf), obj.id)
        Assertions.assertFalse(validate(obj).isEmpty())
    }

    @Test
    fun `should CPF not be valid with same digits`() {
        val cpf = "111.111.111-11"
        val obj = buildVote(id = cpf).transform(agendaId = "1")

        assertEquals(cleanCodeText(cpf), obj.id)
        Assertions.assertFalse(validate(obj).isEmpty())
    }

}
