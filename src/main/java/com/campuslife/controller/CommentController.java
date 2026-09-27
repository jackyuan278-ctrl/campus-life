package com.campuslife.controller;

import com.campuslife.common.Result;
import com.campuslife.common.interceptor.UserContext;
import com.campuslife.domain.dto.CommentFormDTO;
import com.campuslife.service.ICommentService;
import com.campuslife.domain.vo.CommentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    /** 发表评论（登录） */
    @PostMapping("/answers/{answerId}/comments")
    public Result<Long> create(@PathVariable("answerId") Long answerId,
                               @RequestBody @Valid CommentFormDTO form) {
        return Result.success(commentService.create(form, answerId, UserContext.getUser()));
    }

    /** 某回答的评论列表（免登录） */
    @GetMapping("/answers/{answerId}/comments")
    public Result<List<CommentVO>> list(@PathVariable("answerId") Long answerId) {
        return Result.success(commentService.listByAnswerId(answerId));
    }
}
