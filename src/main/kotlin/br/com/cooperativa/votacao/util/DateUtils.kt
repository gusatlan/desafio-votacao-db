package br.com.cooperativa.votacao.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val formatterZonedDateTime = DateTimeFormatter.ISO_ZONED_DATE_TIME

private const val NANO = 999999999

fun getDefaultZoneId(): ZoneId = ZoneId.of("America/Sao_Paulo")

fun zonedNow(): ZonedDateTime = ZonedDateTime.now(getDefaultZoneId())

fun now(): LocalDateTime = zonedNow().toLocalDateTime()

fun LocalDateTime.duration(other: LocalDateTime): Duration = Duration.between(this, other)
