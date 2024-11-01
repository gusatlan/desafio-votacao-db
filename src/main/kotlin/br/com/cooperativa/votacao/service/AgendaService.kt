package br.com.cooperativa.votacao.service

import br.com.cooperativa.votacao.domain.dto.AgendaDTO
import br.com.cooperativa.votacao.domain.persist.AgendaPersist
import br.com.cooperativa.votacao.exception.EntityFoundException
import br.com.cooperativa.votacao.exception.ValidationException
import br.com.cooperativa.votacao.mapper.transform
import br.com.cooperativa.votacao.repository.AgendaRepository
import br.com.cooperativa.votacao.util.KAFKA_GROUP_VOTACAO
import br.com.cooperativa.votacao.util.KAFKA_TOPIC_AGENDA
import br.com.cooperativa.votacao.util.buildMapper
import br.com.cooperativa.votacao.util.cleanCodeText
import br.com.cooperativa.votacao.util.createLogger
import br.com.cooperativa.votacao.util.fromJson
import br.com.cooperativa.votacao.util.toJson
import br.com.cooperativa.votacao.util.validateList
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Service
class AgendaService(
    private val repository: AgendaRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    companion object {
        val logger = createLogger(this::class.java)
        val mapper = buildMapper()
    }

    fun send(value: AgendaDTO): Mono<AgendaDTO> {

        return validatePersist(value)
            .map {
                kafkaTemplate.send(KAFKA_TOPIC_AGENDA, toJson(value))
                it
            }
            .map(AgendaDTO::transform)
            .flatMap(this::save)
            .map(AgendaPersist::transform)
    }

    private fun filter(
        id: String? = null,
        topic: String? = null,
        description: String? = null,
        value: AgendaPersist
    ): Boolean {
        return listOf(
            id.isNullOrEmpty() || id.equals(value.id, ignoreCase = true),
            topic.isNullOrEmpty() || value.topic.trim().lowercase().contains(topic.trim().lowercase()),
            description.isNullOrEmpty() || value.description.trim().lowercase()
                .contains(description.trim().lowercase()),
        )
            .stream()
            .reduce { a, b -> a && b }
            .orElse(false)
    }

    fun find(
        id: String? = null,
        topic: String? = null,
        description: String? = null
    ): Flux<AgendaPersist> {
        logger.info("[AgendaService][find][BEGIN] [$id, $topic, $description]")

        val items = if (!id.isNullOrEmpty()) {
            repository.findById(id).toFlux()
        } else if (!topic.isNullOrEmpty()) {
            repository.findByTopicContainsIgnoreCase(topic)
        } else if (!description.isNullOrEmpty()) {
            repository.findByDescriptionContainsIgnoreCase(description)
        } else {
            Flux.empty()
        }

        return items
            .filter {
                filter(
                    id = id,
                    topic = topic,
                    description = description,
                    value = it
                )
            }
            .doOnNext {
                logger.debug("[AgendaService][find] [{}]", it)
            }
            .doOnComplete {
                logger.info("[AgendaService][find][END] [$id, $topic, $description]")
            }
    }

    fun exists(id: String): Mono<Boolean> {
        return repository.existsById(cleanCodeText(id))
    }

    fun validatePersist(value: AgendaDTO): Mono<AgendaDTO> {
        return exists(id = value.id)
            .map { exist ->
                if (exist) {
                    throw EntityFoundException(message = "Pauta já existe")
                } else {
                    val constraints = validateList(value)

                    if (constraints.isNotEmpty()) {
                        throw ValidationException(constraints = constraints)
                    } else {
                        value
                    }
                }
            }
    }

    fun save(value: AgendaDTO): Mono<AgendaDTO> {
        return validatePersist(value = value)
            .doOnError {
                logger.error("[AgendaService][save] [$value]", it)
            }
            .map(AgendaDTO::transform)
            .flatMap(this::save)
            .map(AgendaPersist::transform)
    }

    fun save(value: AgendaPersist): Mono<AgendaPersist> {
        return value
            .toMono()
            .doOnNext {
                logger.info("[AgendaService][SAVING] [$it]")
            }
            .flatMap(
                repository::save
            )
            .doOnNext {
                logger.info("[AgendaService][SAVED] [$it]")
            }
            .doOnError {
                logger.error("[AgendaService][SAVE][ERROR] [$value]", it)
            }
    }

    @KafkaListener(topics = [KAFKA_TOPIC_AGENDA], groupId = KAFKA_GROUP_VOTACAO)
    fun consumer(message: String?) {
        message
            ?.toMono()
            ?.map {
                fromJson(value = it, clazz = AgendaDTO::class.java, mapper = mapper)
            }
            ?.doOnNext {
                logger.info("[AgendaService][consumer][RECEIVED] [$it]")
            }
            ?.flatMap(this::save)
            ?.then()
            ?.subscribe()
    }
}

