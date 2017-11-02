package top.winxblast.happymall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.winxblast.happymall.common.ServerResponse;
import top.winxblast.happymall.dao.CategoryMapper;
import top.winxblast.happymall.pojo.Category;
import top.winxblast.happymall.service.CategoryService;

import java.util.List;
import java.util.Set;

/**
 * 分类模块接口实现类
 *
 * @author winxblast
 * @create 2017/10/28
 **/
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加品类
     * @param categoryName 品类名称
     * @param parentId 父品类id，SpringMVC中默认使用0，如果没有传入
     * @return
     */
    @Override
    public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
        if(parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//表示这个分类是可用的，有效的

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0) {
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    /**
     * 修改品类名字
     * @param categoryId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName) {
        if(categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0) {
            return ServerResponse.createBySuccessMessage("更改品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更改品类名字失败");
    }

    /**
     * 仅获取下一级分类
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)) {
            //这里没有子分类为什么打印日志比较好呢，应该可以有其他处理方法，这里按照老师的讲解来
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本节点id和孩子节点id
     * 和下面一个private方法联合使用
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId) {
        //这个跟我平时不一样，用的谷歌的一个包来初始化，里面也有很多方便的工具
        Set<Category> categorySet = Sets.newHashSet();
        findChildCatgory(categorySet,categoryId);

        //同样是谷歌guava里的方法
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null) {
            for (Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }
    /**
     * 这里要重写Category的hashcode和equals方法
     * 递归算法算出子节点
     */
    private Set<Category> findChildCatgory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null) {
            categorySet.add(category);
        }

        //查找子节点，递归算法一定要有停止条件
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        //由于mybatis的设计，如果没有查询结果，也不会返回null，所以下面不用进行null判断
        for (Category categoryItem : categoryList) {
            findChildCatgory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }

}
