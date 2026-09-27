package com.campuslife.service.impl;

import com.campuslife.mapper.FollowMapper;
import com.campuslife.mapper.UserMapper;
import com.campuslife.service.IFollowService;
import com.campuslife.domain.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现留白：核心业务由你实现（IFollowService 的 javadoc 即契约）。
 */
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements IFollowService {

    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    @Override
    public void follow(Long targetUserId, Long userId) {
        // TODO 由你实现：不能关注自己 → 校验目标用户存在 → 查重（重复抛"请勿重复关注"）→ insert
        throw new UnsupportedOperationException("TODO: follow 由你实现");
    }

    @Override
    public void unfollow(Long targetUserId, Long userId) {
        // TODO 由你实现：delete where userId + followUserId，删 0 行也静默返回（幂等）
        throw new UnsupportedOperationException("TODO: unfollow 由你实现");
    }

    @Override
    public List<UserVO> queryMine(Long userId) {
        // TODO 由你实现：查 tb_follow（createTime 倒序）→ selectBatchIds 补用户
        // selectBatchIds 返回顺序与入参无关，按 follow 记录顺序从 Map 里取
        throw new UnsupportedOperationException("TODO: queryMine 由你实现");
    }
}
