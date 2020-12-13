package com.jongheon.www.noticeboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class IndexController {

    @Autowired
    private HttpSession httpSession;

    @GetMapping("")
    public String Index(Model model){
        Optional.ofNullable((String)httpSession.getAttribute("Member"))
        .ifPresent(memberId -> model.addAttribute("name", memberId));
        return "index";
    }

    @GetMapping("/login")
    public String Login(){
        return "login";
    }
}
