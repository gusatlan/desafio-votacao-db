package br.com.cooperativa.votacao.domain.dto

import br.com.cooperativa.votacao.mapper.MESSAGE_AGENDA_DESCRIPTION
import br.com.cooperativa.votacao.mapper.MESSAGE_AGENDA_ID
import br.com.cooperativa.votacao.mapper.MESSAGE_AGENDA_TOPIC
import br.com.cooperativa.votacao.util.cleanCodeText
import br.com.cooperativa.votacao.util.createId
import br.com.cooperativa.votacao.util.now
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDateTime

open class AgendaDTO(
    id: String = createId(),
    topic: String = "",
    description: String = "",
    val begin: LocalDateTime = now(),
    @field:Min(value = 10, message = "O tempo mínimo para votação é de 10 segundos") val durationInSeconds: Long = 60L
) {

    @NotEmpty(message = MESSAGE_AGENDA_ID)
    val id = cleanCodeText(id).trim().lowercase()

    @NotEmpty(message = MESSAGE_AGENDA_TOPIC)
    val topic = topic.trim()

    @NotEmpty(message = MESSAGE_AGENDA_DESCRIPTION)
    val description = description.trim()

    override fun equals(other: Any?) = other is AgendaDTO && id == other.id

    override fun hashCode() = id.hashCode()

    override fun toString() =
        """
            {
               "id": "$id",
               "topic": "$topic",
               "description": "$description",
               "begin": "$begin",
               "durationInSeconds": $durationInSeconds
            }
        """.trimIndent()
}