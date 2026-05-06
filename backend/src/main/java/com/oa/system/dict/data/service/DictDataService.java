package com.oa.system.dict.data.service;

import com.oa.common.page.PageResult;
import com.oa.system.dict.data.dto.DictDataCreateDTO;
import com.oa.system.dict.data.dto.DictDataListVO;
import com.oa.system.dict.data.dto.DictDataQueryDTO;
import com.oa.system.dict.data.dto.DictDataUpdateDTO;

import java.util.List;

/**
 * 字典数据服务接口
 */
public interface DictDataService {

    PageResult<DictDataListVO> listDictData(DictDataQueryDTO queryDTO);

    List<DictDataListVO> getDictDataByType(String dictType);

    void createDictData(DictDataCreateDTO createDTO);

    void updateDictData(DictDataUpdateDTO updateDTO);

    void deleteDictData(Long id);
}