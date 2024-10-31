package br.com.cooperativa.votacao.domain.dto

import br.com.cooperativa.votacao.util.duration
import br.com.cooperativa.votacao.util.now
import java.time.LocalDateTime

class SummaryAgendaDTO(
    id: String = "",
    topic: String = "",
    description: String = "",
    begin: LocalDateTime = now(),
    val end: LocalDateTime = now(),
    val summary: Map<VoteType, Long> = emptyMap()
) : AgendaDTO(
    id = id,
    topic = topic,
    description = description,
    begin = begin,
    durationInSeconds = begin.duration(end).seconds
)