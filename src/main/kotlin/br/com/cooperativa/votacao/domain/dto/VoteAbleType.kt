package br.com.cooperativa.votacao.domain.dto

enum class VoteAbleType(val value:Boolean) {
    ABLE_TO_VOTE(true), UNABLE_TO_VOTE(false);

    companion object {
        fun of(value: Boolean): VoteAbleType {
            return VoteAbleType.entries.stream().filter { it.value == value }.findFirst().get()
        }
    }
}
