package br.com.cooperativa.votacao.domain.dto

enum class VoteType(
    val description: String,
    val flag: Boolean?
) {
    YES("Sim", true),
    NO("Não", false),
    NOT_SELECTED("Não Selecionado", null)
}