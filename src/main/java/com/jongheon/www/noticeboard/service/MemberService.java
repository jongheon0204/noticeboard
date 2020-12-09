package com.jongheon.www.noticeboard.service;

import com.jongheon.www.noticeboard.cipher.SHA256;
import com.jongheon.www.noticeboard.domain.entity.Member;
import com.jongheon.www.noticeboard.domain.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SHA256 sha256;

    /**
     *
     * @return
     * 회원가입 성공 시 "SignUp Success"
     * 비밀번호 SHA256 암호화 에러시 "Encrypt Error"
     * 아이디 중복 시 "ID Already Exist"
     */
    public ResponseEntity<String> SignUp(final String memberId, final String memberPwd) {
        Optional<Member> member = memberRepository.findById(memberId);
        if(member.isPresent()){
            return new ResponseEntity<>("ID Already Exist", HttpStatus.NOT_FOUND);
        }

        Optional<String> encryptedPwd = sha256.Encrypt(memberId+memberPwd);
        if(encryptedPwd.isEmpty()){
            return new ResponseEntity<>("Encrypt Error", HttpStatus.NOT_FOUND);
        }

        memberRepository.save(Member.builder()
                .memberId(memberId).memberPwd(encryptedPwd.get()).build());

        return new ResponseEntity<>("SignUp Success", HttpStatus.OK);
    }

    /**
     * 아이디 값을 통해 멤버 정보를 찾는다.
     * 아이디와 비밀번호를 더한 후 SHA256암호를 사용하여 암호화 한다.
     *
     *  @return
     *  비밀번호가 일치하는 경우 "SignIn Success"
     *  비밀번호가 다른 경우 "SignIn Fail"
     *  멤버 아이디가 잘못된 경우 "Wrong Member ID"
     */
    public ResponseEntity<String> SignIn(final String memberId, final String memberPwd) {
        Optional<Member> member = memberRepository.findById(memberId);

        Optional<String> encryptedMsg = sha256.Encrypt(memberId+memberPwd);
        if(encryptedMsg.isEmpty()){
            return new ResponseEntity<>("Encrypt Error", HttpStatus.NOT_FOUND);
        }

        return member.map(mem -> {
            if(mem.getMemberPwd().equals(encryptedMsg.get())){
                return new ResponseEntity<>("SignIn Success", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("SignIn Fail", HttpStatus.NOT_FOUND);
            }
        }).orElseGet(() -> new ResponseEntity<>("Wrong Member ID", HttpStatus.NOT_FOUND));
    }
}
