package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.dto.Client;
import com.example.kafkaproducer.dto.Transaction;
import com.example.kafkaproducer.dto.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProducerControllerIntegrationTest {

    private static final String CLIENT_TOPIC = "client-topic";
    private static final String TRANSACTION_TOPIC = "transaction-topic";

    private static KafkaContainer kafkaContainer;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ProducerController producerController;

    @Autowired
    private ObjectMapper mapper;

    private static KafkaConsumer<String, Object> consumer;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @SneakyThrows
    @BeforeAll
    static void setUp() {
        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
        kafkaContainer.start();

        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "testGroup");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        consumerProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, StringDeserializer.class.getName());
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.kafkaproducer.dto");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(List.of(CLIENT_TOPIC, TRANSACTION_TOPIC));
        KafkaTestUtils.getRecords(consumer);
    }

    @AfterAll
    static void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
        if (kafkaContainer != null) {
            kafkaContainer.stop();
        }
    }

    @SneakyThrows
    @Test
    void testCreateClient() {
        Client client = new Client(1L, "email@gmail.com");
        producerController.createClient(client);
        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(consumer, CLIENT_TOPIC);
        assertThat(singleRecord).isNotNull();
        assertThat(singleRecord.value()
                .toString()).isEqualTo(mapper.writeValueAsString(client));
    }

    @SneakyThrows
    @Test
    void testCreateTransaction() {
        Transaction transaction = new Transaction("bank", 1L, TransactionType.INCOME, 20, 10.0, LocalDateTime.now());

        producerController.createTransaction(transaction);
        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(consumer, TRANSACTION_TOPIC);
        assertThat(singleRecord).isNotNull();
        assertThat(singleRecord.value()
                .toString()).isEqualTo(mapper.writeValueAsString(transaction));
    }
}