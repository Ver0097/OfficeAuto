package com.oa.system.dict.type.mapper;

import com.oa.system.dict.type.dto.DictTypeListVO;
import com.oa.system.dict.type.dto.DictTypeQueryDTO;
import com.oa.system.dict.type.entity.SysDictType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysDictTypeMapper {

    SysDictType selectByDictType(@Param("dictType") String dictType);

    SysDictType selectById(@Param("id") Long id);

    Long countDictTypes(DictTypeQueryDTO queryDTO);

    List<DictTypeListVO> selectDictTypeList(DictTypeQueryDTO queryDTO);

    int insert(SysDictType dictType);

    int updateById(SysDictType dictType);

    int deleteById(@Param("id") Long id);
}