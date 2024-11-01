package br.com.cooperativa.votacao.exception

import br.com.cooperativa.votacao.mapper.MESSAGE_ENTITY_FOUND

class EntityFoundException(override val message: String = MESSAGE_ENTITY_FOUND) :
    ApplicationException(message = message)