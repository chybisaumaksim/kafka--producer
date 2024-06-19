package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.dto.Client;
import com.example.kafkaproducer.dto.Transaction;
import com.example.kafkaproducer.dto.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ProducerController.class)
class ProducerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createClient() throws Exception {
        var client = new Client();
        client.setClientId(1L);
        client.setEmail("someMail@gmail.com");

        mockMvc.perform(post("/client").contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isOk());

        verify(kafkaTemplate).send("client-topic", client.getClientId()
                .toString(), objectMapper.writeValueAsString(client));
    }

    @Test
    public void createTransaction() throws Exception {
        var transaction = Transaction.builder()
                .clientId(1L)
                .createdAt(LocalDateTime.now())
                .price(10.0)
                .bank("PKO")
                .transactionType(TransactionType.INCOME)
                .quantity(10)
                .build();

        mockMvc.perform(post("/transaction").contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk());

        verify(kafkaTemplate).send("transaction-topic", transaction.getClientId()
                .toString(), objectMapper.writeValueAsString(transaction));
    }
}