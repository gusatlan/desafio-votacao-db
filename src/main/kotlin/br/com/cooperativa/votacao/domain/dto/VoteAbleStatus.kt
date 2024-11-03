package br.com.cooperativa.votacao.domain.dto

data class VoteAbleStatus(
    val status: VoteAbleType = VoteAbleType.UNABLE_TO_VOTE
)