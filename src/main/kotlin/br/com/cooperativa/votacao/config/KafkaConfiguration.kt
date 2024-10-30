package br.com.cooperativa.votacao.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
@EnableKafka
class KafkaConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}") private val bootStrapServers: String,
    @Value("\${spring.kafka.consumer.group-id}") private val consumerId: String,
    @Value("\${spring.kafka.consumer.auto-offset-reset-config:latest}") private val autoOffset: String,

    @Value("\${spring.kafka.consumer.key-deserializer:}")
    private val keyDeserializer: String,
    @Value("\${spring.kafka.consumer.value-deserializer:}")
    private val valueDeserializer: String,

    @Value("\${spring.kafka.producer.key-serializer:}")
    private val keySerializer: String,
    @Value("\${spring.kafka.producer.value-serializer:}")
    private val valueSerializer: String
) {

    private val keySer = keySerializer.ifBlank { StringSerializer::class.java }
    private val keyDes = keyDeserializer.ifBlank { StringDeserializer::class.java }
    private val valueSer = valueSerializer.ifBlank { StringSerializer::class.java }
    private val valueDes = valueDeserializer.ifBlank { StringDeserializer::class.java }

    fun consumerFactory(): ConsumerFactory<String, String> {
        val config: Map<String, Any> = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootStrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to consumerId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to keyDes,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to valueDes,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to autoOffset
        )

        return DefaultKafkaConsumerFactory(config)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val config: Map<String, Any> = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootStrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to keySer,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to valueSer
        )
        return DefaultKafkaProducerFactory(config)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }
}
