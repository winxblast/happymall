package top.winxblast.happymall.service;

import com.github.pagehelper.PageInfo;
import top.winxblast.happymall.common.ServerResponse;
import top.winxblast.happymall.pojo.Product;
import top.winxblast.happymall.vo.ProductDetailVo;

/**
 * 商品相关服务
 *
 * @author winxblast
 * @create 2017/11/02
 **/
public interface ProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

}
