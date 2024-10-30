package br.com.cooperativa.votacao.domain.persist

import br.com.cooperativa.votacao.util.cleanCodeText
import br.com.cooperativa.votacao.util.createId
import br.com.cooperativa.votacao.util.zonedNow
import jakarta.validation.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.ZonedDateTime

@Document(collection = "agenda")
class AgendaPersist(
    id: String = createId(),
    val beginDate: ZonedDateTime = zonedNow(),
    val endDate: ZonedDateTime = zonedNow(),
    topic: String = "",
    description: String = ""
) {

    @NotEmpty(message = "Id não pode ser vazio")
    @Id
    val id = cleanCodeText(id)

    @NotEmpty(message = "Tópico da pauta não pode ser vazio")
    @Field("topic")
    val topic = topic.trim()

    @NotEmpty(message = "Descrição da pauta não pode ser vazia")
    @Field("description")
    val description = description.trim()

    @Field("is_open")
    val isOpen = validOpen()


    private fun validOpen(baseDate: ZonedDateTime = zonedNow()): Boolean {
        return !baseDate.isAfter(endDate) && !baseDate.isBefore(beginDate)
    }

    override fun equals(other: Any?) = other is AgendaPersist && id == other.id

    override fun hashCode() = id.hashCode()

    override fun toString() =
        """
            {
               "id": "$id",
               "topic": "$topic",
               "description": "$description",
               "beginDate": "$beginDate",
               "endDate": "$endDate"
            }
        """.trimIndent()

    companion object {
        fun build(
            topic: String,
            description: String,
            begin: ZonedDateTime = zonedNow(),
            durationSeconds: Long
        ): AgendaPersist {
            return AgendaPersist(
                topic = topic,
                description = description,
                beginDate = begin,
                endDate = begin.plusSeconds(durationSeconds)
            )
        }
    }
}
