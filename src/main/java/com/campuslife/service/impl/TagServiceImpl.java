package com.campuslife.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campuslife.domain.po.Tag;
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
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    @Override
    public List<TagVO> listAll() {
        // TODO 由你实现：orderByDesc(Tag::getQuestionCount) 查全部 → 组装 TagVO
        throw new UnsupportedOperationException("TODO: listAll 由你实现");
    }
}
