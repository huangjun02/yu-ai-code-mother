package com.yupi.yuaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新自己应用请求（仅支持修改应用名称）
 *
 */
@Data
public class AppUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    @Serial
    private static final long serialVersionUID = 1L;
}
