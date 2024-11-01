package br.com.cooperativa.votacao.exception

class EntityNotFoundException(override val message: String = "Entidade não existente") :
    ApplicationException(message = message)