package cn.machine.geek.controller;

import cn.machine.geek.common.P;
import cn.machine.geek.common.R;
import cn.machine.geek.service.CodeGeneratorService;
import cn.machine.geek.service.DatabaseService;
import cn.machine.geek.util.ZipUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Author: MachineGeek
 * @Description: 代码生成器控制器
 * @Date: 2020/11/6
 */
@Api(tags = "代码生成器接口")
@RestController
@RequestMapping(value = "/generator")
public class CodeGeneratorController {
    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private CodeGeneratorService codeGeneratorService;

    @ApiOperation(value = "获取数据库表",notes = "获取数据库表")
    @GetMapping(value = "/pagingTableByDatabaseName")
    public R pagingTableByDatabaseName(@Validated P p){
        return R.ok(databaseService.pagingTableByDatabaseName(p.getPage(),p.getSize(),p.getKeyword()));
    }

    @ApiOperation(value = "生成代码",notes = "生成代码")
    @GetMapping(value = "/generate")
    public void generate(@RequestParam(value = "tableName") String tableName, @RequestParam(value = "moduleName") String moduleName, HttpServletResponse response){
        String generatePath = codeGeneratorService.generate(tableName, moduleName);
        String zipPath = generatePath + ".zip";
        ZipUtil.compressionToZip(generatePath,zipPath);
        response.setContentType("application/octet-stream");
        File file = new File(zipPath);
        response.setHeader("Content-Disposition", "attachment; filename="+file.getName());
        try {
            IOUtils.copy(new FileInputStream(zipPath),response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}