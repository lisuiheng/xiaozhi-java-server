package com.github.lisuiheng.astra.sys.controller;

import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.sys.entity.WorkflowCategory;
import com.github.lisuiheng.astra.sys.service.IWorkflowCategoryService;
import com.github.lisuiheng.astra.server.ai.model.dto.WorkflowCategoryDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作流分类控制器
 */
@RestController
@RequestMapping("/workflow/category")
public class WorkflowCategoryController {

    @Autowired
    private IWorkflowCategoryService workflowCategoryService;

    /**
     * 查询工作流分类列表
     */
    @GetMapping("/list")
    public R<List<WorkflowCategoryDto>> listCategories() {
        List<WorkflowCategory> categories = workflowCategoryService.listCategories();
        
        List<WorkflowCategoryDto> categoryDtos = categories.stream()
                .map(entity -> {
                    WorkflowCategoryDto dto = new WorkflowCategoryDto();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                })
                .collect(Collectors.toList());
                
        return R.ok(categoryDtos);
    }

    /**
     * 根据ID查询工作流分类
     */
    @GetMapping("/{categoryId}")
    public R<WorkflowCategoryDto> getCategory(@PathVariable Long categoryId) {
        WorkflowCategory category = workflowCategoryService.getCategoryById(categoryId);
        if (category == null) {
            return R.fail("分类不存在");
        }
        
        WorkflowCategoryDto dto = new WorkflowCategoryDto();
        BeanUtils.copyProperties(category, dto);
        
        return R.ok(dto);
    }

    /**
     * 新增工作流分类
     */
    @PostMapping
    public R<Void> addCategory(@RequestBody WorkflowCategoryDto categoryDto) {
        WorkflowCategory category = new WorkflowCategory();
        BeanUtils.copyProperties(categoryDto, category);
        
        workflowCategoryService.insertCategory(category);
        
        return R.ok();
    }

    /**
     * 修改工作流分类
     */
    @PutMapping
    public R<Void> updateCategory(@RequestBody WorkflowCategoryDto categoryDto) {
        WorkflowCategory category = new WorkflowCategory();
        BeanUtils.copyProperties(categoryDto, category);
        
        workflowCategoryService.updateCategory(category);
        
        return R.ok();
    }

    /**
     * 删除工作流分类
     */
    @DeleteMapping("/{categoryId}")
    public R<Void> deleteCategory(@PathVariable Long categoryId) {
        workflowCategoryService.deleteCategoryById(categoryId);
        
        return R.ok();
    }
    
    /**
     * 获取流程分类树列表
     */
    @GetMapping("/categoryTree")
    public R<List<CategoryTreeDto>> categoryTree() {
        List<WorkflowCategory> categories = workflowCategoryService.listCategories();
        
        // 构建树形结构
        List<CategoryTreeDto> treeDtos = buildTree(categories, 0L);
        
        return R.ok(treeDtos);
    }
    
    /**
     * 构建树形结构的辅助方法
     */
    private List<CategoryTreeDto> buildTree(List<WorkflowCategory> categories, Long parentId) {
        return categories.stream()
                .filter(category -> {
                    // 处理根节点的情况 (parentId 为 null 或者为 0)
                    if (parentId == 0L) {
                        return category.getParentId() == null || category.getParentId().equals(0L);
                    } else {
                        return category.getParentId() != null && category.getParentId().equals(parentId);
                    }
                })
                .map(category -> {
                    CategoryTreeDto dto = new CategoryTreeDto();
                    dto.setId(category.getCategoryId());
                    dto.setLabel(category.getCategoryName());
                    dto.setParentId(category.getParentId());
                    dto.setWeight(category.getOrderNum() != null ? category.getOrderNum() : 0L);
                    
                    // 递归构建子节点
                    List<CategoryTreeDto> children = buildTree(categories, category.getCategoryId());
                    if (!children.isEmpty()) {
                        dto.setChildren(children);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 树形结构DTO
     */
    public static class CategoryTreeDto {
        private Long id;
        private String label;
        private Long parentId;
        private Long weight;
        private List<CategoryTreeDto> children;
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getLabel() {
            return label;
        }
        
        public void setLabel(String label) {
            this.label = label;
        }
        
        public Long getParentId() {
            return parentId;
        }
        
        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }
        
        public Long getWeight() {
            return weight;
        }
        
        public void setWeight(Long weight) {
            this.weight = weight;
        }
        
        public List<CategoryTreeDto> getChildren() {
            return children;
        }
        
        public void setChildren(List<CategoryTreeDto> children) {
            this.children = children;
        }
    }
}