package br.com.cooperativa.votacao.service

import br.com.cooperativa.votacao.client.CpfClient
import br.com.cooperativa.votacao.domain.dto.VoteType
import br.com.cooperativa.votacao.exception.AgendaClosedException
import br.com.cooperativa.votacao.exception.AlreadyVotedException
import br.com.cooperativa.votacao.exception.ValidationException
import br.com.cooperativa.votacao.mapper.transform
import br.com.cooperativa.votacao.repository.AgendaRepository
import br.com.cooperativa.votacao.util.now
import br.com.cooperativa.votacao.util.validateCpf
import br.com.cooperativa.votacao.utils.buildAgenda
import br.com.cooperativa.votacao.utils.buildVote
import br.com.cooperativa.votacao.utils.getInvalidCpf
import br.com.cooperativa.votacao.utils.getValidCpf
import br.com.cooperativa.votacao.utils.getValidCpf2
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.kafka.core.KafkaTemplate
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.expectError
import reactor.test.StepVerifier

class VoteServiceTest {

    private val kafkaTemplate = mockk<KafkaTemplate<String, String>>(relaxed = true)
    private val repository = mockk<AgendaRepository>(relaxed = true)
    private val agendaService = AgendaService(repository = repository, kafkaTemplate = kafkaTemplate)
    private val cpfClient = mockk<CpfClient>(relaxed = true)
    private val voteService =
        VoteService(kafkaTemplate = kafkaTemplate, agendaService = agendaService, cpfClient = cpfClient)

    @Test
    fun `validate constraints be valid`() {
        val vote = buildVote(id = getValidCpf()).transform(agendaId = "1")
        StepVerifier
            .create(voteService.validateVote(vote))
            .expectNext(vote)
            .expectComplete()
    }

    @Test
    fun `validate constraints not be valid 1`() {
        val vote = buildVote(id = getInvalidCpf(), vote = VoteType.NOT_SELECTED).transform(agendaId = "1")
        StepVerifier
            .create(voteService.validateVote(vote))
            .expectError<ValidationException>()
            .verify()
    }

    @Test
    fun `validate constraints not be valid 2`() {
        val vote = buildVote(id = getValidCpf(), vote = VoteType.NOT_SELECTED).transform(agendaId = "1")
        StepVerifier
            .create(voteService.validateVote(vote))
            .expectError<ValidationException>()
            .verify()
    }

    @Test
    fun `validate CPF valid`() {
        val vote = buildVote(id = getValidCpf()).transform(agendaId = "1")

        every { cpfClient.verifyCpf(vote.id) } returns Mono.just(validateCpf(vote.id))

        StepVerifier
            .create(voteService.validateCpf(vote))
            .expectNext(vote)
            .verifyComplete()
    }

    @Test
    fun `validate CPF invalid`() {
        val vote = buildVote(id = getInvalidCpf()).transform(agendaId = "1")

        every { cpfClient.verifyCpf(vote.id) } returns Mono.just(validateCpf(vote.id))

        StepVerifier
            .create(voteService.validateCpf(vote))
            .expectError<ValidationException>()
            .verify()
    }

    @Test
    fun `validate vote with agenda must be valid`() {
        val agenda = buildAgenda(
            begin = now(),
            durationInSeconds = 300,
            votes = setOf(buildVote(id = getValidCpf2(), vote = VoteType.YES))
        )
        val vote = buildVote(id = getValidCpf(), vote = VoteType.YES).transform(agendaId = agenda.id)

        StepVerifier
            .create(voteService.validateVoteAgenda(value = vote, agenda = agenda))
            .expectNext(vote)
            .verifyComplete()
    }

    @Test
    fun `validate vote with agenda must be closed`() {
        val agenda = buildAgenda(votes = setOf(buildVote(id = getValidCpf2(), vote = VoteType.YES)))
        val vote = buildVote(id = getValidCpf(), vote = VoteType.YES).transform(agendaId = agenda.id)

        StepVerifier
            .create(voteService.validateVoteAgenda(value = vote, agenda = agenda))
            .expectError<AgendaClosedException>()
            .verify()
    }

    @Test
    fun `validate vote with agenda must be constrained`() {
        val agenda = buildAgenda(
            begin = now(),
            durationInSeconds = 300,
            topic = "",
            votes = setOf(buildVote(id = getValidCpf2(), vote = VoteType.YES))
        )
        val vote = buildVote(id = getValidCpf(), vote = VoteType.YES).transform(agendaId = agenda.id)

        StepVerifier
            .create(voteService.validateVoteAgenda(value = vote, agenda = agenda))
            .expectError<ValidationException>()
            .verify()
    }

    @Test
    fun `validate vote with agenda must be already voted`() {
        val agenda = buildAgenda(
            begin = now(),
            durationInSeconds = 300,
            votes = setOf(buildVote(id = getValidCpf(), vote = VoteType.YES))
        )
        val vote = buildVote(id = getValidCpf(), vote = VoteType.YES).transform(agendaId = agenda.id)

        StepVerifier
            .create(voteService.validateVoteAgenda(value = vote, agenda = agenda))
            .expectError<AlreadyVotedException>()
            .verify()
    }

}