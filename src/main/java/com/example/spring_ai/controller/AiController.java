package com.example.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class AiController {

    private final ChatClient chatClient;

    @Autowired
    private EmbeddingModel embeddingModel;

    // Inject the Gemini model directly: with both Gemini and Ollama on the
    // classpath,
    // the auto-configured ChatClient.Builder can't pick a single ChatModel
    public AiController(GoogleGenAiChatModel chatModel) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @GetMapping("/api/chat")
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

    @GetMapping("/api/ping")
    public String ping() {
        return "It is working";
    }

    @PostMapping("/api/embedding")
    public float[] embedding(@RequestParam String text) {
        return embeddingModel.embed(text);
    }
}