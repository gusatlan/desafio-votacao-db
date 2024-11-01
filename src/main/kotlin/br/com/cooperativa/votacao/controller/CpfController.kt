package br.com.cooperativa.votacao.controller

import br.com.cooperativa.votacao.domain.dto.VoteAbleType
import br.com.cooperativa.votacao.util.validateCpf
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@RestController
class CpfController {

    @GetMapping("/cpf/{cpf}")
    fun valid(
        @PathVariable(value = "cpf", required = true) cpf: String
    ): Mono<ResponseEntity<VoteAbleType>> {
        return validateCpf(text = cpf)
            .toMono()
            .map(VoteAbleType::of)
            .map {
                when (it) {
                    VoteAbleType.ABLE_TO_VOTE -> ResponseEntity.ok(it)
                    else -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(it)
                }
            }
    }
}
