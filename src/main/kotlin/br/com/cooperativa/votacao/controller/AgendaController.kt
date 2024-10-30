package br.com.cooperativa.votacao.controller

import br.com.cooperativa.votacao.domain.persist.AgendaPersist
import br.com.cooperativa.votacao.service.AgendaService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class AgendaController(
    private val service: AgendaService
) {

    @GetMapping("/agenda/id/{id}")
    fun findAgenda(
        @PathVariable("id") id: String
    ): Flux<AgendaPersist> {
        return service.find(id = id)
    }
}