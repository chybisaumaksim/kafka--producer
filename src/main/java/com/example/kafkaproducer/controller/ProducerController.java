package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.dto.Client;
import com.example.kafkaproducer.dto.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProducerController {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper mapper;

    @Value("${kafka.topic.client}")
    private String clientTopic;

    @Value("${kafka.topic.transaction}")
    private String transactionTopic;

    @SneakyThrows
    @PostMapping(path = "/client", produces = APPLICATION_JSON_VALUE)
    public String createClient(@RequestBody Client client) {
        kafkaTemplate.send(clientTopic, client.getClientId()
                .toString(), mapper.writeValueAsString(client));
        log.info("Client information with ID={} has been sent", client.getClientId());
        return "Client created successfully";
    }

    @SneakyThrows
    @PostMapping(path = "/transaction", produces = APPLICATION_JSON_VALUE)
    public String createTransaction(@RequestBody Transaction transaction) {
        kafkaTemplate.send(transactionTopic, transaction.getClientId()
                .toString(), mapper.writeValueAsString(transaction));
        log.info("Transaction information for client with ID={} has been sent", transaction.getClientId());

        return "Transaction created successfully";
    }
}