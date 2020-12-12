package com.jongheon.www.noticeboard.controller;

import com.jongheon.www.noticeboard.cache.MemberCache;
import com.jongheon.www.noticeboard.cipher.SHA256;
import com.jongheon.www.noticeboard.domain.entity.Member;
import com.jongheon.www.noticeboard.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    private static final String memberId = "memberId@naver.com";
    private static final String memberPwd = "memberPwd";
    private static final String changePwd = "changePwd";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCache memberCache;

    @Autowired
    private SHA256 sha256;

    // DB에 저장되어 있는 모든 데이터 삭제
    private void deleteAllData() {
        memberRepository.deleteAll();
        memberCache.cachePreDestroy();
    }

    // DB에 임의의 데이터 넣기
    private void insertMemberData() throws Exception{
        Optional<String> encryptedPwd = sha256.Encrypt(memberId+memberPwd);
        if(encryptedPwd.isEmpty()){
            throw new Exception();
        }
        memberRepository.save(Member.builder().memberId(memberId).memberPwd(encryptedPwd.get()).build());
        memberCache.addNewMember(memberId);
    }

    // TODO : JPA에 실제로 값을 넣지 않도록 하는 방법을 찾아보기
    @BeforeEach
    private void beforeEach() {
        deleteAllData();
    }

    @Test
    void createSuccess() throws Exception {
        // 회원가입 기능이 제대로 되는지 확인
        mockMvc.perform(post("/sign_up")
                .param("member_id", memberId)
                .param("member_pwd", memberPwd))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("SignUp Success"));
    }

    @Test
    public void createFail() throws Exception {
        insertMemberData();

        // 아이디가 중복된 경우 "ID Already Exist", Not Found를 반환하는지 검사
        mockMvc.perform(post("/sign_up")
                .param("member_id", memberId)
                .param("member_pwd", memberPwd))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("ID Already Exist"));
    }

    @Test
    void readSuccess() throws Exception {
        insertMemberData();

        // 로그인 기능이 제대로 수행되는지 검사
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
        insertMemberData();

        // 비밀번호가 다를 경우 "SignIn Fail", Not Found를 반환하는지 검사
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
        insertMemberData();

        // 존재하지 않는 아이디인 경우 "Wrong Member ID", Not Found를 반환하는지 검사
        MvcResult mvcResult = mockMvc.perform(post("/sign_in")
                .param("member_id", "WrongID")
                .param("member_pwd", memberPwd))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wrong Member ID"))
                .andReturn();
    }

    @Test
    void updateSuccess() throws Exception{
        insertMemberData();

        // 비밀번호 변경 성공 시
        MvcResult mvcResult = mockMvc.perform(put("/member/password")
                .param("member_id", memberId)
                .param("member_pwd", memberPwd)
                .param("change_pwd", changePwd))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Change Member Password Success"))
                .andReturn();
    }

    @Test
    void updateFail() throws Exception{
        insertMemberData();

        // 올바르지 않은 비밀번호를 입력한 경우
        mockMvc.perform(put("/member/password")
                .param("member_id", memberId)
                .param("member_pwd", "WrongPwd")
                .param("change_pwd", changePwd))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wrong Member Password"));

        // 올바르지 않은 아이디를 입력한 경우
        mockMvc.perform(put("/member/password")
                .param("member_id", "WrongId")
                .param("member_pwd", memberPwd)
                .param("change_pwd", changePwd))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wrong Member ID"));
    }
}