package com.campuslife.service.impl;

import com.campuslife.mapper.TagMapper;
import com.campuslife.service.ITagService;
import com.campuslife.domain.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现留白：核心业务由你实现（ITagService 的 javadoc 即契约）。
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements ITagService {

    private final TagMapper tagMapper;

    @Override
    public List<TagVO> listAll() {
        // TODO 由你实现：orderByDesc(Tag::getQuestionCount) 查全部 → 组装 TagVO
        throw new UnsupportedOperationException("TODO: listAll 由你实现");
    }
}
