package com.fraudrisk.fraudriskapi.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudrisk.fraudriskapi.dto.FraudInputDto;
import com.fraudrisk.fraudriskapi.dto.MlBatchResponseDto;
import com.fraudrisk.fraudriskapi.dto.MlFraudResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MlClient {

    private final WebClient mlWebClient;
    private final ObjectMapper objectMapper;

    public MlFraudResponseDto predict(FraudInputDto dto) {
        try {
            System.out.println("JSON ENVIADO PARA PYTHON:");
            System.out.println(objectMapper.writeValueAsString(dto));

            return mlWebClient.post()
                    .uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(MlFraudResponseDto.class)
                    .block();

        } catch (WebClientResponseException e) {
            System.out.println("ML STATUS: " + e.getStatusCode());
            System.out.println("ML BODY: " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** JSON batch -> POST /predict/batch */
    public MlBatchResponseDto predictBatchJson(List<FraudInputDto> items) {
        try {
            System.out.println("JSON BATCH ENVIADO PARA PYTHON:");
            System.out.println(objectMapper.writeValueAsString(items));

            return mlWebClient.post()
                    .uri("/predict/batch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(items)
                    .retrieve()
                    .bodyToMono(MlBatchResponseDto.class)
                    .block();

        } catch (WebClientResponseException e) {
            System.out.println("ML STATUS: " + e.getStatusCode());
            System.out.println("ML BODY: " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** CSV multipart -> POST /predict/batch/csv */
    public MlBatchResponseDto predictBatchCsv(byte[] csvBytes, String filename) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("file", new ByteArrayResource(csvBytes) {
                    @Override
                    public String getFilename() { return filename; }
                })
                .contentType(MediaType.TEXT_PLAIN);

        return mlWebClient.post()
                .uri("/predict/batch/csv")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(MlBatchResponseDto.class)
                .block();
    }
}
