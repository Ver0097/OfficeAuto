package com.oa.system.dict.data.mapper;

import com.oa.system.dict.data.dto.DictDataListVO;
import com.oa.system.dict.data.dto.DictDataQueryDTO;
import com.oa.system.dict.data.entity.SysDictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysDictDataMapper {

    SysDictData selectById(@Param("id") Long id);

    Long countDictData(DictDataQueryDTO queryDTO);

    List<DictDataListVO> selectDictDataList(DictDataQueryDTO queryDTO);

    List<DictDataListVO> selectByDictType(@Param("dictType") String dictType);

    int insert(SysDictData dictData);

    int updateById(SysDictData dictData);

    int deleteById(@Param("id") Long id);
}