package br.com.cooperativa.votacao.domain.dto

import br.com.cooperativa.votacao.util.cleanCodeText
import br.com.cooperativa.votacao.util.now
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDateTime

class VoteDTO(
    id: String = "",
    agendaId: String = "",
    val vote: VoteType = VoteType.NOT_SELECTED,
    val createdAt: LocalDateTime = now()
) {

    @NotEmpty(message = "Id não pode ser nula")
    val id = cleanCodeText(id)

    @NotEmpty(message = "Id da pauta não pode ser nula")
    val agendaId = cleanCodeText(agendaId)

    @JsonIgnore
    fun isValid() = id.isNotEmpty() && agendaId.isNotEmpty() && vote != VoteType.NOT_SELECTED

    override fun equals(other: Any?) = other is VoteDTO && agendaId == other.agendaId && id == other.id

    override fun hashCode() = agendaId.hashCode() or id.hashCode()

    override fun toString() = """{"agendaId": "$agendaId", "id": "$id", "vote": "$vote", "createdAt": "$createdAt"}"""
}
