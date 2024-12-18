package br.com.cooperativa.votacao.controller

import br.com.cooperativa.votacao.domain.dto.AgendaDTO
import br.com.cooperativa.votacao.domain.persist.AgendaPersist
import br.com.cooperativa.votacao.mapper.toSummary
import br.com.cooperativa.votacao.service.AgendaService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AgendaController(
    private val service: AgendaService
) {

    @GetMapping("/agenda/id/{id}")
    fun findAgenda(
        @PathVariable("id") id: String
    ) = service.find(id = id).map(AgendaPersist::toSummary)

    @GetMapping("/agenda")
    fun searchAgenda(
        @RequestParam("id", required = false) id: String? = null,
        @RequestParam("topic", required = false) topic: String? = null,
        @RequestParam("description", required = false) description: String? = null
    ) = service.find(
        id = id,
        topic = topic,
        description = description
    ).map(AgendaPersist::toSummary)

    @PostMapping("/agenda")
    @ResponseStatus(HttpStatus.OK)
    fun save(
        @Valid @RequestBody value: AgendaDTO
    ) = service.send(value = value)

}