package com.jongheon.www.noticeboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jongheon.www.noticeboard.domain.dto.request.PostRequestDto;
import com.jongheon.www.noticeboard.domain.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    private static final String memberId = "memberId@naver.com";
    private static final String title = "title";
    private static final String content = "content";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Test
    void create() throws Exception{
        String ct = objectMapper.writeValueAsString(
                PostRequestDto.builder().memberId(memberId).title(title).content(content).build());

        // 포스팅 기능이 제대로 동작하는지 확인
        mockMvc.perform(post("/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ct))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Post Success"));
    }

    @Test
    void read() throws Exception{

        MvcResult mvcResult = mockMvc.perform(get("/post?id=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void update() throws Exception{
        String ct = objectMapper.writeValueAsString(
                PostRequestDto.builder().id(2L).memberId(memberId).title(title).content("cococo").build());

        mockMvc.perform(put("/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ct))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Post Update Success"));
    }

}