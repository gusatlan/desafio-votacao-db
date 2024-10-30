package br.com.cooperativa.votacao

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VotacaoApplication

fun main(args: Array<String>) {
	runApplication<VotacaoApplication>(*args)
}
