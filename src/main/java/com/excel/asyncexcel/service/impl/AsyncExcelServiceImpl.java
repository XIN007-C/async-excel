package com.excel.asyncexcel.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.excel.asyncexcel.entity.Record;
import com.excel.asyncexcel.mapper.AsyncExcelMapper;
import com.excel.asyncexcel.service.IAsyncExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 * 异步下载excel业务
 *
 * @author : 一个爱运动的程序员
 */
@Service
public class AsyncExcelServiceImpl extends ServiceImpl<AsyncExcelMapper, Record> implements IAsyncExcelService {

    private final static Logger logger = LoggerFactory.getLogger(AsyncExcelServiceImpl.class);

    @Autowired
    AsyncExcelMapper asyncExcelMapper;


    /**
     * 创建等待队列
     */
    BlockingQueue<Runnable> bq = new ArrayBlockingQueue<Runnable>(20);

    /**
     * 创建线程池，池中保存的线程数为3，允许的最大线程数为5
     * keepAliveTime：当线程数大于核心数时，该参数为所有的任务终止前，多余的空闲线程等待新任务的最长时间
     * unit：等待时间的单位
     * workQueue：任务执行前保存任务的队列，仅保存由execute方法提交的Runnable任务
     */
    ThreadPoolExecutor pool = new ThreadPoolExecutor(3, 5, 50, TimeUnit.MILLISECONDS, bq);


    /**
     * 查询下载成功的任务
     *
     * @param account
     * @return
     */
    @Override
    public List<Record> findDownloadTask(String account) {
        return asyncExcelMapper.selectList(new QueryWrapper<Record>().eq("status", 1).eq("account", account).orderByDesc("creation_time"));
    }

    /**
     * 通过文件码下载文件
     *
     * @param response
     * @param id
     */
    @Override
    public void streamDownloadFile(HttpServletResponse response, String id) {
        try {
            Record record = asyncExcelMapper.selectById(id);
            File file = new File(record.getUrl());
            String filename = file.getName();
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(record.getUrl()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            response.setContentType("application/octet-stream;charset=UTF-8");
            String fileName = new String(filename.getBytes("gb2312"), "iso8859-1");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            OutputStream ouputStream = response.getOutputStream();
            ouputStream.write(buffer);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(String.format("文件下载出现异常: %s", e.getMessage()));
        }
    }

    /**
     * 添加下载任务-SQL
     *
     * @param username
     * @param fileName
     * @param querySQL
     */
    @Override
    public String addSQLDownloadTask(String username, String fileName, String querySQL) {
        // 在任务表中新建下载任务
        Record record = new Record();
        String taskId = username + System.currentTimeMillis();
        record.setId(taskId);
        record.setAccount(username);
        record.setStatus(0);
        record.setCreationTime(String.valueOf(new Date()));
        record.setFileName(fileName);
        asyncExcelMapper.insert(record);
        try {
            pool.execute(() -> {
                try {
                    Thread.sleep(5000);
                    List<Map<String, Object>> mapList = asyncExcelMapper.queryList(querySQL);
                    // 未查询到数据，删除任务表中正在下载的任务记录
                    if (mapList.isEmpty() || mapList == null) {
                        asyncExcelMapper.deleteById(taskId);
                        logger.info(taskId + " 未查询到数据，无法下载");
                    } else {    // 查询到数据生成Excel表
                        String property = System.getProperty("user.dir") + "\\src\\main\\resources\\excel\\";
                        String fileUrl = property + taskId + fileName + ".xlsx";
                        List<List<Object>> lists = new ArrayList<List<Object>>();
                        for (Map<String, Object> m : mapList) {
                            List<Object> data = new ArrayList<Object>();
                            for (Map.Entry<String, Object> entry : m.entrySet()) {
                                data.add(entry.getValue());
                            }
                            lists.add(data);
                        }
                        EasyExcel.write(fileUrl)
                                // 这里放入动态头
                                .head(head(mapList)).sheet(fileName)
                                // 当然这里数据也可以用 List<List<String>> 去传入
                                .doWrite(lists);
                        Record r = new Record();
                        r.setId(taskId);
                        r.setStatus(1);
                        r.setUrl(fileUrl);
                        asyncExcelMapper.updateById(r);
                        logger.info(taskId + fileName + "下载完成");
                    }
                } catch (Exception e) {
                    asyncExcelMapper.deleteById(taskId);
                    throw new RuntimeException(String.format("线程池下载任务出现异常: %s", e.getMessage()));
                }
            });
        } catch (Exception e) {
            asyncExcelMapper.deleteById(taskId);
            throw new RuntimeException(String.format("线程池等待队列已满，无法添加新的下载任务: %s", e.getMessage()));
        }
        return "已加入下载任务";
    }

    /**
     * 获取行信息
     * @param list
     * @return
     */
    private List<List<String>> head(List<Map<String, Object>> list) {
        List<List<String>> lists = new ArrayList<List<String>>();
        for (Map<String, Object> m : list) {
            for (Map.Entry<String, Object> entry : m.entrySet()) {
                List<String> l = new ArrayList<>();
                String mapKey = entry.getKey();
                l.add(mapKey);
                lists.add(l);
            }
            break;
        }
        return lists;
    }
}


