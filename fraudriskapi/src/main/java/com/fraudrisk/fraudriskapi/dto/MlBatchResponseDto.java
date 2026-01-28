package com.fraudrisk.fraudriskapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MlBatchResponseDto(
        BatchMeta meta,
        List<FraudBatchItemDto> results
) {
    public record BatchMeta(
            @JsonProperty("model_version")
            String modelVersion,
            int total,
            @JsonProperty("threshold_optimal") Double thresholdOptimal // pode ser null
    ) {}

    public record FraudBatchItemDto(
            double probability,
            String label
    ) {}
}
