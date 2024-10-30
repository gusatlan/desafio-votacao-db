package br.com.cooperativa.votacao.service

import br.com.cooperativa.votacao.domain.persist.AgendaPersist
import br.com.cooperativa.votacao.repository.AgendaRepository
import br.com.cooperativa.votacao.util.buildMapper
import br.com.cooperativa.votacao.util.createLogger
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Service
class AgendaService(
    private val repository: AgendaRepository
) {

    companion object {
        val logger = createLogger(this::class.java)
        val mapper = buildMapper()
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
                logger.error("[AgendaService][SAVE] [$value]", it)
            }
    }
}
