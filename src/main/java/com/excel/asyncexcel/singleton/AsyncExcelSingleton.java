package com.excel.asyncexcel.singleton;

import com.excel.asyncexcel.entity.Record;
import com.excel.asyncexcel.service.IAsyncExcelService;
import com.excel.asyncexcel.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 异步下载excel的全局单例调用类
 *
 * @author : 一个爱运动的程序员
 */
@Slf4j
public class AsyncExcelSingleton {

    private static volatile AsyncExcelSingleton INSTANCE;

    /**
     * 加上 ForTool 后缀来和之前两种方式创建的对象作区分。
     */
    private IAsyncExcelService iAsyncExcelService;

    private AsyncExcelSingleton() {
        iAsyncExcelService = SpringContextUtils.getBean(IAsyncExcelService.class);
    }


    public static AsyncExcelSingleton getInstance() {
        if (null == INSTANCE) {
            synchronized (AsyncExcelSingleton.class) {
                if (null == INSTANCE) {
                    INSTANCE = new AsyncExcelSingleton();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 使用 SpringContextUtils 获取的 UserService 对象，并从 UserDao 中获取数据
     */
    /**
     * 查询下载成功的任务
     * @param account
     * @return
     */
    public List<Record> findDownloadTask(String account) {
        if (null == iAsyncExcelService) {
            log.debug("AsyncExcelSingleton iAsyncExcelService findDownloadTask is null");
            throw new RuntimeException(String.format("AsyncExcelSingleton iAsyncExcelService findDownloadTask is null"));
        }
        return iAsyncExcelService.findDownloadTask(account);
    }

    /**
     * 通过文件码下载文件
     * @param response
     * @param id
     */
    public void streamDownloadFile(HttpServletResponse response, String id) {
        if (null == iAsyncExcelService) {
            log.debug("AsyncExcelSingleton iAsyncExcelService streamDownloadFile is null");
            throw new RuntimeException(String.format("AsyncExcelSingleton iAsyncExcelService streamDownloadFile is null"));
        }
        iAsyncExcelService.streamDownloadFile(response, id);
    }

    /**
     * 添加下载任务-SQL
     *
     * @param username
     * @param fileName
     * @param querySQL
     */
    public String addSQLDownloadTask(String username, String fileName, String querySQL) {
        if (null == iAsyncExcelService) {
            log.debug("AsyncExcelSingleton iAsyncExcelService addSQLDownloadTask is null");
            throw new RuntimeException(String.format("AsyncExcelSingleton iAsyncExcelService addSQLDownloadTask is null"));
        }
        return iAsyncExcelService.addSQLDownloadTask(username, fileName, querySQL);
    }
}