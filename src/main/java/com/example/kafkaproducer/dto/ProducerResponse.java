package com.example.kafkaproducer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProducerResponse {
    private String message;
}