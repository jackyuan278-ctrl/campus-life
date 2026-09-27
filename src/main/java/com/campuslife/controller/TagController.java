package com.campuslife.controller;

import com.campuslife.common.Result;
import com.campuslife.service.ITagService;
import com.campuslife.domain.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final ITagService tagService;

    /** 全部标签（免登录，供提问/筛选使用） */
    @GetMapping
    public Result<List<TagVO>> list() {
        return Result.success(tagService.listAll());
    }
}
