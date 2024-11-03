package br.com.cooperativa.votacao.domain.dto

import br.com.cooperativa.votacao.mapper.transform
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class VoteAbleStatusTest {

    @Test
    fun `should transform VoteAbleStatus to VoteAbleType`() {
        assertEquals(VoteAbleType.ABLE_TO_VOTE, VoteAbleStatus(status = VoteAbleType.ABLE_TO_VOTE).status)
        assertEquals(VoteAbleType.UNABLE_TO_VOTE, VoteAbleStatus(status = VoteAbleType.UNABLE_TO_VOTE).status)
    }

    @Test
    fun `should transform VoteAbleType to VoteAbleStatus`() {
        assertEquals(VoteAbleType.ABLE_TO_VOTE, VoteAbleType.ABLE_TO_VOTE.transform().status)
        assertEquals(VoteAbleType.UNABLE_TO_VOTE, VoteAbleType.UNABLE_TO_VOTE.transform().status)
    }
}
