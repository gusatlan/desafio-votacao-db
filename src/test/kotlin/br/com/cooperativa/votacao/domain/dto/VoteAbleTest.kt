package br.com.cooperativa.votacao.domain.dto

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class VoteAbleTest {

    @Test
    fun `should transform`() {
        val voteAble = VoteAbleType.of(true)
        val voteUnable = VoteAbleType.of(false)

        assertEquals(VoteAbleType.ABLE_TO_VOTE, voteAble)
        assertEquals(VoteAbleType.UNABLE_TO_VOTE, voteUnable)
    }
}