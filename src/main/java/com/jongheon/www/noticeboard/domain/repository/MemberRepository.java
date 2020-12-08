package com.jongheon.www.noticeboard.domain.repository;

import com.jongheon.www.noticeboard.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
}
