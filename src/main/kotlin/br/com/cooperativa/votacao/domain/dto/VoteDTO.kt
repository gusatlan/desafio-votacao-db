package br.com.cooperativa.votacao.domain.dto

import br.com.cooperativa.votacao.mapper.MESSAGE_AGENDA_ID
import br.com.cooperativa.votacao.mapper.MESSAGE_VOTE_CPF
import br.com.cooperativa.votacao.mapper.MESSAGE_VOTE_ID
import br.com.cooperativa.votacao.util.cleanCodeText
import br.com.cooperativa.votacao.util.now
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.br.CPF
import java.time.LocalDateTime

class VoteDTO(
    id: String = "",
    agendaId: String = "",
    val vote: String = VoteType.NOT_SELECTED.description,
    val createdAt: LocalDateTime = now()
) {

    @NotEmpty(message = MESSAGE_VOTE_ID)
    @CPF(message = MESSAGE_VOTE_CPF)
    val id = cleanCodeText(id)

    @NotEmpty(message = MESSAGE_AGENDA_ID)
    val agendaId = cleanCodeText(agendaId)

    @JsonIgnore
    fun isValid() = id.isNotEmpty() && agendaId.isNotEmpty() && VoteType.ofDescription(vote) != VoteType.NOT_SELECTED

    override fun equals(other: Any?) = other is VoteDTO && agendaId == other.agendaId && id == other.id

    override fun hashCode() = agendaId.hashCode() or id.hashCode()

    override fun toString() = """{"agendaId": "$agendaId", "id": "$id", "vote": "$vote", "createdAt": "$createdAt"}"""
}
