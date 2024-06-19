package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.dto.Client;
import com.example.kafkaproducer.dto.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/create")
public class ProducerController {

    private static final String CLIENT_TOPIC = "client-topic";
    private static final String TRANSACTION_TOPIC = "transaction-topic";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping(path = "/client", produces = APPLICATION_JSON_VALUE)
    public String createClient(@RequestBody Client client) {
        kafkaTemplate.send(CLIENT_TOPIC, client.getClientId()
                .toString(), client);
        log.info("Client information with ID : {} has been sent", client.getClientId());
        return "Client created successfully";
    }

    @PostMapping(path = "/transaction", produces = APPLICATION_JSON_VALUE)
    public String createTransaction(@RequestBody Transaction transaction) {
        kafkaTemplate.send(TRANSACTION_TOPIC, transaction.getClientId()
                .toString(), transaction);
        log.info("Transaction info for client with id: [{}] was send", transaction.getClientId());

        return "Transaction created successfully";
    }
}