package com.oa.system.config.service;

import com.oa.common.exception.BusinessException;
import com.oa.common.page.PageResult;
import com.oa.common.result.ResultCode;
import com.oa.system.config.dto.ConfigCreateDTO;
import com.oa.system.config.dto.ConfigListVO;
import com.oa.system.config.dto.ConfigQueryDTO;
import com.oa.system.config.dto.ConfigUpdateDTO;
import com.oa.system.config.entity.SysConfig;
import com.oa.system.config.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final SysConfigMapper sysConfigMapper;

    @Override
    public PageResult<ConfigListVO> listConfigs(ConfigQueryDTO queryDTO) {
        Long total = sysConfigMapper.countConfigs(queryDTO);
        List<ConfigListVO> list = sysConfigMapper.selectConfigList(queryDTO);
        return new PageResult<>(total, queryDTO.getPageNum(), queryDTO.getPageSize(), list);
    }

    @Override
    public String getConfigValue(String configKey) {
        SysConfig config = sysConfigMapper.selectByConfigKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional
    public void createConfig(ConfigCreateDTO createDTO) {
        SysConfig existing = sysConfigMapper.selectByConfigKey(createDTO.getConfigKey());
        if (existing != null) {
            throw new BusinessException(ResultCode.CONFIG_KEY_EXISTS);
        }

        SysConfig config = new SysConfig();
        config.setConfigKey(createDTO.getConfigKey());
        config.setConfigValue(createDTO.getConfigValue());
        config.setConfigName(createDTO.getConfigName());
        config.setConfigType(createDTO.getConfigType() != null ? createDTO.getConfigType() : "text");
        config.setRemark(createDTO.getRemark());
        config.setStatus(createDTO.getStatus() != null ? createDTO.getStatus() : 1);

        sysConfigMapper.insert(config);
    }

    @Override
    @Transactional
    public void updateConfig(ConfigUpdateDTO updateDTO) {
        SysConfig config = sysConfigMapper.selectById(updateDTO.getId());
        if (config == null) {
            throw new BusinessException(ResultCode.CONFIG_NOT_FOUND);
        }

        if (updateDTO.getConfigKey() != null && !updateDTO.getConfigKey().equals(config.getConfigKey())) {
            SysConfig existing = sysConfigMapper.selectByConfigKey(updateDTO.getConfigKey());
            if (existing != null) {
                throw new BusinessException(ResultCode.CONFIG_KEY_EXISTS);
            }
            config.setConfigKey(updateDTO.getConfigKey());
        }

        if (updateDTO.getConfigValue() != null) {
            config.setConfigValue(updateDTO.getConfigValue());
        }
        if (updateDTO.getConfigName() != null) {
            config.setConfigName(updateDTO.getConfigName());
        }
        if (updateDTO.getConfigType() != null) {
            config.setConfigType(updateDTO.getConfigType());
        }
        if (updateDTO.getRemark() != null) {
            config.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            config.setStatus(updateDTO.getStatus());
        }

        sysConfigMapper.updateById(config);
    }

    @Override
    @Transactional
    public void deleteConfig(Long id) {
        SysConfig config = sysConfigMapper.selectById(id);
        if (config == null) {
            throw new BusinessException(ResultCode.CONFIG_NOT_FOUND);
        }
        sysConfigMapper.deleteById(id);
    }
}