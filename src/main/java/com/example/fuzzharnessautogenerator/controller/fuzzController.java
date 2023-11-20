package com.example.fuzzharnessautogenerator.controller;

import com.example.fuzzharnessautogenerator.dto.request.GenerateRequest;
import com.example.fuzzharnessautogenerator.service.GenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@Controller
@Log4j2
@RequiredArgsConstructor
public class fuzzController {
    private final GenerateService generateService;

    @RequestMapping("/main")
    public String examplePage() {
        return "main";
    }

    //sync
//    @PostMapping("/submit")
//    public String submitForm(
//            @RequestParam("program") String program,
//            @RequestParam("method") String method,
//            Model model
//    ) {
//        log.info("shyswy 2 !!!");
//        //String result="successshyswy";
//        String result = generateService.generateText(method, program, 1.0f, 1000).getHarness();
//        model.addAttribute("result", result);
//        return "main";
//        //return "redirect:/main";
//    }


    //async
    @PostMapping("/submit")
    public Mono<String> submitForm(
            @RequestParam("program") String program,
            @RequestParam("method") String method,
            @RequestParam("version") String version,
            Model model
    ) {
        return generateService.generateText(method, program,version, 1.0f, 1000)
                .map(response -> {
                    model.addAttribute("result", response.getHarness());
                    return "main";
                })
                .onErrorResume(e -> Mono.just("error")); // Handle errors if necessary
    }

}
