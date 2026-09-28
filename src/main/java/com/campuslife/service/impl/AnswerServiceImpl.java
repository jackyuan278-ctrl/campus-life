package com.campuslife.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campuslife.domain.dto.AnswerFormDTO;
import com.campuslife.mapper.AnswerMapper;
import com.campuslife.mapper.LikeMapper;
import com.campuslife.mapper.QuestionMapper;
import com.campuslife.mapper.UserMapper;
import com.campuslife.domain.po.Answer;
import com.campuslife.service.IAnswerService;
import com.campuslife.domain.vo.AnswerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现留白：核心业务由你实现（IAnswerService 的 javadoc 即契约）。
 */
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements IAnswerService {

    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final LikeMapper likeMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Long create(AnswerFormDTO form, Long questionId, Long userId) {
        // TODO 由你实现：校验问题存在 → insert tb_answer → question.answerCount+1 → ZINCRBY hot:questions +5
        throw new UnsupportedOperationException("TODO: create 由你实现");
    }

    @Override
    public List<AnswerVO> listByQuestionId(Long questionId, Long userId) {
        // TODO 由你实现：status=1 按 likeCount 倒序（同赞新的在前）→ 批量补作者 → liked 查 like:user:{userId}:2
        throw new UnsupportedOperationException("TODO: listByQuestionId 由你实现");
    }

    @Override
    public void like(Long id, Long userId) {
        // TODO 由你实现：SADD like:user:{userId}:2 返回 0 → 抛"请勿重复点赞"；否则插 tb_like(targetType=2) + answer.likeCount+1
        // 回答点赞不动 hot:questions（热度分只算浏览/问题点赞/回答数）
        throw new UnsupportedOperationException("TODO: like 由你实现");
    }

    @Override
    public void unlike(Long id, Long userId) {
        // TODO 由你实现：SREM + 删 tb_like(userId,id,2) → likeCount-1（不减成负数）
        throw new UnsupportedOperationException("TODO: unlike 由你实现");
    }
}
