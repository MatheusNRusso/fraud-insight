package com.fraudrisk.fraudriskapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MlFraudResponseDto(
        @JsonProperty("prob_fraud") double probFraud,
        @JsonProperty("fraud_label") String fraudLabel,
        @JsonProperty("model_version") String modelVersion,
        @JsonProperty("threshold_optimal") double thresholdOptimal
) {}
