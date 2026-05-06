package com.oa.system.dict.data.service;

import com.oa.common.exception.BusinessException;
import com.oa.common.page.PageResult;
import com.oa.common.result.ResultCode;
import com.oa.system.dict.data.dto.DictDataCreateDTO;
import com.oa.system.dict.data.dto.DictDataListVO;
import com.oa.system.dict.data.dto.DictDataQueryDTO;
import com.oa.system.dict.data.dto.DictDataUpdateDTO;
import com.oa.system.dict.data.entity.SysDictData;
import com.oa.system.dict.data.mapper.SysDictDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictDataServiceImpl implements DictDataService {

    private final SysDictDataMapper sysDictDataMapper;

    @Override
    public PageResult<DictDataListVO> listDictData(DictDataQueryDTO queryDTO) {
        Long total = sysDictDataMapper.countDictData(queryDTO);
        List<DictDataListVO> list = sysDictDataMapper.selectDictDataList(queryDTO);
        return new PageResult<>(total, queryDTO.getPageNum(), queryDTO.getPageSize(), list);
    }

    @Override
    public List<DictDataListVO> getDictDataByType(String dictType) {
        return sysDictDataMapper.selectByDictType(dictType);
    }

    @Override
    @Transactional
    public void createDictData(DictDataCreateDTO createDTO) {
        SysDictData dictData = new SysDictData();
        dictData.setDictType(createDTO.getDictType());
        dictData.setDictLabel(createDTO.getDictLabel());
        dictData.setDictValue(createDTO.getDictValue());
        dictData.setDictSort(createDTO.getDictSort() != null ? createDTO.getDictSort() : 0);
        dictData.setRemark(createDTO.getRemark());
        dictData.setStatus(createDTO.getStatus() != null ? createDTO.getStatus() : 1);

        sysDictDataMapper.insert(dictData);
    }

    @Override
    @Transactional
    public void updateDictData(DictDataUpdateDTO updateDTO) {
        SysDictData dictData = sysDictDataMapper.selectById(updateDTO.getId());
        if (dictData == null) {
            throw new BusinessException(ResultCode.DICT_DATA_NOT_FOUND);
        }

        if (updateDTO.getDictType() != null) {
            dictData.setDictType(updateDTO.getDictType());
        }
        if (updateDTO.getDictLabel() != null) {
            dictData.setDictLabel(updateDTO.getDictLabel());
        }
        if (updateDTO.getDictValue() != null) {
            dictData.setDictValue(updateDTO.getDictValue());
        }
        if (updateDTO.getDictSort() != null) {
            dictData.setDictSort(updateDTO.getDictSort());
        }
        if (updateDTO.getRemark() != null) {
            dictData.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            dictData.setStatus(updateDTO.getStatus());
        }

        sysDictDataMapper.updateById(dictData);
    }

    @Override
    @Transactional
    public void deleteDictData(Long id) {
        SysDictData dictData = sysDictDataMapper.selectById(id);
        if (dictData == null) {
            throw new BusinessException(ResultCode.DICT_DATA_NOT_FOUND);
        }
        sysDictDataMapper.deleteById(id);
    }
}