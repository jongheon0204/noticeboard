package com.jongheon.www.noticeboard.controller;

import com.jongheon.www.noticeboard.domain.dto.request.PostRequestDto;
import com.jongheon.www.noticeboard.domain.entity.Post;
import com.jongheon.www.noticeboard.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("")
    public ResponseEntity<String> create(@RequestBody PostRequestDto postRequestDto){
        return postService.Post(postRequestDto);
    }

    @GetMapping("")
    public ResponseEntity<Post> read(@RequestParam("id") Long id){
        return postService.ReadById(id);
    }
}
