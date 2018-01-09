package top.winxblast.happymall.controller.portal;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.winxblast.happymall.common.ServerResponse;
import top.winxblast.happymall.service.ProductService;
import top.winxblast.happymall.vo.ProductDetailVo;

/**
 * 前台商品controller
 *
 * @author winxblast
 * @create 2017/11/13
 **/
@Controller
@RequestMapping(value = "/product/")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 返回商品详情，与后台不同的是，这里不需要验证管理员权限，而是要验证商品上下架状态
     * @param productId
     * @return
     */
    @RequestMapping(value = "detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId) {
        return productService.getProductDetail(productId);
    }

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false) String keyword,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pageNum",defaultValue = "10")int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "10")String orderBy){
        return productService.getProductByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }

}
