package br.com.cooperativa.votacao.utils

import br.com.cooperativa.votacao.domain.persist.AgendaPersist
import br.com.cooperativa.votacao.util.getDefaultZoneId
import java.time.ZonedDateTime

fun buildAgenda(
    id: String? = null,
    topic: String = "Topic",
    description: String = "Descriptio of Agenda",
    begin: ZonedDateTime = ZonedDateTime.of(2024, 10, 30, 8, 29, 0, 0, getDefaultZoneId()),
    durationInSeconds: Long = 60
): AgendaPersist {
    return AgendaPersist.build(
        id = id,
        topic = topic,
        description = description,
        begin = begin,
        durationInSeconds = durationInSeconds
    )
}