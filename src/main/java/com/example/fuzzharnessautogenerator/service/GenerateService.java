package com.example.fuzzharnessautogenerator.service;

import com.example.fuzzharnessautogenerator.dto.response.GenerateResponse;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
@RequiredArgsConstructor
@Log4j2
public class GenerateService {

    @Value("${spring.gpt.apikey}")
    private String API_KEY;
    private static final String ENDPOINT = "https://api.openai.com/v1/completions";


    //async
    public Mono<GenerateResponse> generateText(String method, String program,String version, float temperature, int maxTokens) {
        return Mono.defer(() -> {
            try {
                Path promptFilePath = Paths.get("src/main/resources/prompt.txt");
                String prompt = Files.readString(promptFilePath);
                prompt = prompt.replace("[target Method Data]", method)
                        .replace("[target Program Data]", program)
                        .replace("[Version]",version);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + API_KEY);

                WebClient client = WebClient.create();

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "text-davinci-003");
                requestBody.put("prompt", prompt);
                requestBody.put("temperature", temperature);
                requestBody.put("max_tokens", maxTokens);

                return client.post()
                        .uri(ENDPOINT)
                        .headers(httpHeaders -> httpHeaders.addAll(headers))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(requestBody))
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(responseBody -> {
                            try {
                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode jsonNode = objectMapper.readTree(responseBody);
                                String generatedText = jsonNode.get("choices").get(0).get("text").asText();
                               // log.info("shyswy===\n" + generatedText);
                                return Mono.just(GenerateResponse.builder().harness(generatedText).build());
                            } catch (IOException e) {
                                log.error("Error reading response body", e);
                                return Mono.error(new RuntimeException("Error reading response body", e));
                            }
                        })
                        .onErrorResume(WebClientResponseException.TooManyRequests.class, e ->
                                Mono.delay(Duration.ofSeconds(1))
                                        .then(Mono.error(new RuntimeException("Too Many Requests")))
                        );
            } catch (IOException e) {
                log.error("Error reading prompt file", e);
                return Mono.error(new RuntimeException("Error reading prompt file", e));
            }
        });
    }


//sync
//    public GenerateResponse generateText(String method, String program, float temperature, int maxTokens) {
//        try {
//            Path promptFilePath = Paths.get("src/main/resources/prompt.txt");
//            // txt 파일에서 프롬프트 읽어오기
//            String prompt = Files.readString(promptFilePath);
//
//            // [target Method Data] 및 [target Program Data] 값으로 대체
//            prompt = prompt.replace("[target Method Data]", method)
//                    .replace("[target Program Data]", program);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("Authorization", "Bearer " + API_KEY);
//
//            WebClient client = WebClient.create();
//
//            Map<String, Object> requestBody = new HashMap<>();
//            requestBody.put("model", "text-davinci-003");
//            requestBody.put("prompt", prompt);
//            requestBody.put("temperature", temperature);
//            requestBody.put("max_tokens", maxTokens);
//
//            Mono<String> responseMono = client.post()
//                    .uri(ENDPOINT)
//                    .headers(httpHeaders -> httpHeaders.addAll(headers))
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(BodyInserters.fromValue(requestBody))
//                    .retrieve()
//                    .bodyToMono(String.class);
//
//            String responseBody = responseMono.block(); // block until the response is available
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(responseBody);
//
//            // Extracting generated text from the "text" field in the first choice
//            String generatedText = jsonNode.get("choices").get(0).get("text").asText();
//            //generatedText = generatedText.replace("\r\n", "");
//            //generatedText = generatedText.replace("\r\n", System.lineSeparator());
//
//           log.info("shyswy===\n"+generatedText);
//
//            return GenerateResponse.builder().harness(generatedText).build();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return GenerateResponse.builder().harness("Error reading prompt file").build();
//        } catch (WebClientResponseException.TooManyRequests e) {
//            // Handle 429 error
//            try {
//                Thread.sleep(1000); // 1000 milliseconds (1 second) 대기 후 다시 시도
//                return GenerateResponse.builder().harness("Too Many Request").build();
//            } catch (InterruptedException ex) {
//                throw new RuntimeException(ex);
//            }
//        }
//    }

}
