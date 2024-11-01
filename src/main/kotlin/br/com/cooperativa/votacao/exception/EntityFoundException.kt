package br.com.cooperativa.votacao.exception

class EntityFoundException(override val message: String = "Entidade já existente") :
    ApplicationException(message = message)