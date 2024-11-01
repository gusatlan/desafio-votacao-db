package br.com.cooperativa.votacao.domain.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VoteTypeTest {

    @Test
    fun `should be Yes`() {
        assertEquals(VoteType.YES, VoteType.ofFlag(true))
        assertEquals(VoteType.YES, VoteType.ofDescription("Verdadeiro"))
        assertEquals(VoteType.YES, VoteType.ofDescription("Sim"))
    }

    @Test
    fun `should be No`() {
        assertEquals(VoteType.NO, VoteType.ofFlag(false))
        assertEquals(VoteType.NO, VoteType.ofDescription("Não"))
    }

    @Test
    fun `should be not selected`() {
        assertEquals(VoteType.NOT_SELECTED, VoteType.ofFlag(null))
        assertEquals(VoteType.NOT_SELECTED, VoteType.ofDescription(null))
        assertEquals(VoteType.NOT_SELECTED, VoteType.ofDescription("Não selecionado"))
        assertEquals(VoteType.NOT_SELECTED, VoteType.ofDescription("não Selecionado"))
    }
}
