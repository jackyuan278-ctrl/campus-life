package com.campuslife.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campuslife.domain.dto.CommentFormDTO;
import com.campuslife.mapper.AnswerMapper;
import com.campuslife.mapper.CommentMapper;
import com.campuslife.mapper.QuestionMapper;
import com.campuslife.mapper.UserMapper;
import com.campuslife.domain.po.Comment;
import com.campuslife.service.ICommentService;
import com.campuslife.domain.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现留白：核心业务由你实现（ICommentService 的 javadoc 即契约）。
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    private final AnswerMapper answerMapper;
    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;

    @Override
    public Long create(CommentFormDTO form, Long answerId, Long userId) {
        // TODO 由你实现：校验回答存在 → insert tb_comment → answer.commentCount+1 且所属 question.commentCount+1
        throw new UnsupportedOperationException("TODO: create 由你实现");
    }

    @Override
    public List<CommentVO> listByAnswerId(Long answerId) {
        // TODO 由你实现：status=1 按 createTime 正序 → 批量补作者
        throw new UnsupportedOperationException("TODO: listByAnswerId 由你实现");
    }
}
