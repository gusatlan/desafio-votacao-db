package br.com.cooperativa.votacao.domain.persist

import br.com.cooperativa.votacao.domain.dto.VoteType
import br.com.cooperativa.votacao.util.cleanCodeText
import br.com.cooperativa.votacao.util.createId
import br.com.cooperativa.votacao.util.zonedNow
import jakarta.validation.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.ZonedDateTime
import java.util.stream.Collectors

@Document(collection = "agenda")
class AgendaPersist(
    id: String = createId(),
    val beginDate: ZonedDateTime = zonedNow(),
    val endDate: ZonedDateTime = zonedNow(),
    topic: String = "",
    description: String = "",
    val votes: Set<VotePersist> = emptySet()
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

    @Field("summary")
    val summary = aggregateVotes()

    private fun validOpen(baseDate: ZonedDateTime = zonedNow()): Boolean {
        return !baseDate.isAfter(endDate) && !baseDate.isBefore(beginDate)
    }

    private fun aggregateVotes(): Map<VoteType, Int> {
        return votes
            .stream()
            .filter { it.vote != VoteType.NOT_SELECTED }
            .map {
                it.vote to 1
            }
            .collect(
                Collectors.groupingBy({ it.first }, Collectors.summingInt { it.second })
            )
            .toMap()
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
            id: String? = null,
            topic: String,
            description: String,
            begin: ZonedDateTime = zonedNow(),
            durationInSeconds: Long,
            votes: Set<VotePersist> = emptySet()
        ): AgendaPersist {
            return AgendaPersist(
                id = id ?: createId(),
                topic = topic,
                description = description,
                beginDate = begin,
                endDate = begin.plusSeconds(durationInSeconds),
                votes = votes
            )
        }

        fun addVote(agenda: AgendaPersist, vote: VotePersist, baseDate: ZonedDateTime = zonedNow()): AgendaPersist {
            return if (agenda.validOpen(baseDate) && !agenda.votes.contains(vote)) {
                val votes = agenda.votes.toMutableSet()

                votes.add(vote)

                AgendaPersist(
                    id = agenda.id,
                    topic = agenda.topic,
                    description = agenda.description,
                    beginDate = agenda.beginDate,
                    endDate = agenda.endDate,
                    votes = votes
                )
            } else {
                agenda
            }
        }
    }
}
