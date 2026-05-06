package com.oa.system.dict.type.service;

import com.oa.common.page.PageResult;
import com.oa.system.dict.type.dto.DictTypeCreateDTO;
import com.oa.system.dict.type.dto.DictTypeListVO;
import com.oa.system.dict.type.dto.DictTypeQueryDTO;
import com.oa.system.dict.type.dto.DictTypeUpdateDTO;

/**
 * 字典类型服务接口
 */
public interface DictTypeService {

    PageResult<DictTypeListVO> listDictTypes(DictTypeQueryDTO queryDTO);

    void createDictType(DictTypeCreateDTO createDTO);

    void updateDictType(DictTypeUpdateDTO updateDTO);

    void deleteDictType(Long id);
}