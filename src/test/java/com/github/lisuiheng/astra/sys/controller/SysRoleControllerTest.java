package com.github.lisuiheng.astra.sys.controller;

import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.mybatis.core.page.PageQuery;
import com.github.lisuiheng.astra.sys.domain.bo.SysRoleBo;
import com.github.lisuiheng.astra.sys.domain.vo.SysRoleVo;
import com.github.lisuiheng.astra.sys.service.ISysRoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class SysRoleControllerTest {

    @Mock
    private ISysRoleService roleService;

    @InjectMocks
    private SysRoleController sysRoleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testList() {
        // 准备测试数据
        SysRoleBo role = new SysRoleBo();
        PageQuery pageQuery = new PageQuery();

        TableDataInfo<SysRoleVo> expected = new TableDataInfo<>();
        expected.setRows(Arrays.asList(new SysRoleVo()));

        when(roleService.selectPageRoleList(any(SysRoleBo.class), any(PageQuery.class)))
                .thenReturn(expected);

        // 执行测试
        TableDataInfo<SysRoleVo> result = sysRoleController.list(role, pageQuery);

        // 验证结果
        assertNotNull(result);
        verify(roleService, times(1)).selectPageRoleList(any(SysRoleBo.class), any(PageQuery.class));
    }

    @Test
    void testGetInfo() {
        // 准备测试数据
        Long roleId = 1L;
        SysRoleVo expected = new SysRoleVo();
        expected.setRoleId(roleId);

        when(roleService.selectRoleById(anyLong())).thenReturn(expected);
        doNothing().when(roleService).checkRoleDataScope(anyLong());

        // 执行测试
        var result = sysRoleController.getInfo(roleId);

        // 验证结果
        assertNotNull(result);
        assertEquals(expected, result.getData());
        verify(roleService, times(1)).selectRoleById(anyLong());
        verify(roleService, times(1)).checkRoleDataScope(anyLong());
    }
}