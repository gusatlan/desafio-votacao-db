package br.com.cooperativa.votacao.client

import br.com.cooperativa.votacao.domain.dto.VoteAbleType
import br.com.cooperativa.votacao.util.createLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class CpfClient(
    @Value("\${cpf.baseUrl}") private val baseUrl: String,
    @Value("\${cpf.endpoint}") private val endpoint: String
) {

    companion object {
        val logger = createLogger(this::class.java)
    }

    private fun getWebClient(): WebClient {
        return WebClient.builder().baseUrl(baseUrl).build()
    }

    private fun handleError(response: ClientResponse): Mono<Throwable> {
        return response.bodyToMono(String::class.java).flatMap { body ->
            val errorMessage = "[CpfClient][STATUS:${response.statusCode()}] [BODY:$body]"
            Mono.error(RuntimeException(errorMessage))
        }
    }

    @Caching
    fun verifyCpf(cpf: String): Mono<Boolean> {
        logger.info("[CpfClient][verifyCpf][BEGIN] [$cpf]")

        return getWebClient()
            .get()
            .uri { uriBuilder ->
                uriBuilder.path(endpoint).build(cpf)
            }
            .retrieve()
            .onStatus({ status -> status.is5xxServerError }) { response ->
                handleError(response)
            }
            .bodyToMono(VoteAbleType::class.java)
            .map(VoteAbleType::value)
            .doOnNext {
                logger.info("[CpfClient][verifyCpf][VERIFY] [$cpf, $it]")
            }
            .cache()
            .also {
                logger.info("[CpfClient][verifyCpf][END] [$cpf]")
            }
    }
}