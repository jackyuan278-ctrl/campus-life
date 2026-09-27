package com.campuslife.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campuslife.domain.po.Signup;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SignupMapper extends BaseMapper<Signup> {
}
