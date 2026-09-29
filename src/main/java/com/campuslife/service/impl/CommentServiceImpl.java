package com.campuslife.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campuslife.common.BizException;
import com.campuslife.common.constant.RedisKeys;
import com.campuslife.domain.dto.CommentFormDTO;
import com.campuslife.domain.po.Answer;
import com.campuslife.domain.po.Question;
import com.campuslife.domain.po.User;
import com.campuslife.mapper.AnswerMapper;
import com.campuslife.mapper.CommentMapper;
import com.campuslife.mapper.QuestionMapper;
import com.campuslife.mapper.UserMapper;
import com.campuslife.domain.po.Comment;
import com.campuslife.service.ICommentService;
import com.campuslife.domain.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实现留白：核心业务由你实现（ICommentService 的 javadoc 即契约）。
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    private final AnswerMapper answerMapper;
    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CommentFormDTO form, Long answerId, Long userId) {
        Answer answer = answerMapper.selectById(answerId);
        if (answer == null || answer.getStatus() != 1) {
            throw new BizException(400, "回答不存在");
        }
        String content = form.getContent();
        Comment comment = Comment.builder().content(content).userId(userId).answerId(answerId).createTime(LocalDateTime.now()).status(1).build();
        save(comment);
        answerMapper.update(null, Wrappers.<Answer>lambdaUpdate().eq(Answer::getId, answerId).setIncrBy(Answer::getCommentCount,1));
        Long questionId = answer.getQuestionId();
        questionMapper.update(null,Wrappers.<Question>lambdaUpdate().eq(Question::getId,questionId).setIncrBy(Question::getCommentCount,1));
        stringRedisTemplate.delete(RedisKeys.questionCache(questionId));
        return comment.getId();
    }

    @Override
    public List<CommentVO> listByAnswerId(Long answerId) {
        List<Comment> list = lambdaQuery().eq(Comment::getAnswerId, answerId).eq(Comment::getStatus,1).orderByAsc(Comment::getCreateTime).list();
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        List<Long> idList = new ArrayList<>();
        for (Comment comment : list) {
            Long userId = comment.getUserId();
            idList.add(userId);
        }
        List<CommentVO> commentVOS  = new ArrayList<>();
        Map<Long, User> userMap = userMapper.selectByIds(idList).stream().collect(Collectors.toMap(User::getId, user -> user));
        for (Comment comment : list) {
            Long userId = comment.getUserId();
            User user = userMap.get(userId);
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment,commentVO);
            if (user != null) {
                commentVO.setAuthorNickname(user.getNickname());
                commentVO.setAuthorAvatar(user.getAvatarUrl());
            }
            commentVOS.add(commentVO);
        }
        return commentVOS;
    }
}
