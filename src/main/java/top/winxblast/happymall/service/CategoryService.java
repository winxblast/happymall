package top.winxblast.happymall.service;

import top.winxblast.happymall.common.ServerResponse;
import top.winxblast.happymall.pojo.Category;

import java.util.List;

/**
 * 分类操作模块接口
 *
 * @author winxblast
 * @create 2017/10/28
 **/
public interface CategoryService {

    ServerResponse<String> addCategory(String categoryName, Integer parentId);

    ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);

}
