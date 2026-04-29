package com.yupi.yuaicodemother.core;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yupi.yuaicodemother.ai.model.HtmlCodeResult;
import com.yupi.yuaicodemother.ai.model.MultiFileCodeResult;
import com.yupi.yuaicodemother.model.emuns.CodeGenTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CodeFileSaver {


    // 文件保存根目录 - 修改为项目内的tmp目录
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/src/main/java/com/yupi/yuaicodemother/tmp/";


    /**
     * 保存 HtmlCodeResult
     */
    public static File saveHtmlCodeResult(HtmlCodeResult result) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        return new File(baseDirPath);
    }

    /**
     * 保存 MultiFileCodeResult
     */
    public static File saveMultiFileCodeResult(MultiFileCodeResult result) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        writeToFile(baseDirPath, "style.css", result.getCssCode());
        writeToFile(baseDirPath, "script.js", result.getJsCode());
        return new File(baseDirPath);
    }

    /**
     * 构建唯一目录路径：tmp/code_output/bizType_雪花ID
     */
    private static String buildUniqueDir(String bizType) {
        String uniqueDirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }


    /**
     * 写入单个文件
     */
    private static void writeToFile(String dirPath, String filename, String content) {
        if (StrUtil.isBlank(content)) {
            log.warn("内容为空,跳过写入文件: {}/{}", dirPath, filename);
            return;
        }
        String filePath = dirPath + File.separator + filename;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
        log.info("文件写入成功: {} (大小: {} bytes)", filePath, content.length());
    }
}
