package com.fraudrisk.fraudriskapi.controller;

import com.fraudrisk.fraudriskapi.dto.FraudInputDto;
import com.fraudrisk.fraudriskapi.dto.FraudRiskResponseDto;
import com.fraudrisk.fraudriskapi.dto.MlBatchResponseDto;
import com.fraudrisk.fraudriskapi.service.FraudRiskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
public class FraudRiskController {

    private final FraudRiskService fraudRiskService;

    @PostMapping("/predict")
    public FraudRiskResponseDto predict(@Valid @RequestBody FraudInputDto input) {
        return fraudRiskService.predict(input);
    }

    @PostMapping("/predict/batch")
    public MlBatchResponseDto predictBatch(@Valid @RequestBody List<FraudInputDto> items) {
        return fraudRiskService.predictBatch(items);
    }

    @PostMapping(value = "/predict/batch/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MlBatchResponseDto predictBatchCsv(@RequestPart("file") MultipartFile file) throws IOException {
        return fraudRiskService.predictBatchCsv(file);
    }
}
