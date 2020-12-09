package com.jongheon.www.noticeboard.controller;

import com.jongheon.www.noticeboard.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("sign_up")
    public ResponseEntity<String> create(@RequestParam("member_id") final String memberId, @RequestParam("member_pwd") final String memberPwd){
        return memberService.SignUp(memberId, memberPwd);
    }

    @PostMapping("sign_in")
    public ResponseEntity<String> read(@RequestParam("member_id") final String memberId, @RequestParam("member_pwd") final String memberPwd){
        return memberService.SignIn(memberId, memberPwd);
    }

    @PutMapping("member/password")
    public ResponseEntity<String> update(@RequestParam("member_id") final String memberId,
                                         @RequestParam("member_pwd") final String memberPwd, @RequestParam("change_pwd") final String changePwd){
        return memberService.ModifyUserInfo(memberId, memberPwd, changePwd);
    }

}
