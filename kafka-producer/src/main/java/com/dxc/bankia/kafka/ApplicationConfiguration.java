/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.kafka;

import java.util.HashMap;
import java.util.Map;

import com.dxc.bankia.model.Event;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 *
 * @author Dennis Federico
 */
@EnableKafka
@Configuration
public class ApplicationConfiguration {

    @Value("${kafka.bootstrap.host:kafka-broker}")
    public String kafkaBootstrapHost;

    @Value("${kafka.bootstrap.port:9092}")
    public String kafkaBootstrapPort;

    @Value("${kafka.producer.topic:DEFAULT}")
    public String kafkaProducerTopic;

    @Value("${kafka.consumer.group:ConsumerGroup}")
    public String kafkaConsumerGroup;

    @Value("${kafka.consumer.topic:DEFAULT}")
    public String kafkaConsumerTopic;

    @Value("${produceCount:100}")
    public int produceCount;

    @Bean
    public KafkaTemplate<String, String> stringMessageProducerKafkaTemplate(ApplicationConfiguration configuration) {
        Map<String, Object> producerProperties = producerProperties(configuration);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        ProducerFactory<String, String> kf = new DefaultKafkaProducerFactory<>(producerProperties);
        KafkaTemplate<String, String> template = new KafkaTemplate<>(kf);
        return template;
    }

    @Bean
    public KafkaTemplate<String, Event> eventProducerKafkaTemplate(ApplicationConfiguration configuration) {
        Map<String, Object> producerProperties = producerProperties(configuration);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ProducerFactory<String, Event> kf = new DefaultKafkaProducerFactory<>(producerProperties);
        KafkaTemplate<String, Event> template = new KafkaTemplate<>(kf);
        return template;
    }

    private Map<String, Object> producerProperties(ApplicationConfiguration configuration) {
        Map<String, Object> producerProperties = new HashMap<>();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.kafkaBootstrapHost + ":" + configuration.kafkaBootstrapPort);
        producerProperties.put(ProducerConfig.RETRIES_CONFIG, 0);
        producerProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        producerProperties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        producerProperties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        return producerProperties;
    }

    @Bean
    @Primary
    public ConsumerFactory kafkaStringConsumerFactory(ApplicationConfiguration configuration) {
        Map<String, Object> consumerProperties = new HashMap<>();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.kafkaBootstrapHost + ":" + configuration.kafkaBootstrapPort);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.kafkaConsumerGroup);
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        consumerProperties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        consumerProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }

    @Bean
    @Qualifier("kafkaEventConsumerFactory")
    public ConsumerFactory<String, Event> kafkaEventConsumerFactory(ApplicationConfiguration configuration) {
        Map<String, Object> consumerProperties = new HashMap<>();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.kafkaBootstrapHost + ":" + configuration.kafkaBootstrapPort);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.kafkaConsumerGroup);
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        consumerProperties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        consumerProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");

        return new DefaultKafkaConsumerFactory<>(consumerProperties, new StringDeserializer(), new JsonDeserializer<>(Event.class));
    }

    @Bean
//    @Primary
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaStringListenerContainerFactory(@Qualifier("kafkaStringConsumerFactory") ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
//        factory.setRecordFilterStrategy(record -> record.value().contains("Wrod")  );
        return factory;
    }

    @Bean
//    @Qualifier("eventListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Event> kafkaEventListenerContainerFactory(@Qualifier("kafkaEventConsumerFactory") ConsumerFactory<String, Event> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Event> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
//        factory.setRecordFilterStrategy(record -> record.value().contains("Wrod")  );
        return factory;
    }

}
