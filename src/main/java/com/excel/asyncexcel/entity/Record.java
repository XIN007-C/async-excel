package com.excel.asyncexcel.entity;

import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
@ColumnWidth(25)
public class Record {
    /**
     * 文件码（id）
     */
    private String id;

    /**
     * 用户账户
     */
    private String account;

    /**
     * 文件状态 0 下载中， 1 下载完成
     */
    private Integer status;

    /**
     * 文件下载的路径
     */
    private String url;

    /**
     * 下载任务的创建时间
     */
    private String creationTime;

    /**
     * 文件模块
     */
    private String fileName;
}
