package br.com.cooperativa.votacao.domain.persist

import br.com.cooperativa.votacao.domain.dto.VoteType
import br.com.cooperativa.votacao.util.cleanCodeText
import br.com.cooperativa.votacao.util.createId
import br.com.cooperativa.votacao.util.now
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime
import java.util.stream.Collectors

@Document(collection = "agenda")
class AgendaPersist(
    id: String = createId(),
    val begin: LocalDateTime = now(),
    val end: LocalDateTime = now(),
    topic: String = "",
    description: String = "",
    val votes: Set<VotePersist> = emptySet()
) {

    @NotEmpty(message = "Id não pode ser vazio")
    @Id
    var id = cleanCodeText(id)

    @NotEmpty(message = "Tópico da pauta não pode ser vazio")
    @Field("topic")
    val topic = topic.trim()

    @NotEmpty(message = "Descrição da pauta não pode ser vazia")
    @Field("description")
    val description = description.trim()

    @Transient
    @JsonIgnore
    val isOpen = validOpen()

    @Transient
    @JsonIgnore
    val summary = aggregateVotes()

    private fun validOpen(baseDate: LocalDateTime = now()): Boolean {
        return !baseDate.isAfter(end) && !baseDate.isBefore(begin)
    }

    private fun aggregateVotes(): Map<VoteType, Long> {
        return votes
            .stream()
            .filter { it.vote != VoteType.NOT_SELECTED }
            .map {
                it.vote to 1
            }
            .collect(
                Collectors.groupingBy({ it.first }, Collectors.summingLong { it.second.toLong() })
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
               "beginDate": "$begin",
               "endDate": "$end"
            }
        """.trimIndent()

    companion object {
        fun build(
            id: String? = null,
            topic: String,
            description: String,
            begin: LocalDateTime = now(),
            durationInSeconds: Long,
            votes: Set<VotePersist> = emptySet()
        ): AgendaPersist {
            return AgendaPersist(
                id = id ?: createId(),
                topic = topic,
                description = description,
                begin = begin,
                end = begin.plusSeconds(durationInSeconds),
                votes = votes
            )
        }

        fun addVote(agenda: AgendaPersist, vote: VotePersist, baseDate: LocalDateTime = now()): AgendaPersist {
            return if (agenda.validOpen(baseDate) && !agenda.votes.contains(vote)) {
                val votes = agenda.votes.toMutableSet()

                votes.add(vote)

                AgendaPersist(
                    id = agenda.id,
                    topic = agenda.topic,
                    description = agenda.description,
                    begin = agenda.begin,
                    end = agenda.end,
                    votes = votes
                )
            } else {
                agenda
            }
        }
    }
}
