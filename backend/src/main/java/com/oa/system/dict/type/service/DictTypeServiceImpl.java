package com.oa.system.dict.type.service;

import com.oa.common.exception.BusinessException;
import com.oa.common.page.PageResult;
import com.oa.common.result.ResultCode;
import com.oa.system.dict.type.dto.DictTypeCreateDTO;
import com.oa.system.dict.type.dto.DictTypeListVO;
import com.oa.system.dict.type.dto.DictTypeQueryDTO;
import com.oa.system.dict.type.dto.DictTypeUpdateDTO;
import com.oa.system.dict.type.entity.SysDictType;
import com.oa.system.dict.type.mapper.SysDictTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictTypeServiceImpl implements DictTypeService {

    private final SysDictTypeMapper sysDictTypeMapper;

    @Override
    public PageResult<DictTypeListVO> listDictTypes(DictTypeQueryDTO queryDTO) {
        Long total = sysDictTypeMapper.countDictTypes(queryDTO);
        List<DictTypeListVO> list = sysDictTypeMapper.selectDictTypeList(queryDTO);
        return new PageResult<>(total, queryDTO.getPageNum(), queryDTO.getPageSize(), list);
    }

    @Override
    @Transactional
    public void createDictType(DictTypeCreateDTO createDTO) {
        SysDictType existing = sysDictTypeMapper.selectByDictType(createDTO.getDictType());
        if (existing != null) {
            throw new BusinessException(ResultCode.DICT_TYPE_EXISTS);
        }

        SysDictType dictType = new SysDictType();
        dictType.setDictType(createDTO.getDictType());
        dictType.setDictName(createDTO.getDictName());
        dictType.setRemark(createDTO.getRemark());
        dictType.setStatus(createDTO.getStatus() != null ? createDTO.getStatus() : 1);

        sysDictTypeMapper.insert(dictType);
    }

    @Override
    @Transactional
    public void updateDictType(DictTypeUpdateDTO updateDTO) {
        SysDictType dictType = sysDictTypeMapper.selectById(updateDTO.getId());
        if (dictType == null) {
            throw new BusinessException(ResultCode.DICT_TYPE_NOT_FOUND);
        }

        if (updateDTO.getDictType() != null && !updateDTO.getDictType().equals(dictType.getDictType())) {
            SysDictType existing = sysDictTypeMapper.selectByDictType(updateDTO.getDictType());
            if (existing != null) {
                throw new BusinessException(ResultCode.DICT_TYPE_EXISTS);
            }
            dictType.setDictType(updateDTO.getDictType());
        }

        if (updateDTO.getDictName() != null) {
            dictType.setDictName(updateDTO.getDictName());
        }
        if (updateDTO.getRemark() != null) {
            dictType.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            dictType.setStatus(updateDTO.getStatus());
        }

        sysDictTypeMapper.updateById(dictType);
    }

    @Override
    @Transactional
    public void deleteDictType(Long id) {
        SysDictType dictType = sysDictTypeMapper.selectById(id);
        if (dictType == null) {
            throw new BusinessException(ResultCode.DICT_TYPE_NOT_FOUND);
        }
        sysDictTypeMapper.deleteById(id);
    }
}