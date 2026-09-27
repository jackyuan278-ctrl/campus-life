package com.campuslife.controller;

import com.campuslife.common.Result;
import com.campuslife.common.interceptor.UserContext;
import com.campuslife.domain.dto.AnswerFormDTO;
import com.campuslife.service.IAnswerService;
import com.campuslife.domain.vo.AnswerVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final IAnswerService answerService;

    /** 回答问题（登录） */
    @PostMapping("/questions/{questionId}/answers")
    public Result<Long> create(@PathVariable("questionId") Long questionId,
                               @RequestBody @Valid AnswerFormDTO form) {
        return Result.success(answerService.create(form, questionId, UserContext.getUser()));
    }

    /** 某问题的回答列表（免登录，带 token 时附带 liked） */
    @GetMapping("/questions/{questionId}/answers")
    public Result<List<AnswerVO>> listByQuestion(@PathVariable("questionId") Long questionId) {
        return Result.success(answerService.listByQuestionId(questionId, UserContext.getUser()));
    }

    /** 点赞回答（登录） */
    @PostMapping("/answers/{id}/like")
    public Result<Void> like(@PathVariable("id") Long id) {
        answerService.like(id, UserContext.getUser());
        return Result.success();
    }

    /** 取消点赞（登录） */
    @DeleteMapping("/answers/{id}/like")
    public Result<Void> unlike(@PathVariable("id") Long id) {
        answerService.unlike(id, UserContext.getUser());
        return Result.success();
    }
}
