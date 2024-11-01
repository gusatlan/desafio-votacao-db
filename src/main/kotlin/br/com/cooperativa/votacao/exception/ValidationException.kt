package br.com.cooperativa.votacao.exception

class ValidationException(constraints: Collection<String>) :
    ApplicationException(message = constraints.joinToString())