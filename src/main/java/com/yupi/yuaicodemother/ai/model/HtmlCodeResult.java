package com.yupi.yuaicodemother.ai.model;


import dev.langchain4j.model.output.structured.Description;
import lombok.Data;
/**
 * HTML 代码结果
 */
@Description("HTML 代码结果"    )
@Data
public class HtmlCodeResult {
    @Description("HTML 代码")
    private String htmlCode;
    @Description("代码描述")
    private String description;
}
