package com.jongheon.www.noticeboard.cache;

import com.jongheon.www.noticeboard.domain.entity.Member;
import com.jongheon.www.noticeboard.domain.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

// TODO : Member 테이블에 is_locked 컬럼을 추가하여 로그인 실패 10회 이상 시 락이 걸릴 수 있게 한다
/**
 * 멤버 캐시를 통해 디비 접근 최소화
 * 캐시는 멤버의 아이디와 실패 횟수 정보만 가지고 있는다.
 * MemberCache 빈 생성 시 DB에서 멤버 정보를 가져와 members를 초기화
 */

@Slf4j
@Component
public class MemberCache {

    private Map<String, Integer> members;

    @Autowired
    private MemberRepository memberRepository;

    @PostConstruct
    public void cachePostConstruct(){
        members = new HashMap<>();
        memberRepository.findAll().stream().map(Member::getMemberId).forEach(memberId -> members.put(memberId, 0));
    }

    @PreDestroy
    public void cachePreDestroy(){
        members.clear();
    }

    public void addNewMember(final String memberId){
        if(!members.containsKey(memberId)){
            members.put(memberId, 0);
        }
    }

    public void removeMember(final String memberId){
        members.remove(memberId);
    }

    public void addFailCount(final String memberId){
        members.put(memberId, members.get(memberId) + 1);
    }

    public void resetFailCount(final String memberId){
        members.put(memberId, 0);
    }

    public boolean isExistData(final String memberId){
        return members.containsKey(memberId);
    }

    /**
     * @return
     * 0 : 로그인 성공
     * 1 : 로그인 10회 실패로 인해 더 이상 로그인 할 수 없다.
     * 2 : 입력한 아이디 값이 멤버 목록에 존재하지 않는다
     */
    public int isCanLogin(final String memberId){
        if(!members.containsKey(memberId)){
            return 2;
        }
        if(members.get(memberId) > 10){
            return 1;
        }
        return 0;
    }

}
