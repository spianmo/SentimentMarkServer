package com.kirito666.marktoolsserver.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author Finger
 * @Description 操作日志记录实体
 * @date 2020/7/23 11:29
 */
@Data
@ToString
@NoArgsConstructor
@Document(collection = "systemLog")
public class BaseLog {

    /**
     *
     */
    private static final long serialVersionUID = 4552409445309519452L;

    private String id;
    private Date createTime;

    /**
     * 方法名
     */
    private String method;
    /**
     * 参数
     */
    private String args;
    /**
     * 操作人id
     */
    private String userId;
    /**
     * 日志描述
     */
    private String describe;
    /**
     * 操作模块
     */
    private String operationType;
    /**
     * 方法运行时间
     */
    private Long runTime;
    /**
     * 方法返回值
     */
    private String returnValue;
}
