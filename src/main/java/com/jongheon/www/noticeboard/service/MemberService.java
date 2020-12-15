package com.jongheon.www.noticeboard.service;

import com.jongheon.www.noticeboard.cache.MemberCache;
import com.jongheon.www.noticeboard.cipher.SHA256;
import com.jongheon.www.noticeboard.domain.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class MemberService {

    @Autowired
    private MemberCache memberCache;

    @Autowired
    private SHA256 sha256;

    @Autowired
    private HttpSession httpSession;

    /**
     * 회원가입 메서드
     * 1. 입력한 아이디와 비밀번호를 SHA256 암호화
     * 2. MemberCache의 addNewMember 함수를 통해 새로운 멤버를 추가
     *
     * @return
     * 회원가입 성공 시 "회원가입에 성공하였습니다"
     * 비밀번호 SHA256 암호화 에러시 "관리자에게 문의해 주시기 바랍니다(Error.01)"
     * 아이디 중복 시 "존재하는 아이디 입니다"
     */
    public ResponseEntity<String> SignUp(final String id, final String password, final String name) {
        return sha256.Encrypt(id + password)
                .map(encryptedPwd -> {
                    Member newMember = Member.builder()
                            .memberId(id).name(name).password(password).loginFailCnt(0).build();
                    return memberCache.addNewMember(newMember) ?
                            new ResponseEntity<>("회원가입에 성공하였습니다", HttpStatus.OK) :
                            new ResponseEntity<>("존재하는 아이디 입니다", HttpStatus.NOT_FOUND);
                }).orElse(new ResponseEntity<>("관리자에게 문의해 주시기 바랍니다(Error.01)", HttpStatus.NOT_FOUND));
    }

    /**
     * 로그인 메서드
     * 1. 입력한 아이디와 빔리번호를 암호화 한다.
     * 2. MemberCache의 isRightLoginInfo 함수를 통해 로그인이 가능한지 검사
     *
     *  @return
     *  아이디 혹은 비밀번호가 잘못된 경우, 로그인이 10회 이상 실패한 경우 "로그인 실패"
     *  로그인 성공시 "로그인 성공"
     */
    public ResponseEntity<String> SignIn(final String id, final String password) {
        return sha256.Encrypt(id + password)
                .filter(encryptedPwd -> memberCache.isRightLoginInfo(id, encryptedPwd))
                .map(pwd -> {
                    memberCache.resetLoginFailCnt(id);
                    httpSession.setAttribute("Member", memberCache.getMemberInfo(id));
                    return new ResponseEntity<>("로그인 성공", HttpStatus.OK);
                }).orElseGet(() -> {
                    memberCache.addLoginFailCnt(id);
                    return new ResponseEntity<>("로그인 실패", HttpStatus.NOT_FOUND);
                });
    }

    /**
     * 비밀번호 변경 메서드
     * 1. 로그인 가능 함수를 통해 올바른 비밀번호인지 검사
     * 2. 새로운 비밀번호로 변경한다.
     *
     * @return
     * 아이디, 비밀번호가 잘못된 경우 "비밀번호 오류"
     * 비밀번호 변경 성공 시 "비밀번호 변경 성공"
     */
    public ResponseEntity<String> ModifyUserInfo(final String id, final String password, final String newPassword) {
        return sha256.Encrypt(id + password)
                .filter(encryptedPwd -> memberCache.isRightLoginInfo(id, encryptedPwd))
                .map(pwd ->
                    sha256.Encrypt(id + newPassword)
                            .filter(encryptedNewPwd -> memberCache.updateMemberPassword(id, encryptedNewPwd))
                            .map(newPwd -> new ResponseEntity<>("비밀번호 변경 성공", HttpStatus.OK))
                            .orElse(new ResponseEntity<>("비밀번호 변경 실패", HttpStatus.NOT_FOUND))
                ).orElse(new ResponseEntity<>("비밀번호 오류", HttpStatus.NOT_FOUND));
    }
}
