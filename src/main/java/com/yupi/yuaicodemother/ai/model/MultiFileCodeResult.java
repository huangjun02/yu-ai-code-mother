package com.yupi.yuaicodemother.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * 多文件代码结果
 */
@Description("多文件代码结果")
@Data
public class MultiFileCodeResult {
    @Description("HTML 代码")
    private String htmlCode;
    @Description("CSS 样式代码")
    private String cssCode;
    @Description("JavaScript 代码")
    private String jsCode;
    @Description("代码描述")
    private String description;
}
