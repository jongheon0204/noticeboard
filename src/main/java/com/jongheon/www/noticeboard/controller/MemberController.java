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
    public ResponseEntity<String> create(@RequestParam("member_id") final String memberId,
                                         @RequestParam("member_pwd") final String password,
                                         @RequestParam("member_name") final String name){
        return memberService.SignUp(memberId, password, name);
    }

    @PostMapping("sign_in")
    public ResponseEntity<String> read(@RequestParam("member_id") final String memberId,
                                       @RequestParam("member_pwd") final String password){
        return memberService.SignIn(memberId, password);
    }

    @PutMapping("member/password")
    public ResponseEntity<String> update(@RequestParam("member_id") final String memberId,
                                         @RequestParam("member_pwd") final String password,
                                         @RequestParam("change_pwd") final String changePwd){
        return memberService.ModifyUserInfo(memberId, password, changePwd);
    }

}
