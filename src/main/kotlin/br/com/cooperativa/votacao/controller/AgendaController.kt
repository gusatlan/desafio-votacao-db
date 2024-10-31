package br.com.cooperativa.votacao.controller

import br.com.cooperativa.votacao.domain.dto.AgendaDTO
import br.com.cooperativa.votacao.domain.persist.AgendaPersist
import br.com.cooperativa.votacao.mapper.toSummary
import br.com.cooperativa.votacao.mapper.transform
import br.com.cooperativa.votacao.service.AgendaService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AgendaController(
    private val service: AgendaService
) {

    @GetMapping("/agenda/id/{id}")
    fun findAgenda(
        @PathVariable("id") id: String
    ) = service.find(id = id).map(AgendaPersist::toSummary)

    @PostMapping("/agenda")
    fun save(
        @Valid @RequestBody value: AgendaDTO
    ) = service.save(value = value.transform())

}