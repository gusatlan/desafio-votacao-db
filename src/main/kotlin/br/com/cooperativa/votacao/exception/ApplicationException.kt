package br.com.cooperativa.votacao.exception

import br.com.cooperativa.votacao.mapper.MESSAGE_APPLICATION_ERROR

open class ApplicationException(message: String = MESSAGE_APPLICATION_ERROR) : Exception(message)