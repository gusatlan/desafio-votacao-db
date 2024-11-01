package br.com.cooperativa.votacao.controller

import br.com.cooperativa.votacao.domain.dto.VoteAbleType
import br.com.cooperativa.votacao.util.cleanCodeText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CpfControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `should returns able`() {
        val cpf = cleanCodeText("070.680.938-68")
        webTestClient.get()
            .uri("/cpf/$cpf")
            .exchange()
            .expectStatus().isOk
            .expectBody<VoteAbleType>()
            .consumeWith { response ->
                val responseBody = response.responseBody
                assertNotNull(responseBody)
                assertEquals(VoteAbleType.ABLE_TO_VOTE, responseBody)
                if (responseBody != null) {
                    assertTrue(responseBody.value)
                }
            }
    }

    @Test
    fun `should status 404`() {
        webTestClient.get()
            .uri("/cpf/11111111111")
            .exchange()
            .expectStatus().isNotFound
            .expectBody<VoteAbleType>()
            .consumeWith { response ->
                val responseBody = response.responseBody

                assertNotNull(responseBody)
                assertEquals(VoteAbleType.UNABLE_TO_VOTE, responseBody)
                assertFalse(responseBody!!.value)
            }
    }
}