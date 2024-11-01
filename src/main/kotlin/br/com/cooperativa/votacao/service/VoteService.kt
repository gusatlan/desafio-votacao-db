package br.com.cooperativa.votacao.service

import br.com.cooperativa.votacao.client.CpfClient
import br.com.cooperativa.votacao.domain.dto.VoteDTO
import br.com.cooperativa.votacao.domain.dto.VoteType
import br.com.cooperativa.votacao.domain.persist.AgendaPersist
import br.com.cooperativa.votacao.domain.persist.VotePersist
import br.com.cooperativa.votacao.exception.AgendaClosedException
import br.com.cooperativa.votacao.exception.AlreadyVotedException
import br.com.cooperativa.votacao.exception.EntityNotFoundException
import br.com.cooperativa.votacao.exception.ValidationException
import br.com.cooperativa.votacao.mapper.MESSAGE_VOTE_CPF
import br.com.cooperativa.votacao.mapper.MESSAGE_VOTE_NOT_SELECTED
import br.com.cooperativa.votacao.mapper.transform
import br.com.cooperativa.votacao.util.buildMapper
import br.com.cooperativa.votacao.util.createLogger
import br.com.cooperativa.votacao.util.validateList
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class VoteService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val agendaService: AgendaService,
    private val cpfClient: CpfClient
) {

    companion object {
        val logger = createLogger(this::class.java)
        val mapper = buildMapper()
    }

    fun validateCpf(value: VoteDTO): Mono<VoteDTO> {
        return cpfClient
            .verifyCpf(value.id)
            .map { valid ->
                when (valid) {
                    true -> value
                    else -> throw ValidationException(constraints = listOf(MESSAGE_VOTE_CPF))
                }
            }
            .doOnNext {
                logger.info("[VoteService][validateCpf][VALIDATED] [$it]")
            }
            .doOnError {
                logger.error("[VoteService][validateCpf][INVALID] [$value]", it)
            }

    }

    fun validateVote(value: VoteDTO): Mono<VoteDTO> {
        return value
            .toMono()
            .map {
                val constraints = validateList(value).toMutableList()

                if(VoteType.ofDescription(it.vote) == VoteType.NOT_SELECTED) {
                    constraints.add(MESSAGE_VOTE_NOT_SELECTED)
                }

                if (constraints.isNotEmpty()) {
                    throw ValidationException(constraints = constraints)
                } else {
                    it
                }
            }
            .doOnNext {
                logger.info("[VoteService][validateVote][VALIDATED] [$it]")
            }
            .doOnError {
                logger.error("[VoteService][validateVote][INVALID] [$value]", it)
            }

    }

    fun validateVoteAgenda(value: VoteDTO, agenda: AgendaPersist): Mono<VoteDTO> {
        return agenda
            .toMono()
            .map { agendaPersist ->
                val constraints = validateList(agendaPersist)

                if (constraints.isNotEmpty()) {
                    throw ValidationException(constraints = constraints)
                } else if (!agendaPersist.isOpen) {
                    throw AgendaClosedException()
                } else if (agenda.votes.stream().map(VotePersist::id).anyMatch { voteId -> voteId == value.id }) {
                    throw AlreadyVotedException()
                } else {
                    value
                }
            }
            .doOnNext {
                logger.info("[VoteService][validateVoteAgenda][VALIDATED] [$it]")
            }
            .doOnError {
                logger.error("[VoteService][validateVoteAgenda][INVALID] [$value]", it)
            }
    }

    fun save(value: VoteDTO): Mono<AgendaPersist> {
        return validateCpf(value)
            .flatMap(this::validateVote)
            .flatMap { vote ->
                agendaService.find(id = vote.agendaId)
                    .switchIfEmpty {
                        throw EntityNotFoundException()
                    }
                    .doOnError {
                        logger.error("[VoteService][save][ERROR] [$value]", it)
                    }
                    .collectList()
                    .map { agendas -> agendas.stream().findFirst().get() }
                    .flatMap { agenda ->
                        validateVoteAgenda(vote, agenda)
                            .map { voteValid ->
                                AgendaPersist.addVote(
                                    agenda=agenda,
                                    vote = voteValid.transform()
                                )
                            }
                    }
            }
            .flatMap(agendaService::save)
            .doOnNext {
                logger.info("[VoteService][save][VOTED] [$value]")
            }
    }

    fun send(value: VoteDTO): Mono<VoteDTO> {
        return save(value)
            .thenReturn(value)
    }

}