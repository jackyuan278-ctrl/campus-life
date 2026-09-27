package com.campuslife.service;

import com.campuslife.domain.vo.TagVO;

import java.util.List;

/**
 * 标签服务：问题标签的查询。
 */
public interface ITagService {

    /**
     * 全部标签：按 question_count 倒序，组装 TagVO
     */
    List<TagVO> listAll();
}
