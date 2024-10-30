package br.com.cooperativa.votacao.repository

import br.com.cooperativa.votacao.domain.persist.AgendaPersist
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface AgendaRepository : ReactiveMongoRepository<AgendaPersist, String> {

    fun findByTopicContainsIgnoreCase(topic: String): Flux<AgendaPersist>

    fun findByDescriptionContainsIgnoreCase(description: String): Flux<AgendaPersist>
}