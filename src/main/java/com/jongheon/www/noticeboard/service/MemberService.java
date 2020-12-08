package com.jongheon.www.noticeboard.service;

import com.jongheon.www.noticeboard.domain.entity.Member;
import com.jongheon.www.noticeboard.domain.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    /**
     * @return
     * 회원가입 성공 시 "SignUp Success"
     * 아이디 중복 시 "ID Already Exist"
     */
    public ResponseEntity<String> SignUp(final String memberId, final String memberPwd) {
        Optional<Member> member = memberRepository.findById(memberId);
        if(member.isPresent()){
            return new ResponseEntity<>("ID Already Exist", HttpStatus.NOT_FOUND);
        }

        memberRepository.save(Member.builder()
                .memberId(memberId).memberPwd(memberPwd).build());

        return new ResponseEntity<>("SignUp Success", HttpStatus.OK);
    }

    /**
     *  @return
     *  비밀번호가 일치하는 경우 "SignIn Success"
     *  비밀번호가 다른 경우 "SignIn Fail"
     *  멤버 아이디가 잘못된 경우 "Wrong Member ID"
     */
    public ResponseEntity<String> SignIn(final String memberId, final String memberPwd) {
        Optional<Member> member = memberRepository.findById(memberId);

        return member.map(mem -> {
            if(mem.getMemberPwd().equals(memberPwd)){
                return new ResponseEntity<>("SignIn Success", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("SignIn Fail", HttpStatus.NOT_FOUND);
            }
        }).orElseGet(() -> new ResponseEntity<>("Wrong Member ID", HttpStatus.NOT_FOUND));
    }
}
