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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO : 변경된 MemberService에 맞게 Test도 업데이트 하기
// TODO : yml 혹은 properties 파일을 통해 상수 값 사용하기
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    private static final String memberId = "memberId@naver.com";
    private static final String password = "password";
    private static final String changePwd = "changedPwd";
    private static final String name = "name";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCache memberCache;

    @Autowired
    private SHA256 sha256;

    // DB에 임의의 데이터 넣기
    private boolean insertMemberData(){
        return sha256.Encrypt(memberId + password)
                .map(encryptedPwd -> {
                    Member newMember = Member.builder().memberId(memberId).password(password).loginFailCnt(0).build();
                    memberCache.addNewMember(newMember);
                    return true;
                }).orElse(false);
    }

    // TODO : JPA에 실제로 값을 넣지 않도록 하는 방법을 찾아보기
    @BeforeEach
    private void beforeEach() {
        memberCache.cachePreDestroy();
    }

    @Test
    void createSuccess() throws Exception {
        // 회원가입 기능이 제대로 되는지 확인
        mockMvc.perform(post("/sign_up")
                .param("member_id", memberId)
                .param("member_pwd", password)
                .param("member_name", name))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("회원가입에 성공하였습니다"));
    }

    @Test
    public void createFail() throws Exception {
        insertMemberData();

        // 아이디가 중복된 경우 "ID Already Exist", Not Found를 반환하는지 검사
        mockMvc.perform(post("/sign_up")
                .param("member_id", memberId)
                .param("member_pwd", password)
                .param("member_name", name))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("존재하는 아이디 입니다"));
    }

    @Test
    void readSuccess() throws Exception {
        insertMemberData();

        // 로그인 기능이 제대로 수행되는지 검사
        MvcResult mvcResult = mockMvc.perform(post("/sign_in")
                .param("member_id", memberId)
                .param("member_pwd", password))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("로그인 성공"))
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
                .andExpect(content().string("로그인 실패"))
                .andReturn();

    }

    @Test
    void readIdFail() throws Exception {
        insertMemberData();

        // 존재하지 않는 아이디인 경우 "Wrong Member ID", Not Found를 반환하는지 검사
        MvcResult mvcResult = mockMvc.perform(post("/sign_in")
                .param("member_id", "WrongID")
                .param("member_pwd", password))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("존재하지 않는 아이디 입니다"))
                .andReturn();
    }

    @Test
    void updateSuccess() throws Exception{
        insertMemberData();

        // 비밀번호 변경 성공 시
        MvcResult mvcResult = mockMvc.perform(put("/member/password")
                .param("member_id", memberId)
                .param("member_pwd", password)
                .param("change_pwd", changePwd))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("비밀번호 변경 성공"))
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
                .andExpect(content().string("비밀번호 오류"));

        // 올바르지 않은 아이디를 입력한 경우
        mockMvc.perform(put("/member/password")
                .param("member_id", "WrongId")
                .param("member_pwd", password)
                .param("change_pwd", changePwd))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("존재하지 않는 아이디 입니다"));
    }
}