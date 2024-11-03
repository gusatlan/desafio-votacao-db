package br.com.cooperativa.votacao.service

import br.com.cooperativa.votacao.domain.persist.AgendaPersist
import br.com.cooperativa.votacao.exception.EntityFoundException
import br.com.cooperativa.votacao.exception.ValidationException
import br.com.cooperativa.votacao.mapper.transform
import br.com.cooperativa.votacao.repository.AgendaRepository
import br.com.cooperativa.votacao.util.toJson
import br.com.cooperativa.votacao.utils.buildAgenda
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.kafka.core.KafkaTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.test.expectError
import reactor.test.StepVerifier

class AgendaServiceTest {

    private val repository = mockk<AgendaRepository>(relaxed = true)
    private val kafkaTemplate = mockk<KafkaTemplate<String, String>>(relaxed = true)
    private val service = AgendaService(repository = repository, kafkaTemplate = kafkaTemplate)

    @Test
    fun `should Agenda not exist`() {
        every { repository.existsById(any<String>()) } returns Mono.just(false)

        StepVerifier
            .create(service.exists("123"))
            .expectNext(false)
            .expectComplete()
    }

    @Test
    fun `should Agenda exist`() {
        every { repository.existsById(any<String>()) } returns Mono.just(true)

        StepVerifier
            .create(service.exists("123"))
            .expectNext(true)
            .expectComplete()
    }

    @Test
    fun `should validate persist not valid`() {
        every { repository.existsById(any<String>()) } returns Mono.just(true)

        StepVerifier
            .create(service.validatePersist(buildAgenda().transform()))
            .expectError<EntityFoundException>()
            .verify()
    }

    @Test
    fun `should validate persist valid`() {
        val obj = buildAgenda().transform()

        every { repository.existsById(any<String>()) } returns Mono.just(false)

        StepVerifier
            .create(service.validatePersist(obj))
            .expectNext(obj)
            .verifyComplete()
    }

    @Test
    fun `should find`() {
        val obj = buildAgenda()

        every { repository.findById(any<String>()) } returns Mono.just(obj)
        every { repository.findByTopicContainsIgnoreCase(any()) } returns Flux.just(obj)
        every { repository.findByDescriptionContainsIgnoreCase(any()) } returns Flux.just(obj)

        StepVerifier
            .create(service.find(id = obj.id))
            .expectNext(obj)
            .verifyComplete()

        StepVerifier
            .create(service.find(topic = obj.topic))
            .expectNext(obj)
            .verifyComplete()

        StepVerifier
            .create(service.find(description = obj.description))
            .expectNext(obj)
            .verifyComplete()
    }

    @Test
    fun `should not find`() {
        val obj = buildAgenda(id = "123", topic = "topic", description = "description")

        every { repository.findById(obj.id) } returns Mono.just(obj)
        every { repository.findById("456") } returns Mono.empty()

        StepVerifier
            .create(service.find(id = obj.id))
            .expectNext(obj)
            .verifyComplete()

        StepVerifier
            .create(service.find(id = "456"))
            .expectNextCount(0)
            .verifyComplete()

        StepVerifier
            .create(service.find(id = obj.id, topic = "abc", description = "def"))
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun `should save AgendaPersist`() {
        val obj = buildAgenda()

        every { repository.save(any()) } returns Mono.just(obj)

        StepVerifier
            .create(service.save(obj))
            .expectNext(obj)
            .verifyComplete()
    }

    @Test
    fun `should save AgendaDTO`() {
        val obj = buildAgenda()

        every { repository.existsById(any<String>()) } returns Mono.just(false)
        every { repository.save(any()) } returns Mono.just(obj)

        StepVerifier
            .create(service.save(obj.transform()))
            .expectNext(obj.transform())
            .verifyComplete()
    }

    @Test
    fun `should not save AgendaDTO`() {
        val obj = buildAgenda()

        every { repository.existsById(any<String>()) } returns Mono.just(true)
        every { repository.save(any()) } returns Mono.just(obj)

        StepVerifier
            .create(service.save(obj.transform()))
            .expectError<EntityFoundException>()
            .verify()
    }

    @Test
    fun `should not valid save AgendaDTO`() {
        val obj = buildAgenda(id = "", topic = "", description = "")

        every { repository.existsById(any<String>()) } returns Mono.just(false)
        every { repository.save(any()) } returns Mono.just(obj)

        StepVerifier
            .create(service.save(obj.transform()))
            .expectError<ValidationException>()
            .verify()
    }

    @Test
    fun `should not consume`() {
        every { repository.save(any<AgendaPersist>()) } throws Exception()

        assertDoesNotThrow { service.consumer(message = null) }
    }

    @Test
    fun `should consume not complete`() {
        val persist = buildAgenda(id = "123")
        val dto = persist.transform()
        val message = toJson(dto)

        every { repository.existsById(any<String>()) } returns Mono.just(true)

        assertDoesNotThrow { service.consumer(message = message) }
    }

    @Test
    fun `should consume complete`() {
        val persist = buildAgenda(id = "123")
        val dto = persist.transform()
        val message = toJson(dto)

        every { repository.existsById(any<String>()) } returns Mono.just(false)
        every { repository.findById(any<String>()) } returns Mono.empty()
        every { repository.save(any<AgendaPersist>()) } returns Mono.just(persist)

        assertDoesNotThrow { service.consumer(message = message) }
    }

    @Test
    fun `should send AgendaDTO to persist`() {
        val obj = buildAgenda().transform()

        StepVerifier
            .create(service.send(obj))
            .expectNext(obj)
            .expectComplete()
    }

}