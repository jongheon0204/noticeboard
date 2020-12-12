package com.jongheon.www.noticeboard.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

    @Email
    @NotEmpty
    private String memberId;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;
}
