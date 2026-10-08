package com.campuslife.controller;

import com.campuslife.domain.dto.ActivityFormDTO;
import com.campuslife.domain.dto.ActivityPageQuery;
import com.campuslife.domain.vo.ActivityVO;
import com.campuslife.es.ActivityDoc;
import com.campuslife.service.IActivityService;
import com.campuslife.service.IActivitySearchService;
import com.campuslife.common.Result;
import com.campuslife.domain.dto.PageDTO;
import com.campuslife.common.interceptor.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final IActivityService activityService;
    private final IActivitySearchService activitySearchService;

    /** 发布活动（登录） */
    @PostMapping
    public Result<Long> publish(@RequestBody @Valid ActivityFormDTO form) {
        return Result.success(activityService.publish(form, UserContext.getUser()));
    }

    /** 活动分页列表（免登录，支持 keyword/status） */
    @GetMapping
    public Result<PageDTO<ActivityVO>> page(ActivityPageQuery query) {
        return Result.success(activityService.queryActivityPage(query));
    }

    /** 活动全文搜索（ES + IK 分词，免登录；关键词为空返回空页） */
    @GetMapping("/search")
    public Result<PageDTO<ActivityDoc>> search(@RequestParam(defaultValue = "") String keyword,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(activitySearchService.search(keyword, page, pageSize));
    }

    /** 活动详情（免登录；带 token 时附带我的报名状态） */
    @GetMapping("/{id}")
    public Result<ActivityVO> detail(@PathVariable("id") Long id) {
        return Result.success(activityService.queryActivityById(id, UserContext.getUser()));
    }

    /** 发布人取消活动（登录） */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable("id") Long id) {
        activityService.cancelActivity(id, UserContext.getUser());
        return Result.success();
    }
}
