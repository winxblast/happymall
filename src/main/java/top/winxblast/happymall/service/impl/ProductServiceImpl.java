package top.winxblast.happymall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import top.winxblast.happymall.common.Const;
import top.winxblast.happymall.common.ResponseCode;
import top.winxblast.happymall.common.ServerResponse;
import top.winxblast.happymall.dao.CategoryMapper;
import top.winxblast.happymall.dao.ProductMapper;
import top.winxblast.happymall.pojo.Category;
import top.winxblast.happymall.pojo.Product;
import top.winxblast.happymall.service.ProductService;
import top.winxblast.happymall.util.DateTimeUtil;
import top.winxblast.happymall.util.PropertiesUtil;
import top.winxblast.happymall.vo.ProductDetailVo;
import top.winxblast.happymall.vo.ProductListVo;

import java.util.List;

/**
 * 商品服务接口实现类
 *
 * @author winxblast
 * @create 2017/11/02
 **/
@Service(value = "productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增或者更新产品，这个在前端需要分开两个功能，在后端可以在一个方法中实现
     * @param product
     * @return
     */
    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if(product == null) {
            return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
        }

        //如果子图不为空，就取第一张图片作为主图
        if(StringUtils.isNotBlank(product.getSubImages())) {
            String[] subImageArray = product.getSubImages().split(",");
            if(subImageArray.length > 0) {
                product.setMainImage(subImageArray[0]);
            }
        }

        //有产品id表示是更新
        if(product.getId() != null) {
            //看到这里，感觉这里没有涉及事务管理啊，可能并发的考虑也不多，后期自己看看能不能加一些相关的内容
            int rowCount = productMapper.updateByPrimaryKey(product);
            if(rowCount > 0) {
                return ServerResponse.createBySuccess("更新产品成功");
            } else {
                return ServerResponse.createByErrorMessage("更新产品失败");
            }
        } else {
            //没有id就新增产品
            int rowCount = productMapper.insert(product);
            if(rowCount > 0) {
                return ServerResponse.createBySuccess("新增产品成功");
            } else {
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }
    }

    /**
     * 商品上下架
     * @param productId
     * @param status
     * @return
     */
    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if(productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount > 0) {
            return ServerResponse.createBySuccess("修改商品销售状态成功");
        } else {
            return ServerResponse.createByErrorMessage("修改商品销售状态失败");
        }
    }

    /**
     * 获得商品详情，后台
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null) {
            return ServerResponse.createByErrorMessage("商品已下架或者删除");
        }
        //这里用vo对象--value object
        //业务更复杂：pojo-->bo(business object)-->vo(view object)

        //通过一个私有化的方法来组装这个对象
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }
    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        //下面我不能理解这么麻烦为什么不写到ProductDetailVo的一个构造函数中
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setStatus(product.getStatus());

        //imageHost,从配置文件中获取，为了配置和代码分离，为了热部署、配置中心等等
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymall.winxblast.top/"));

        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getId());
        if(category == null) {
            //没有就默认根节点吧，这样也奇怪···先这么定
            productDetailVo.setParentCategoryId(0);
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    /**
     * 获得商品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        //startPage-start
        //填充自己的sql查询逻辑
        //pageHelper-收尾

        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();
        //由于列表也不需要太多的信息，不用把查找到的商品详情全部返回，所以也创建一个VO
        //还有老师这里优化的可能也不是很够，既然不需要那么多信息，那么sql查询的时候就不要查这么多信息
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        /* 这是老师的写法，我感觉直接用productListVoList构造pageinfo就行了
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);*/
        PageInfo pageResult = new PageInfo(productListVoList);
        return ServerResponse.createBySuccess(pageResult);

    }
    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymall.winxblast.top/"));
        return productListVo;
    }

    /**
     * 通过搜索名称或者id来返回商品列表
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if(StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 前台用户获取商品详情，大部分跟后台一致，只不过需要检查商品上下架状态
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if(productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null) {
            return ServerResponse.createByErrorMessage("商品已下架或者删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("商品已下架或者删除");
        }
        //这里用vo对象--value object
        //业务更复杂：pojo-->bo(business object)-->vo(view object)

        //通过一个私有化的方法来组装这个对象
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

}
