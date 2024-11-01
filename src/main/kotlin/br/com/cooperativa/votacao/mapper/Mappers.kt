package br.com.cooperativa.votacao.mapper

import br.com.cooperativa.votacao.domain.dto.AgendaDTO
import br.com.cooperativa.votacao.domain.dto.SummaryAgendaDTO
import br.com.cooperativa.votacao.domain.dto.VoteDTO
import br.com.cooperativa.votacao.domain.dto.VoteType
import br.com.cooperativa.votacao.domain.persist.AgendaPersist
import br.com.cooperativa.votacao.domain.persist.VotePersist
import br.com.cooperativa.votacao.util.duration

fun AgendaPersist.transform(): AgendaDTO {
    return AgendaDTO(
        id = this.id,
        topic = this.topic,
        description = this.description,
        begin = this.begin,
        durationInSeconds = this.begin.duration(this.end).seconds
    )
}

fun AgendaPersist.toSummary(): SummaryAgendaDTO {
    return SummaryAgendaDTO(
        id = this.id,
        topic = this.topic,
        description = this.description,
        begin = this.begin,
        end = this.end,
        summary = this.summary
    )
}

fun AgendaDTO.transform(): AgendaPersist {
    return AgendaPersist.build(
        id = this.id,
        topic = this.topic,
        description = this.description,
        begin = this.begin,
        durationInSeconds = this.durationInSeconds
    )
}

fun VotePersist.transform(agendaId: String): VoteDTO {
    return VoteDTO(
        id = this.id,
        agendaId = agendaId,
        vote = this.vote.description,
        createdAt = this.createdAt
    )
}

fun VoteDTO.transform(): VotePersist {
    return VotePersist(
        id = this.id,
        vote = VoteType.ofDescription(this.vote),
        createdAt = this.createdAt
    )
}
