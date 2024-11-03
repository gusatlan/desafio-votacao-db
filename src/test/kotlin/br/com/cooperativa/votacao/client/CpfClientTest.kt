package br.com.cooperativa.votacao.client

import br.com.cooperativa.votacao.controller.CpfController
import br.com.cooperativa.votacao.util.cleanCodeText
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
class CpfClientTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private val controller = mockk<CpfController>(relaxed = true)
    private val client = CpfClient(baseUrl = "http://localhost:8080", endpoint = "/cpf/{cpf}")

    @Test
    fun `should able vote`() {
        val cpf = cleanCodeText("070.680.938-68")

        StepVerifier
            .create(client.verifyCpf(cpf))
            .expectNext(true)
            .expectComplete()
    }

    @Test
    fun `should unable vote`() {
        val cpf = cleanCodeText("11111111111")

        StepVerifier
            .create(client.verifyCpf(cpf))
            .expectNext(false)
            .expectComplete()
    }

}