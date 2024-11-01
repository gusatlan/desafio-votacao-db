package br.com.cooperativa.votacao.domain.dto

import br.com.cooperativa.votacao.util.toBool

enum class VoteType(
    val description: String,
    val flag: Boolean?
) {
    YES("Sim", true),
    NO("Não", false),
    NOT_SELECTED("Não Selecionado", null);

    companion object {
        fun ofFlag(value: Boolean?): VoteType {
            return VoteType.entries.stream().filter { it.flag == value }.findFirst().get()
        }

        fun ofDescription(value: String?): VoteType {
            return VoteType.entries.stream().filter { it.flag == value?.toBool() }.findFirst().get()
        }

    }
}