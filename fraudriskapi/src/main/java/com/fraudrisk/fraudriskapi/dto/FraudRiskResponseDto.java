package com.fraudrisk.fraudriskapi.dto;

public record FraudRiskResponseDto(
        double probFraud,
        String fraudLabel,
        String modelVersion,
        double thresholdOptimal
) {}
