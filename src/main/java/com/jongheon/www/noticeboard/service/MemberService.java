package com.jongheon.www.noticeboard.service;

import com.jongheon.www.noticeboard.cache.MemberCache;
import com.jongheon.www.noticeboard.cipher.SHA256;
import com.jongheon.www.noticeboard.domain.entity.Member;
import com.jongheon.www.noticeboard.domain.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCache memberCache;

    @Autowired
    private SHA256 sha256;

    /**
     * 회원가입 메서드
     * 1. 캐시를 통해 입력한 ID가 기존에 등록되어 있는지 확인한다.
     * 2. 입력한 ID와 Password를 통해 암호화 한다.
     * 3. 회원가입 정보를 DB와 캐시에 저장한다.
     *
     * @return
     * 회원가입 성공 시 "SignUp Success"
     * 비밀번호 SHA256 암호화 에러시 "Encrypt Error"
     * 아이디 중복 시 "ID Already Exist"
     */
    public ResponseEntity<String> SignUp(final String memberId, final String memberPwd) {
        if(memberCache.isExistData(memberId)){
            return new ResponseEntity<>("ID Already Exist", HttpStatus.NOT_FOUND);
        }

        Optional<String> memberPwdEncrypted = sha256.Encrypt(memberId+memberPwd);
        if(memberPwdEncrypted.isEmpty()){
            return new ResponseEntity<>("Encrypt Error", HttpStatus.NOT_FOUND);
        }

        memberRepository.save(Member.builder()
                .memberId(memberId).memberPwd(memberPwdEncrypted.get()).build());

        memberCache.addNewMember(memberId);
        return new ResponseEntity<>("SignUp Success", HttpStatus.OK);
    }

    /**
     * 로그인 메서드
     * 1. 캐시를 통해 로그인이 가능한지 검사한다.
     * 2. 입력한 ID와 Password를 암호화 한다.
     * 3. DB에서 멤버 정보를 가져온 후 비교한다.
     *
     *  @return
     *  비밀번호가 일치하는 경우 "SignIn Success"
     *  비밀번호가 다른 경우 "SignIn Fail"
     *  멤버 아이디가 잘못된 경우 "Wrong Member ID"
     *  로그인 10회 이상 실패한 경우 "More than 10 times failed Login"
     */
    public ResponseEntity<String> SignIn(final String memberId, final String memberPwd) {
        int result = memberCache.isCanLogin(memberId);
        if(result == 1){
            return new ResponseEntity<>("More than 10 times failed Login", HttpStatus.NOT_FOUND);
        }else if(result == 2){
            return new ResponseEntity<>("Wrong Member ID", HttpStatus.NOT_FOUND);
        }

        Optional<String> memberPwdEncrypted = sha256.Encrypt(memberId+memberPwd);
        if(memberPwdEncrypted.isEmpty()){
            return new ResponseEntity<>("Encrypt Error", HttpStatus.NOT_FOUND);
        }

        Optional<Member> member = memberRepository.findById(memberId);

        return member.map(mem -> {
            if(mem.getMemberPwd().equals(memberPwdEncrypted.get())){
                memberCache.resetFailCount(memberId);
                return new ResponseEntity<>("SignIn Success", HttpStatus.OK);
            }else{
                memberCache.addFailCount(memberId);
                return new ResponseEntity<>("SignIn Fail", HttpStatus.NOT_FOUND);
            }
        }).orElseGet(() -> new ResponseEntity<>("Wrong Member ID", HttpStatus.NOT_FOUND));
    }

    /**
     * 비밀번호 변경 메서드
     * 1. 캐시를 통해 등록된 ID인지 검사
     * 2. 기존, 새로운 비밀번호를 암호화
     * 3. 기존 암호가 유효한지 검사
     * 4. 새로운 비밀번호로 변경
     *
     * @return
     * 멤버 아이디가 존재하지 않는 경우 "Wrong Member ID"
     * 암호화 실패 시 "Encrypt Error"
     * 비밀번호를 잘못 입력한 경우 "Wrong Member Password"
     * 비밀번호 변경 성공 시 "Change Member Password Success"
     */
    public ResponseEntity<String> ModifyUserInfo(final String memberId, final String memberPwd, final String changePwd) {
        if(!memberCache.isExistData(memberId)){
            return new ResponseEntity<>("Wrong Member ID", HttpStatus.NOT_FOUND);
        }

        Optional<String> memberPwdEncrypted = sha256.Encrypt(memberId+memberPwd);
        Optional<String> changePwdEncrypted = sha256.Encrypt(memberId+changePwd);
        if(memberPwdEncrypted.isEmpty() || changePwdEncrypted.isEmpty()){
            return new ResponseEntity<>("Encrypt Error", HttpStatus.NOT_FOUND);
        }

        Optional<Member> member = memberRepository.findById(memberId);

        if(member.isEmpty() || !member.get().getMemberPwd().equals(memberPwdEncrypted.get())){
            return new ResponseEntity<>("Wrong Member Password", HttpStatus.NOT_FOUND);
        }

        member.get().setMemberPwd(changePwdEncrypted.get());
        Member changedMember = memberRepository.save(member.get());
        log.info("Member is equals ? " + member.get().equals(changedMember));
        return new ResponseEntity<>("Change Member Password Success", HttpStatus.OK);
    }
}
