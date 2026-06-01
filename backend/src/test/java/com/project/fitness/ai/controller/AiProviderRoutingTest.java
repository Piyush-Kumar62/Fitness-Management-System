package com.project.fitness.ai.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "ai.provider=GEMINI",
    "ai.gemini.enabled=true"
})
class AiProviderRoutingTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean(name = "ollamaChatClient", answer = Answers.RETURNS_DEEP_STUBS)
  private ChatClient ollamaChatClient;

  @MockBean(name = "geminiChatClient", answer = Answers.RETURNS_DEEP_STUBS)
  private ChatClient geminiChatClient;

  @BeforeEach
  void setup() {
    when(geminiChatClient.prompt().user(anyString()).call().content())
        .thenReturn("Gemini reply");
  }

  @Test
  @WithMockUser(username = "test-user")
  void chatRoutesToGeminiProviderWhenConfigured() throws Exception {
    String requestJson = "{\"prompt\":\"Route to gemini\"}";

    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ai/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.reply").value("Gemini reply"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.provider").value("gemini"));

    verify(geminiChatClient, times(1)).prompt();
    verify(ollamaChatClient, never()).prompt();
  }
}
