package com.oa.system.config.mapper;

import com.oa.system.config.dto.ConfigListVO;
import com.oa.system.config.dto.ConfigQueryDTO;
import com.oa.system.config.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysConfigMapper {

    SysConfig selectByConfigKey(@Param("configKey") String configKey);

    SysConfig selectById(@Param("id") Long id);

    Long countConfigs(ConfigQueryDTO queryDTO);

    List<ConfigListVO> selectConfigList(ConfigQueryDTO queryDTO);

    int insert(SysConfig config);

    int updateById(SysConfig config);

    int deleteById(@Param("id") Long id);
}