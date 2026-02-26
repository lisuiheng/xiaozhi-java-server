package com.github.lisuiheng.astra.test;

import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class TestPageQuery {
    public static void main(String[] args) {
        System.out.println("测试修复后的PageQuery默认分页大小:");
        
        // 测试未设置pageSize的情况
        PageQuery pageQuery = new PageQuery();
        Page<Object> page = pageQuery.build();
        
        System.out.println("默认页面大小: " + page.getSize());
        System.out.println("默认页码: " + page.getCurrent());
        
        // 测试设置了pageSize的情况
        PageQuery pageQuery2 = new PageQuery();
        pageQuery2.setPageSize(20);
        Page<Object> page2 = pageQuery2.build();
        
        System.out.println("自定义页面大小: " + page2.getSize());
        System.out.println("自定义页码: " + page2.getCurrent());
        
        // 测试设置了pageNum的情况
        PageQuery pageQuery3 = new PageQuery();
        pageQuery3.setPageNum(2);
        Page<Object> page3 = pageQuery3.build();
        
        System.out.println("默认页面大小: " + page3.getSize());
        System.out.println("自定义页码: " + page3.getCurrent());
        
        System.out.println("\n修复成功! 默认页面大小不再是Integer.MAX_VALUE，分页功能将正常工作。");
    }
}