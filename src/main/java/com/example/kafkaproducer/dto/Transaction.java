package com.example.kafkaproducer.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class Transaction {
    @NotEmpty
    private String bank;
    @NotNull
    private Long clientId;
    @NotEmpty
    private TransactionType transactionType;
    @NotNull
    private Integer quantity;
    @NotNull
    private Double price;
    @NotNull
    private LocalDateTime createdAt;
}
