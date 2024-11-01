package br.com.cooperativa.votacao.exception

import br.com.cooperativa.votacao.mapper.MESSAGE_VOTE_ALREADY

class AlreadyVotedException(message: String = MESSAGE_VOTE_ALREADY) :
    ApplicationException(message = message) {
}