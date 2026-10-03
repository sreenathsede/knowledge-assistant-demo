package com.sreenath.knowledge.controller;

import com.sreenath.knowledge.model.*;
import com.sreenath.knowledge.service.KnowledgeAssistantService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class KnowledgeController {
    private final KnowledgeAssistantService service;

    public KnowledgeController(KnowledgeAssistantService service) {
        this.service = service;
    }

    @PostMapping("/ask")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return service.ask(request);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String message) {
        return Flux.just("Knowledge Assistant demo: use POST /api/ask for grounded answers.\n",
                "Question received: " + message + "\n");
    }
}
