package com.excel.asyncexcel.controller;

import com.excel.asyncexcel.entity.Record;
import com.excel.asyncexcel.singleton.AsyncExcelSingleton;
import com.excel.asyncexcel.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 异步下载excel的控制层
 *
 * @author : 一个爱运动的程序员
 */

@RestController
@RequestMapping("/async/excel")
public class AsyncExcelController {

    @GetMapping("/findDownloadTask")
    public R<List<Record>> findDownloadTask(String username) {
        List<Record> list = AsyncExcelSingleton.getInstance().findDownloadTask(username);
        return R.ok(list);
    }

    @GetMapping("/streamDownloadFile")
    public void streamDownloadFile(HttpServletResponse response, String id) {
        AsyncExcelSingleton.getInstance().streamDownloadFile(response, id);
    }

    @GetMapping("/addSQLDownloadTask")
    public R<String> addSQLDownloadTask(String username, String fileName, String querySQL) {
        String s = AsyncExcelSingleton.getInstance().addSQLDownloadTask(username, fileName, querySQL);
        return R.ok(s);
    }
}
