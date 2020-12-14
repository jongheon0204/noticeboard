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

import javax.servlet.http.HttpSession;
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

    @Autowired
    private HttpSession httpSession;

    /**
     * 회원가입 메서드
     * 1. 캐시를 통해 입력한 ID가 기존에 등록되어 있는지 확인한다.
     * 2. 입력한 ID와 Password를 통해 암호화 한다.
     * 3. 회원가입 정보를 DB와 캐시에 저장한다.
     *
     * @return
     * 회원가입 성공 시 "회원가입에 성공하였습니다"
     * 비밀번호 SHA256 암호화 에러시 "관리자에게 문의해 주시기 바랍니다(Error.01)"
     * 아이디 중복 시 "존재하는 아이디 입니다"
     */
    public ResponseEntity<String> SignUp(final String memberId, final String password, final String name) {
        if(memberCache.isExistData(memberId)){
            return new ResponseEntity<>("존재하는 아이디 입니다", HttpStatus.NOT_FOUND);
        }

        return sha256.Encrypt(memberId + password)
                .map(encryptedPwd ->{
                    memberRepository.save(Member.builder().memberId(memberId).password(encryptedPwd).name(name).build());
                    memberCache.addNewMember(memberId);
                    return new ResponseEntity<>("회원가입에 성공하였습니다", HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>("관리자에게 문의해 주시기 바랍니다(Error.01)", HttpStatus.NOT_FOUND));
    }

    /**
     * 로그인 메서드
     * 1. 캐시를 통해 로그인이 가능한지 검사한다.
     * 2. 입력한 ID와 Password를 암호화 한다.
     * 3. DB에서 멤버 정보를 가져온 후 비교한다.
     *
     *  @return
     *  비밀번호가 일치하는 경우 "로그인 성공"
     *  비밀번호가 다른 경우 "로그인 실패"
     *  멤버 아이디가 잘못된 경우 "존재하지 않는 아이디 입니다"
     *  로그인 10회 이상 실패한 경우 "비밀번호 10회 오류"
     */
    public ResponseEntity<String> SignIn(final String memberId, final String password) {
        int result = memberCache.isCanLogin(memberId);
        if(result == 1){
            return new ResponseEntity<>("비밀번호 10회 오류", HttpStatus.NOT_FOUND);
        }else if(result == 2){
            return new ResponseEntity<>("존재하지 않는 아이디 입니다", HttpStatus.NOT_FOUND);
        }

        Optional<String> encryptedPwd = sha256.Encrypt(memberId + password);
        if(encryptedPwd.isEmpty()){
            return new ResponseEntity<>("관리자에게 문의해 주시기 바랍니다(Error.01)", HttpStatus.NOT_FOUND);
        }

        return memberRepository.findById(memberId)
                .filter(member -> member.getPassword().equals(encryptedPwd.get()))
                .map(member ->{
                    memberCache.resetFailCount(memberId);
                    httpSession.setAttribute("Member", member.getName());
                    return new ResponseEntity<>("로그인 성공", HttpStatus.OK);
                }).orElseGet(() -> {
                    memberCache.addFailCount(memberId);
                    return new ResponseEntity<>("로그인 실패", HttpStatus.NOT_FOUND);
                });
    }

    /**
     * 비밀번호 변경 메서드
     * 1. 캐시를 통해 등록된 ID인지 검사
     * 2. 기존, 새로운 비밀번호를 암호화
     * 3. 기존 암호가 유효한지 검사
     * 4. 새로운 비밀번호로 변경
     *
     * @return
     * 멤버 아이디가 존재하지 않는 경우 "존재하지 않는 아이디 입니다"
     * 암호화 실패 시 "관리자에게 문의해 주시기 바랍니다(Error.01)"
     * 비밀번호를 잘못 입력한 경우 "비밀번호 오류"
     * 비밀번호 변경 성공 시 "비밀번호 변경 성공"
     */
    public ResponseEntity<String> ModifyUserInfo(final String memberId, final String password, final String changePwd) {
        if(!memberCache.isExistData(memberId)){
            return new ResponseEntity<>("존재하지 않는 아이디 입니다", HttpStatus.NOT_FOUND);
        }

        Optional<String> encryptedPwd = sha256.Encrypt(memberId+password);
        Optional<String> encryptedChangedPwd = sha256.Encrypt(memberId+changePwd);
        if(encryptedPwd.isEmpty() || encryptedChangedPwd.isEmpty()){
            return new ResponseEntity<>("관리자에게 문의해 주시기 바랍니다(Error.01)", HttpStatus.NOT_FOUND);
        }

        return memberRepository.findById(memberId)
                .filter(member -> member.getPassword().equals(encryptedPwd.get()))
                .map(member -> {
                    member.setPassword(encryptedChangedPwd.get());
                    memberRepository.save(member);
                    return new ResponseEntity<>("비밀번호 변경 성공", HttpStatus.OK);
                }).orElse(new ResponseEntity<>("비밀번호 오류", HttpStatus.NOT_FOUND));
    }
}
