package service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.SiteHotStock;
import entity.common.Response;
import enums.StockTypeEnum;
import mapper.SiteHotStockMapper;
import service.SiteHotStockService;
import service.SysParamConfigService;
import utils.SinaApi;
import utils.StringUtil;
import vo.common.StockQuotesVO;
import vo.server.SearchStockResultVO;

/**
 * <p>
 * 站点热门股票 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-12-10
 */
@Service
public class SiteHotStockServiceImpl extends ServiceImpl<SiteHotStockMapper, SiteHotStock> implements SiteHotStockService {
	@Resource
	private SiteHotStockMapper siteHotStockMapper;
	
	@Resource
	private SysParamConfigService sysParamConfigService;

	@Override
	public  Response<List<SiteHotStock>> hotSearchStockList() {
		List<SiteHotStock> list = this.lambdaQuery().orderByDesc(SiteHotStock::getSearchTimes).last("limit 30").list();
		if(list.size() > 0) {
			List<String> stockGids = new ArrayList<>();
			List<String> hkOrUsStockGids = new ArrayList<>();
			list.forEach(i->{
				if(i.getStockType().equals(StockTypeEnum.BJ.getCode()) 
						|| i.getStockType().equals(StockTypeEnum.SZ.getCode()) 
						|| i.getStockType().equals(StockTypeEnum.SH.getCode())) {
					stockGids.add(i.getStockType() + i.getStockCode());
				} else {
					hkOrUsStockGids.add(i.getStockType() + i.getStockCode());
				}
			});
			if(stockGids.size() > 0) {
				 List<StockQuotesVO> stockQuotesVOList = SinaApi.getSinaStocks(stockGids);
				 list.forEach(i-> {
					 stockQuotesVOList.forEach(stockQuotesVO-> {
						 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
							 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
						     i.setNowPrice(stockQuotesVO.getNowPrice());
						 }
					 });
				 });
			}
			if(hkOrUsStockGids.size() > 0) {
				 List<StockQuotesVO> stockQuotesVOList = sysParamConfigService.getStockRealTimeList(hkOrUsStockGids);
				 list.forEach(i-> {
					 stockQuotesVOList.forEach(stockQuotesVO-> {
						 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
							 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
						     i.setNowPrice(stockQuotesVO.getNowPrice());
						 }
					 });
				 });
			}
		}
		return Response.successData(list);
	}

	@Override
	public Response<Page<SearchStockResultVO>> stockResultByKeywords(Integer userId, String keywords, Integer pageNo, Integer pageSize) {
		Page<SearchStockResultVO> page = new Page<>(pageNo, pageSize);
		if(!StringUtil.isEmpty(keywords)) {
			siteHotStockMapper.stockResultByKeywords(page, keywords, userId);
			List<SearchStockResultVO> list = page.getRecords();
			if(list.size() > 0) {
				List<String> stockGids = new ArrayList<>();
				List<String> hkOrUsStockGids = new ArrayList<>();
				list.forEach(i->{
					if(i.getStockType().equals(StockTypeEnum.BJ.getCode()) 
							|| i.getStockType().equals(StockTypeEnum.SZ.getCode()) 
							|| i.getStockType().equals(StockTypeEnum.SH.getCode())) {
						stockGids.add(i.getStockType() + i.getStockCode());
					} else {
						hkOrUsStockGids.add(i.getStockType() + i.getStockCode());
					}
				});
				if(stockGids.size() > 0) {
					 List<StockQuotesVO> stockQuotesVOList = SinaApi.getSinaStocks(stockGids);
					 list.forEach(i-> {
						 stockQuotesVOList.forEach(stockQuotesVO-> {
							 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
								 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
							     i.setNowPrice(stockQuotesVO.getNowPrice());
							 }
						 });
					 });
				}
				if(hkOrUsStockGids.size() > 0) {
					 List<StockQuotesVO> stockQuotesVOList = sysParamConfigService.getStockRealTimeList(hkOrUsStockGids);
					 list.forEach(i-> {
						 stockQuotesVOList.forEach(stockQuotesVO-> {
							 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
								 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
							     i.setNowPrice(stockQuotesVO.getNowPrice());
							 }
						 });
					 });
				}
			}
		}
		return Response.successData(page);
	}

}
