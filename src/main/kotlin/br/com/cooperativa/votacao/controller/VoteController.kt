package br.com.cooperativa.votacao.controller

import br.com.cooperativa.votacao.domain.dto.VoteDTO
import br.com.cooperativa.votacao.service.VoteService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class VoteController(
    private val service: VoteService
) {

    @PostMapping("/vote")
    fun save(@RequestBody value: VoteDTO) = service.send(value)

}