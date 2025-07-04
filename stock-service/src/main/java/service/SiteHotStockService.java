package service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.SiteHotStock;
import entity.common.Response;
import vo.server.SearchStockResultVO;

/**
 * <p>
 * 站点热门股票 服务类
 * </p>
 *
 * @author 
 * @since 2024-12-10
 */
public interface SiteHotStockService extends IService<SiteHotStock> {
	 Response<List<SiteHotStock>> hotSearchStockList();
	 
	 Response<Page<SearchStockResultVO>> stockResultByKeywords(Integer userId, String keywords, Integer pageNo, Integer pageSize);
}
