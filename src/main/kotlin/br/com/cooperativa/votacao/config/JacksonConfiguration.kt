package br.com.cooperativa.votacao.config

import br.com.cooperativa.votacao.util.buildMapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfiguration {

    @Bean
    fun getMapper(): ObjectMapper {
        return buildMapper()
    }
}
