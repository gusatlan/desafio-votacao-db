package br.com.cooperativa.votacao.exception

class ValidationException(private val constraints: Collection<String>) :
    ApplicationException(message = constraints.joinToString())