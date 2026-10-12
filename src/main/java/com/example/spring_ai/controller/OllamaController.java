package com.example.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class OllamaController {

    private final ChatClient chatClient;

    // Spring AI auto-configures the ChatClient.Builder bean
    public OllamaController(OllamaChatModel builder) {
        this.chatClient = ChatClient.create(builder);
    }

    // @GetMapping("/api/chat")
    public String chat(@RequestParam(defaultValue = "Tell me a joke") String message,
            @RequestParam(defaultValue = "default") String conversationId) {

        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .chatResponse();

        System.out.println(chatResponse.getMetadata() + " " + chatResponse.getResult());

        return chatResponse.getResult().getOutput().getText();
    }

    // @GetMapping("/api/ping")
    public String ping() {
        return "It is working";
    }

}
