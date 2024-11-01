package br.com.cooperativa.votacao.exception

import br.com.cooperativa.votacao.mapper.MESSAGE_ENTITY_NOT_FOUND

class EntityNotFoundException(override val message: String = MESSAGE_ENTITY_NOT_FOUND) :
    ApplicationException(message = message)