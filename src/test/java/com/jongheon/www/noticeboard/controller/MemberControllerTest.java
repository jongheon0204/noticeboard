package com.jongheon.www.noticeboard.controller;

import com.jongheon.www.noticeboard.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    private static final String memberId = "memberId";
    private static final String memberPwd = "memberPwd";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    private void deleteAllData() {
        memberRepository.deleteAll();
    }

    // TODO : JPA에 실제로 값을 넣지 않도록 하는 방법을 찾아보기
    @BeforeEach
    private void beforeEach() {
        deleteAllData();
    }

    @Test
    void createSuccess() throws Exception {
        // 아이디가 중복 되지 않은 경우 OK가 반환되는지 검사
        mockMvc.perform(post("/sign_up")
                .param("member_id", memberId)
                .param("member_pwd", memberPwd))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("SignUp Success"));
    }

    @Test
    public void createFail() throws Exception {
        // 아이디가 중복 되지 않은 경우 OK가 반환되는지 검사
        mockMvc.perform(post("/sign_up")
                .param("member_id", memberId)
                .param("member_pwd", memberPwd))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("SignUp Success"));

        // 아이디가 중복된 경우 NOT FOUND가 반환되는지 검사
        mockMvc.perform(post("/sign_up")
                .param("member_id", memberId)
                .param("member_pwd", memberPwd))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("ID Already Exist"));
    }

    @Test
    void readSuccess() throws Exception {
        // 아이디가 중복 되지 않은 경우 OK가 반환되는지 검사
        mockMvc.perform(post("/sign_up")
                .param("member_id", memberId)
                .param("member_pwd", memberPwd))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("SignUp Success"));

        // DB에서 데이터를 제대로 읽어오는지 검사
        MvcResult mvcResult = mockMvc.perform(post("/sign_in")
                .param("member_id", memberId)
                .param("member_pwd", memberPwd))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("SignIn Success"))
                .andReturn();
    }

    @Test
    void readLoginFail() throws Exception {
        // 아이디가 중복 되지 않은 경우 OK가 반환되는지 검사
        mockMvc.perform(post("/sign_up")
                .param("member_id", memberId)
                .param("member_pwd", memberPwd))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("SignUp Success"));

        // DB에서 데이터를 제대로 읽어오는지 검사
        MvcResult mvcResult = mockMvc.perform(post("/sign_in")
                .param("member_id", memberId)
                .param("member_pwd", "WrongPwd"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("SignIn Fail"))
                .andReturn();

    }

    @Test
    void readIdFail() throws Exception {
        // 아이디가 중복 되지 않은 경우 OK가 반환되는지 검사
        mockMvc.perform(post("/sign_up")
                .param("member_id", memberId)
                .param("member_pwd", memberPwd))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("SignUp Success"));

        // DB에서 데이터를 제대로 읽어오는지 검사
        MvcResult mvcResult = mockMvc.perform(post("/sign_in")
                .param("member_id", "WrongID")
                .param("member_pwd", memberPwd))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wrong Member ID"))
                .andReturn();
    }
}