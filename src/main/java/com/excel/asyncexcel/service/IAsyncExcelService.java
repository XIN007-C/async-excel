package com.excel.asyncexcel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.excel.asyncexcel.entity.Record;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 异步下载excel业务
 *
 * @author : 一个爱运动的程序员
 */
public interface IAsyncExcelService  extends IService<Record> {


    /**
     * 查询下载成功的任务
     * @param account
     * @return
     */
    List<Record> findDownloadTask(String account);

    /**
     * 通过文件码下载文件
     * @param response
     * @param id
     */
    void streamDownloadFile(HttpServletResponse response, String id);

    /**
     * 添加下载任务-SQL
     * @param username
     * @param fileName
     * @param querySQL
     */
    String addSQLDownloadTask(String username, String fileName, String querySQL);
}
