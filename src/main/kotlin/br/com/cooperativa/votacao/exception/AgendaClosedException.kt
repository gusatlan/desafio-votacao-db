package br.com.cooperativa.votacao.exception

import br.com.cooperativa.votacao.mapper.MESSAGE_AGENDA_CLOSED

class AgendaClosedException(message: String = MESSAGE_AGENDA_CLOSED) : ApplicationException(message = message) {
}