package com.wsn.cow.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller基类
 * 提供通用功能和日志记录
 */
public abstract class BaseController {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 获取当前页码（默认1）
     */
    protected int getPageNum(Integer pageNum) {
        return (pageNum == null || pageNum < 1) ? 1 : pageNum;
    }
    
    /**
     * 获取每页大小（默认10，最大100）
     */
    protected int getPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }
}
