package com.oa.system.dict.type.controller;

import com.oa.common.page.PageResult;
import com.oa.common.result.Result;
import com.oa.system.dict.type.dto.DictTypeCreateDTO;
import com.oa.system.dict.type.dto.DictTypeListVO;
import com.oa.system.dict.type.dto.DictTypeQueryDTO;
import com.oa.system.dict.type.dto.DictTypeUpdateDTO;
import com.oa.system.dict.type.service.DictTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 字典类型管理控制器
 */
@RestController
@RequestMapping("/api/system/dict/type")
@RequiredArgsConstructor
public class DictTypeController {

    private final DictTypeService dictTypeService;

    @GetMapping("/list")
    public Result<PageResult<DictTypeListVO>> list(DictTypeQueryDTO queryDTO) {
        return Result.success(dictTypeService.listDictTypes(queryDTO));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody DictTypeCreateDTO createDTO) {
        dictTypeService.createDictType(createDTO);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody DictTypeUpdateDTO updateDTO) {
        dictTypeService.updateDictType(updateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dictTypeService.deleteDictType(id);
        return Result.success();
    }
}