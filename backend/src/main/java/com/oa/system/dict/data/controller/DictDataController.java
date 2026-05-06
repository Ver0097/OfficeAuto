package com.oa.system.dict.data.controller;

import com.oa.common.page.PageResult;
import com.oa.common.result.Result;
import com.oa.system.dict.data.dto.DictDataCreateDTO;
import com.oa.system.dict.data.dto.DictDataListVO;
import com.oa.system.dict.data.dto.DictDataQueryDTO;
import com.oa.system.dict.data.dto.DictDataUpdateDTO;
import com.oa.system.dict.data.service.DictDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据管理控制器
 */
@RestController
@RequestMapping("/api/system/dict/data")
@RequiredArgsConstructor
public class DictDataController {

    private final DictDataService dictDataService;

    @GetMapping("/list")
    public Result<PageResult<DictDataListVO>> list(DictDataQueryDTO queryDTO) {
        return Result.success(dictDataService.listDictData(queryDTO));
    }

    @GetMapping("/type/{dictType}")
    public Result<List<DictDataListVO>> getByType(@PathVariable String dictType) {
        return Result.success(dictDataService.getDictDataByType(dictType));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody DictDataCreateDTO createDTO) {
        dictDataService.createDictData(createDTO);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody DictDataUpdateDTO updateDTO) {
        dictDataService.updateDictData(updateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dictDataService.deleteDictData(id);
        return Result.success();
    }
}