package com.example.fuzzharnessautogenerator.controller;



import com.example.fuzzharnessautogenerator.dto.request.GenerateRequest;


import com.example.fuzzharnessautogenerator.dto.response.RootResponse;
import com.example.fuzzharnessautogenerator.service.GenerateService;
import com.example.fuzzharnessautogenerator.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("gpt")
public class restController {
    private final GenerateService generateService;

    @PostMapping("harness")   // fcm 토큰, device token( 불필요 )
    public ResponseEntity<RootResponse> getHarnessBody(@RequestBody GenerateRequest request) {
//        GenerateResponse response=generateService.generateText(request.getMethod(),request.getProgram().toString(), 1.0f, 1000);
       // log.info("gpt ans: "+generateService.generateText(request.getMethod(),request.getProgram().toString(), 1.0f, 1000));
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, generateService.generateText(request.getMethod(),request.getProgram().toString(),"1.1.1", 1.0f, 1000), null);
    }
}

