package com.fraudrisk.fraudriskapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudrisk.fraudriskapi.client.MlClient;
import com.fraudrisk.fraudriskapi.dto.FraudInputDto;
import com.fraudrisk.fraudriskapi.dto.FraudRiskResponseDto;
import com.fraudrisk.fraudriskapi.dto.MlBatchResponseDto;
import com.fraudrisk.fraudriskapi.dto.MlFraudResponseDto;
import com.fraudrisk.fraudriskapi.model.FraudPredictionEntity;
import com.fraudrisk.fraudriskapi.model.enums.PredictionSource;
import com.fraudrisk.fraudriskapi.repository.FraudRiskPredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudRiskService {

    private final MlClient mlClient;
    private final FraudRiskPredictionRepository fraudRiskPredictionRepository;
    private final ObjectMapper objectMapper;

    /** SINGLE: chama ML, persiste, retorna. */
    public FraudRiskResponseDto predict(FraudInputDto input) {

        MlFraudResponseDto ml = mlClient.predict(input);

        String inputJson = toJson(input);

        FraudPredictionEntity entity = FraudPredictionEntity.of(
                inputJson,
                ml.probFraud(),
                ml.fraudLabel(),
                ml.modelVersion(),
                ml.thresholdOptimal(),
                PredictionSource.API_SINGLE
        );

        fraudRiskPredictionRepository.save(entity);

        return new FraudRiskResponseDto(
                ml.probFraud(),
                ml.fraudLabel(),
                ml.modelVersion(),
                ml.thresholdOptimal()
        );
    }

    /** BATCH JSON: chama ML e retorna (não persiste). */
    public MlBatchResponseDto predictBatch(List<FraudInputDto> items) {
        return mlClient.predictBatchJson(items);
    }

    /** CSV: chama ML e retorna (não persiste). */
    public MlBatchResponseDto predictBatchCsv(MultipartFile file) throws IOException {
        return mlClient.predictBatchCsv(file.getBytes(), file.getOriginalFilename());
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize input to JSON", e);
        }
    }
}
