package com.excel.asyncexcel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.excel.asyncexcel.entity.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 异步下载excel业务
 *
 * @author : 一个爱运动的程序员
 */
@Mapper
@Repository
public interface AsyncExcelMapper extends BaseMapper<Record> {

    @Select(" ${querySql} ")
    List<Map<String, Object>> queryList(@Param("querySql") String querySQL);
}
