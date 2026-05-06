package com.oa.system.config.service;

import com.oa.common.page.PageResult;
import com.oa.system.config.dto.ConfigCreateDTO;
import com.oa.system.config.dto.ConfigListVO;
import com.oa.system.config.dto.ConfigQueryDTO;
import com.oa.system.config.dto.ConfigUpdateDTO;

/**
 * 参数服务接口
 */
public interface ConfigService {

    PageResult<ConfigListVO> listConfigs(ConfigQueryDTO queryDTO);

    String getConfigValue(String configKey);

    void createConfig(ConfigCreateDTO createDTO);

    void updateConfig(ConfigUpdateDTO updateDTO);

    void deleteConfig(Long id);
}