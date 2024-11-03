package br.com.cooperativa.votacao.util

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun getDefaultZoneId(): ZoneId = ZoneId.of("America/Sao_Paulo")

fun zonedNow(): ZonedDateTime = ZonedDateTime.now(getDefaultZoneId())

fun now(): LocalDateTime = zonedNow().toLocalDateTime()

fun LocalDateTime.duration(other: LocalDateTime): Duration = Duration.between(this, other)
