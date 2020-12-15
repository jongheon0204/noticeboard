package com.jongheon.www.noticeboard.cache;

import com.jongheon.www.noticeboard.domain.entity.Member;
import com.jongheon.www.noticeboard.domain.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class MemberCache {

    private Map<String, Member> members;

    @Autowired
    private MemberRepository memberRepository;

    @PostConstruct
    public void cachePostConstruct(){
        members = new HashMap<>();
        memberRepository.findAll().forEach(member -> members.put(member.getMemberId(), member));
    }

    @PreDestroy
    public void cachePreDestroy(){
        members.keySet().forEach(id -> memberRepository.save(members.get(id)));
        members.clear();
    }

    public boolean isAlreadyExist(final String id){
        return members.containsKey(id);
    }

    public Optional<Member> getMemberInfo(final String id){
        return Optional.ofNullable(members.get(id));
    }

    public boolean addNewMember(final Member newMember){
        if(isAlreadyExist(newMember.getMemberId())) return false;
        memberRepository.save(newMember);
        members.put(newMember.getMemberId(), newMember);
        return true;
    }

    public boolean removeMember(final Member member) {
        if (!isAlreadyExist(member.getMemberId())) return false;
        memberRepository.delete(member);
        members.remove(member.getMemberId());
        return true;
    }

    public boolean updateMemberPassword(final String id, final String newPassword){
        if(!isAlreadyExist(id)) return false;
        Member member = members.get(id);
        member.setPassword(newPassword);
        memberRepository.save(member);
        members.put(member.getMemberId(), member);
        return true;
    }

    public void addLoginFailCnt(final String id){
        if(!isAlreadyExist(id)) return;
        Member member = members.get(id);
        if(member.getLoginFailCnt() >= 10) return;
        member.setLoginFailCnt(member.getLoginFailCnt() + 1);
        members.put(id, member);
        if(member.getLoginFailCnt() >= 10){
            memberRepository.save(member);
        }
    }

    public void resetLoginFailCnt(final String id){
        if(!isAlreadyExist(id)) return;
        Member member = members.get(id);
        member.setLoginFailCnt(0);
        members.put(id, member);
    }

    public boolean isRightLoginInfo(final String id, final String encryptedPwd){
        if(!isAlreadyExist(id)) return false;
        if(members.get(id).getLoginFailCnt() >= 10) return false;
        return members.get(id).getPassword().equals(encryptedPwd);
    }

}
