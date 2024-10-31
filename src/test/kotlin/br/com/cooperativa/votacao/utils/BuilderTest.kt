package br.com.cooperativa.votacao.utils

import br.com.cooperativa.votacao.domain.dto.VoteType
import br.com.cooperativa.votacao.domain.persist.AgendaPersist
import br.com.cooperativa.votacao.domain.persist.VotePersist
import br.com.cooperativa.votacao.util.createId
import java.time.LocalDateTime
import kotlin.streams.asStream

fun buildAgenda(
    id: String? = null,
    topic: String = "Topic",
    description: String = "Descriptio of Agenda",
    begin: LocalDateTime = LocalDateTime.of(2024, 10, 30, 8, 29, 0, 0),
    durationInSeconds: Long = 60,
    votes: Set<VotePersist> = emptySet()
): AgendaPersist {
    return AgendaPersist.build(
        id = id,
        topic = topic,
        description = description,
        begin = begin,
        durationInSeconds = durationInSeconds,
        votes = votes
    )
}

fun buildVote(
    id: String = createId(),
    vote: VoteType = VoteType.YES
): VotePersist {
    return VotePersist(
        id = id,
        vote = vote
    )
}

fun buildVotes(quantity: Int = 5): Set<VotePersist> {
    return (1..quantity)
        .asSequence()
        .asStream()
        .map {
            val vote = if (it % 2 == 0) {
                VoteType.YES
            } else {
                VoteType.NO
            }

            buildVote(
                id = it.toString(),
                vote = vote
            )
        }
        .toList()
        .toSet()
}