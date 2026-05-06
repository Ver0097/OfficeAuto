package com.oa.system.config.controller;

import com.oa.common.page.PageResult;
import com.oa.common.result.Result;
import com.oa.system.config.dto.ConfigCreateDTO;
import com.oa.system.config.dto.ConfigListVO;
import com.oa.system.config.dto.ConfigQueryDTO;
import com.oa.system.config.dto.ConfigUpdateDTO;
import com.oa.system.config.service.ConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 参数管理控制器
 */
@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @GetMapping("/list")
    public Result<PageResult<ConfigListVO>> list(ConfigQueryDTO queryDTO) {
        return Result.success(configService.listConfigs(queryDTO));
    }

    @GetMapping("/key/{configKey}")
    public Result<String> getByKey(@PathVariable String configKey) {
        return Result.success(configService.getConfigValue(configKey));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody ConfigCreateDTO createDTO) {
        configService.createConfig(createDTO);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody ConfigUpdateDTO updateDTO) {
        configService.updateConfig(updateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        configService.deleteConfig(id);
        return Result.success();
    }
}