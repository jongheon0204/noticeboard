package com.jongheon.www.noticeboard.service;

import com.jongheon.www.noticeboard.domain.dto.request.PostRequestDto;
import com.jongheon.www.noticeboard.domain.entity.Member;
import com.jongheon.www.noticeboard.domain.entity.Post;
import com.jongheon.www.noticeboard.domain.repository.MemberRepository;
import com.jongheon.www.noticeboard.domain.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    public ResponseEntity<String> Post(final PostRequestDto postRequestDto) {
        Optional<Member> member = memberRepository.findById(postRequestDto.getMemberId());
        if(member.isEmpty()){
            return new ResponseEntity<>("No Such Member", HttpStatus.NOT_FOUND);
        }

        postRepository.save(Post.builder().member(member.get()).title(postRequestDto.getTitle())
                .content(postRequestDto.getContent()).build());

        return new ResponseEntity<>("Post Success", HttpStatus.OK);
    }

    public ResponseEntity<Post> ReadById(final Long id) {
        return postRepository.findById(id)
                .map(post -> new ResponseEntity<>(post, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<String> Revise(PostRequestDto postRequestDto) {
        return postRepository.findById(postRequestDto.getId())
                .map(post -> {
                    post.setContent(postRequestDto.getContent());
                    post.setTitle(postRequestDto.getTitle());
                    postRepository.save(post);
                    return new ResponseEntity("Post Update Success", HttpStatus.OK);
                }).orElse(new ResponseEntity<>("Post Update Fail", HttpStatus.BAD_REQUEST));
    }
}
