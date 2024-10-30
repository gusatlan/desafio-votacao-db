package br.com.cooperativa.votacao.util

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.net.URI

fun buildWebClient(baseUrl: String? = null): WebClient {
    return baseUrl?.let { WebClient.create(it) } ?: WebClient.create()
}

fun handleError(response: ClientResponse): Mono<Throwable> {
    return response.bodyToMono(String::class.java).flatMap { body ->
        val errorMessage = "[ClientRestAPI][STATUS:${response.statusCode()}] [BODY:$body]"
        Mono.error(RuntimeException(errorMessage))
    }
}

fun addHeaders(headersMap: Map<String, String>, headers: HttpHeaders) {
    headersMap.forEach { (key, value) ->
        headers.add(key, value)
    }
}

fun applyParameters(uri: String, parameters: Map<String, String>): URI {
    return UriComponentsBuilder
        .fromUriString(uri)
        .apply {
            parameters.forEach { (key, value) ->
                queryParam(key, value)
            }
        }
        .build(parameters)
        .normalize()
//        .toUri()
}

fun <T : Any?, B:Any?> clientApi(
    webClient: WebClient,
    method: HttpMethod = HttpMethod.GET,
    uri: String,
    body: B? = null,
    parameters: Map<String, String> = emptyMap(),
    headersMap: Map<String, String> = emptyMap(),
    clazz: Class<T>
): Mono<T> {
    val request = webClient
        .method(method)
        .uri(
            applyParameters(
                uri = uri,
                parameters = parameters
            )
        )
        .headers { headers ->
            addHeaders(headersMap, headers)
        }

    if (body != null) {
        request.bodyValue(BodyInserters.fromValue(body))
    }

    return request
        .retrieve()
        .onStatus({ status -> status.is4xxClientError || status.is5xxServerError }) { response ->
            handleError(response)
        }
        .bodyToMono(clazz)
        .doOnNext {
            logger.debug("[RestClientUtils][clientApi] [{}]", it)
        }
}
