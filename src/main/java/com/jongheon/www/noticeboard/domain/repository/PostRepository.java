package com.jongheon.www.noticeboard.domain.repository;

import com.jongheon.www.noticeboard.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
