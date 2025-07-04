package service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import config.RedisDbTypeEnum;
import entity.SiteHotStock;
import entity.StockDataChangeRecord;
import entity.StockInfo;
import entity.UserFavoriteStock;
import entity.common.Response;
import entity.common.StockParamConfig;
import enums.CurrencyEnum;
import enums.StockDataChangeTypeEnum;
import enums.StockMarketTypeEnum;
import enums.StockTypeEnum;
import mapper.StockInfoMapper;
import redis.RedisKeyPrefix;
import service.IpAddressService;
import service.SiteHotStockService;
import service.StockInfoService;
import service.SysParamConfigService;
import service.UserFavoriteStockService;
import utils.BuyAndSellUtils;
import utils.EastMoneyApiAnalysis;
import utils.RedisDao;
import utils.SinaApi;
import utils.StringUtil;
import vo.common.StockQuotesVO;
import vo.manager.AddStockParamVO;
import vo.manager.EditStockParamVO;
import vo.manager.StockListSearchParamVO;
import vo.server.BlockTradingStockInfoVO;
import vo.server.BlockTradingStockListVO;
import vo.server.IndexHotRankVO;
import vo.server.StockData;
import vo.server.StockDayHotRankVO;
import vo.server.StockDetailVO;

/**
 * <p>
 * 股票产品信息表 服务实现类
 * </p>
 *
 * @author
 * @since 2024-11-07
 */
@Service
public class StockInfoServiceImpl extends ServiceImpl<StockInfoMapper, StockInfo> implements StockInfoService {

	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private StockInfoMapper stockInfoMapper;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private UserFavoriteStockService userFavoriteStockService;
	
	@Resource
	private RedisDao redisDao;
	
	@Resource
	private SiteHotStockService siteHotStockService;

	@Override
	public void managerStockList(Page<StockInfo> page, StockListSearchParamVO param) {
		stockInfoMapper.managerStockList(page, param);
		List<StockInfo> list = page.getRecords();
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
					     i.setHeightPrice(stockQuotesVO.getHeightPrice());
					     i.setLowPrice(stockQuotesVO.getLowPrice());
					     i.setOpenPrice(stockQuotesVO.getOpenPrice());
					     i.setVolume(stockQuotesVO.getVolume());
					     i.setTurnover(stockQuotesVO.getTurnover());
					     i.setPrevClose(stockQuotesVO.getPrevClose());
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
					     i.setHeightPrice(stockQuotesVO.getHeightPrice());
					     i.setLowPrice(stockQuotesVO.getLowPrice());
					     i.setOpenPrice(stockQuotesVO.getOpenPrice());
					     i.setVolume(stockQuotesVO.getVolume());
					     i.setTurnover(stockQuotesVO.getTurnover());
					     i.setPrevClose(stockQuotesVO.getPrevClose());
					 }
				 });
			 });
		}
	}
	
	@Override
	@Transactional
	public Response<Void> managerAddStock(AddStockParamVO param, String ip, String operator) {
		if (StringUtil.isEmpty(param.getStockName())) {
			return Response.fail("请输入股票名称");
		}
		if (StringUtil.isEmpty(param.getStockCode())) {
			return Response.fail("请输入股票代码");
		}
		if (StringUtil.isEmpty(param.getStockType())) {
			return Response.fail("请选择股票类型");
		}
		if(StockTypeEnum.getNameByCode(param.getStockType()) == null) {
			return Response.fail("股票类型错误");
		}
		BigDecimal spreadRate = param.getSpreadRate() == null ? BigDecimal.ZERO : param.getSpreadRate();
		if(spreadRate.compareTo(BigDecimal.ZERO) < 0) {
			return Response.fail("点差费率值错误");
		}
		if(param.getIsBlockTrading() != null && param.getIsBlockTrading()) {
			if(param.getBlockTradingPrice() == null || param.getBlockTradingPrice().compareTo(BigDecimal.ZERO) < 1) {
				return Response.fail("请输入大宗交易增发价格");
			}
			if(param.getBlockTradingNum() == null || param.getBlockTradingNum() <= 0) {
				return Response.fail("请输入大宗交易增发股数");
			}
			if(param.getBlockTradingBuyingMinNum() == null || param.getBlockTradingBuyingMinNum() <= 0) {
				return Response.fail("请输入大宗交易最低买入股数");
			}
			if(StringUtil.isEmpty(param.getBlockTradingPwd())) {
				return Response.fail("请输入大宗交易验证口令");
			}
		}
		if (this.lambdaQuery().eq(StockInfo::getStockName, param.getStockName()).count() > 0) {
			return Response.fail("股票名称已存在，请重新输入");
		}
		if (this.lambdaQuery().eq(StockInfo::getStockCode, param.getStockCode()).count() > 0) {
			return Response.fail("股票代码已存在，请重新输入");
		}
		StockInfo s = new StockInfo();
		s.setStockName(param.getStockName());
		s.setStockCode(param.getStockCode());
		s.setStockPlate(param.getStockPlate());
		s.setStockType(param.getStockType());
		s.setIsLock(param.getIsLock());
		s.setIsShow(param.getIsShow());
		s.setIsBlockTrading(param.getIsBlockTrading());
		s.setBlockTradingPrice(param.getBlockTradingPrice());
		s.setBlockTradingNum(param.getBlockTradingNum());
		s.setBlockTradingBuyingMinNum(param.getBlockTradingBuyingMinNum());
		s.setLockInPeriod(param.getLockInPeriod());
		s.setSpreadRate(spreadRate);
		s.setCreator(operator);
		s.setBlockTradingPwd(param.getBlockTradingPwd());
		s.insert();
		StockDataChangeRecord stockDataChangeRecord = new StockDataChangeRecord();
		stockDataChangeRecord.setStockId(s.getId());
		stockDataChangeRecord.setStockCode(s.getStockCode());
		stockDataChangeRecord.setStockName(s.getStockName());
		stockDataChangeRecord.setDataChangeTypeCode(StockDataChangeTypeEnum.ADD_STOCK.getCode());
		stockDataChangeRecord.setDataChangeTypeName(StockDataChangeTypeEnum.ADD_STOCK.getName());
		stockDataChangeRecord.setNewContent(s.toString());
		stockDataChangeRecord.setIp(ip);
		stockDataChangeRecord.setIpAddress(ipAddressService.getIpAddress(ip).getAddress2());
		stockDataChangeRecord.setOperator(operator);
		stockDataChangeRecord.insert();
		return Response.success();
	}

	@Override
	@Transactional
	public Response<Void> managerEditStock(EditStockParamVO param, String ip, String operator) {
		if (StringUtil.isEmpty(param.getStockName())) {
			return Response.fail("请输入股票名称");
		}
		StockInfo old = this.getById(param.getId());
		if (old == null) {
			return Response.fail("股票信息错误");
		}
		BigDecimal spreadRate = param.getSpreadRate() == null ? BigDecimal.ZERO : param.getSpreadRate();
		if(spreadRate.compareTo(BigDecimal.ZERO) < 0) {
			return Response.fail("点差费率值错误");
		}
		if(param.getIsBlockTrading() != null && param.getIsBlockTrading()) {
			if(param.getBlockTradingPrice() == null || param.getBlockTradingPrice().compareTo(BigDecimal.ZERO) < 1) {
				return Response.fail("请输入大宗交易增发价格");
			}
			if(param.getBlockTradingNum() == null || param.getBlockTradingNum() <= 0) {
				return Response.fail("请输入大宗交易增发股数");
			}
			if(param.getBlockTradingBuyingMinNum() == null || param.getBlockTradingBuyingMinNum() <= 0) {
				return Response.fail("请输入大宗交易最低买入股数");
			}
			if(StringUtil.isEmpty(param.getBlockTradingPwd())) {
				return Response.fail("请输入大宗交易验证口令");
			}
		}
		if (this.lambdaQuery().eq(StockInfo::getStockName, param.getStockName()).ne(StockInfo::getId, param.getId())
				.count() > 0) {
			return Response.fail("股票名称已存在，请重新输入");
		}
		LambdaUpdateWrapper<StockInfo> luw = new LambdaUpdateWrapper<>();
		luw.eq(StockInfo::getId, param.getId());
		StringBuilder oldContent = new StringBuilder();
		StringBuilder newContent = new StringBuilder();
		if (!old.getStockName().equals(param.getStockName())) {
			oldContent.append("股票名称：").append(old.getStockName());
			newContent.append("股票名称：").append(param.getStockName());
			luw.set(StockInfo::getStockName, param.getStockName());
		}
		if (StringUtil.isEmpty(old.getStockPlate())) {
			if (!StringUtil.isEmpty(param.getStockPlate())) {
				oldContent.append("\n板块：");
				newContent.append("\n板块：").append(param.getStockPlate());
				luw.set(StockInfo::getStockPlate, param.getStockPlate());
			}
		} else {
			if (StringUtil.isEmpty(param.getStockPlate())) {
				oldContent.append("\n板块：").append(old.getStockPlate());
				newContent.append("\n板块：");
				luw.set(StockInfo::getStockPlate, "");
			} else if (!old.getStockPlate().equals(param.getStockPlate())) {
				oldContent.append("\n板块：").append(old.getStockPlate());
				newContent.append("\n板块：").append(param.getStockPlate());
				luw.set(StockInfo::getStockPlate, param.getStockPlate());
			}
		}
		if (param.getIsLock() != null && old.getIsLock() == !param.getIsLock()) {
			oldContent.append("\n是否锁定：").append(old.getIsLock() ? "是" : "否");
			newContent.append("\n是否锁定：").append(param.getIsLock() ? "是" : "否");
			luw.set(StockInfo::getIsLock, param.getIsLock());
		}
		if (param.getIsShow() != null && old.getIsShow() == !param.getIsShow()) {
			oldContent.append("\n是否显示：").append(old.getIsShow() ? "是" : "否");
			newContent.append("\n是否显示：").append(param.getIsShow() ? "是" : "否");
			luw.set(StockInfo::getIsShow, param.getIsShow());
		}
		if (param.getIsBlockTrading() != null && old.getIsBlockTrading() == !param.getIsBlockTrading()) {
			oldContent.append("\n是否开启大宗交易：").append(old.getIsBlockTrading() ? "是" : "否");
			newContent.append("\n是否开启大宗交易：").append(param.getIsBlockTrading() ? "是" : "否");
			luw.set(StockInfo::getIsBlockTrading, param.getIsBlockTrading());
		}
		if(param.getBlockTradingNum() == null) {
			param.setBlockTradingNum(0);
		}
		if (!old.getBlockTradingNum().equals(param.getBlockTradingNum())) {
			oldContent.append("\n增发数量：").append(old.getBlockTradingNum());
			newContent.append("\n增发数量：").append(param.getBlockTradingNum());
			luw.set(StockInfo::getBlockTradingNum, param.getBlockTradingNum());
		}
		if(param.getBlockTradingBuyingMinNum() == null) {
			param.setBlockTradingBuyingMinNum(0);
		}
		if(!old.getBlockTradingBuyingMinNum().equals(param.getBlockTradingBuyingMinNum())) {
				oldContent.append("\n大宗交易最低买入股数：").append(old.getBlockTradingBuyingMinNum());
				newContent.append("\n大宗交易最低买入股数：").append(param.getBlockTradingBuyingMinNum());
				luw.set(StockInfo::getBlockTradingBuyingMinNum, param.getBlockTradingBuyingMinNum());
		}
		if(param.getBlockTradingPrice() == null) {
			param.setBlockTradingPrice(BigDecimal.ZERO);
		}
		if (old.getBlockTradingPrice().compareTo(param.getBlockTradingPrice()) != 0) {
			oldContent.append("\n增发价格：").append(old.getBlockTradingPrice());
			newContent.append("\n增发价格：").append(param.getBlockTradingPrice());
			luw.set(StockInfo::getBlockTradingPrice, param.getBlockTradingPrice());
		}
		if (old.getLockInPeriod() == null) {
			if (param.getLockInPeriod() != null) {
				oldContent.append("\n锁仓天数：");
				newContent.append("\n锁仓天数：").append(param.getLockInPeriod());
				luw.set(StockInfo::getLockInPeriod, param.getLockInPeriod());
			}
		} else if (param.getLockInPeriod() != null && !old.getLockInPeriod().equals(param.getLockInPeriod())) {
			oldContent.append("\n锁仓天数：").append(old.getLockInPeriod());
			newContent.append("\n锁仓天数：").append(param.getLockInPeriod());
			luw.set(StockInfo::getLockInPeriod, param.getLockInPeriod());
		}
		
		if(spreadRate.compareTo(old.getSpreadRate()) != 0) {
			oldContent.append("\n点差费率：").append(old.getSpreadRate());
			newContent.append("\n点差费率：").append(spreadRate);
			luw.set(StockInfo::getSpreadRate, spreadRate);
		}
		if(StringUtil.isEmpty(old.getBlockTradingPwd())) {
			if(!StringUtil.isEmpty(param.getBlockTradingPwd())) {
				oldContent.append("\n大宗交易验证口令：");
				newContent.append("\n大宗交易验证口令：").append(param.getBlockTradingPwd());
				luw.set(StockInfo::getBlockTradingPwd, param.getBlockTradingPwd());
			}
		} else {
			if(StringUtil.isEmpty(param.getBlockTradingPwd())) {
				oldContent.append("\n大宗交易验证口令：").append(old.getBlockTradingPwd());
				newContent.append("\n大宗交易验证口令：");
				luw.set(StockInfo::getBlockTradingPwd, param.getBlockTradingPwd());
			} else if(!old.getBlockTradingPwd().equals(param.getBlockTradingPwd())) {
				oldContent.append("\n大宗交易验证口令：").append(old.getBlockTradingPwd());
				newContent.append("\n大宗交易验证口令：").append(param.getBlockTradingPwd());
				luw.set(StockInfo::getBlockTradingPwd, param.getBlockTradingPwd());
			}
		}
		if(!StringUtil.isEmpty(luw.getSqlSet())) {
			this.update(luw);
			StockDataChangeRecord stockDataChangeRecord = new StockDataChangeRecord();
			stockDataChangeRecord.setStockId(old.getId());
			stockDataChangeRecord.setStockCode(old.getStockCode());
			stockDataChangeRecord.setStockName(old.getStockName());
			stockDataChangeRecord.setDataChangeTypeCode(StockDataChangeTypeEnum.EDIT_STOCK.getCode());
			stockDataChangeRecord.setDataChangeTypeName(StockDataChangeTypeEnum.EDIT_STOCK.getName());
			stockDataChangeRecord.setOldContent(oldContent.toString());
			stockDataChangeRecord.setNewContent(newContent.toString());
			stockDataChangeRecord.setIp(ip);
			stockDataChangeRecord.setIpAddress(ipAddressService.getIpAddress(ip).getAddress2());
			stockDataChangeRecord.setOperator(operator);
			stockDataChangeRecord.insert();
		}
		return Response.success();
	}

	@Override
	public Response<Void> managerDeleteStock(Integer id, String ip, String operator) {
		StockInfo old = this.getById(id);
		if (old == null) {
			return Response.fail("股票信息错误");
		}
		old.deleteById();
		StockDataChangeRecord stockDataChangeRecord = new StockDataChangeRecord();
		stockDataChangeRecord.setStockId(old.getId());
		stockDataChangeRecord.setStockCode(old.getStockCode());
		stockDataChangeRecord.setStockName(old.getStockName());
		stockDataChangeRecord.setDataChangeTypeCode(StockDataChangeTypeEnum.DELETE_STOCK.getCode());
		stockDataChangeRecord.setDataChangeTypeName(StockDataChangeTypeEnum.DELETE_STOCK.getName());
		stockDataChangeRecord.setOldContent(old.toString());
		stockDataChangeRecord.setIp(ip);
		stockDataChangeRecord.setIpAddress(ipAddressService.getIpAddress(ip).getAddress2());
		stockDataChangeRecord.setOperator(operator);
		stockDataChangeRecord.insert();
		return Response.success();
	}

	@Override
	public Response<IndexHotRankVO> getHotRankFromApi() {
		IndexHotRankVO ret = new IndexHotRankVO();
		StockDayHotRankVO marketA = redisDao.getBean(RedisDbTypeEnum.STOCK_API, RedisKeyPrefix.getAStockHotRankKey(), StockDayHotRankVO.class);
		StockDayHotRankVO marketHk = redisDao.getBean(RedisDbTypeEnum.STOCK_API, RedisKeyPrefix.getHkStockHotRankKey(), StockDayHotRankVO.class);
		StockDayHotRankVO marketUs = redisDao.getBean(RedisDbTypeEnum.STOCK_API, RedisKeyPrefix.getUsStockHotRankKey(), StockDayHotRankVO.class);
		
		if (marketA != null) {
			List<String> stockGids = new ArrayList<>();
			marketA.getList().forEach(i->{
				stockGids.add(i.getStockType() + i.getStockCode());
			});
			List<StockQuotesVO> stockQuotesVOList = SinaApi.getSinaStocks(stockGids);
			marketA.getList().forEach(i-> {
				 stockQuotesVOList.forEach(stockQuotesVO-> {
					 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
					     i.setNowPrice(stockQuotesVO.getNowPrice());
					     i.setStockName(stockQuotesVO.getStockName());
					 }
				 });
			 });
			ret.setMarketAList(marketA);
			ret.setDisplayFirst(marketA.getList().get(0));
		}
		
		if (marketHk != null) {
			List<String> stockGids = new ArrayList<>();
			marketHk.getList().forEach(i-> {
				stockGids.add(i.getStockType() + i.getStockCode());
			});
			List<StockQuotesVO> stockQuotesVOList =  sysParamConfigService.getStockRealTimeList(stockGids);
			marketHk.getList().forEach(i-> {
				 stockQuotesVOList.forEach(stockQuotesVO-> {
					 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
					     i.setNowPrice(stockQuotesVO.getNowPrice());
					     i.setStockName(stockQuotesVO.getStockName());
					 }
				 });
			 });
			ret.setMarketHkList(marketHk);
			ret.setDisplaySecond(marketHk.getList().get(0));
		}
		
		if (marketUs != null) {
			List<String> stockGids = new ArrayList<>();
			marketUs.getList().forEach(i-> {
				stockGids.add(i.getStockType() + i.getStockCode());
			});
			List<StockQuotesVO> stockQuotesVOList =  sysParamConfigService.getStockRealTimeList(stockGids);
			marketUs.getList().forEach(i-> {
				 stockQuotesVOList.forEach(stockQuotesVO-> {
					 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
					     i.setNowPrice(stockQuotesVO.getNowPrice());
					     i.setStockName(stockQuotesVO.getStockName());
					 }
				 });
			 });
			ret.setMarketUsList(marketUs);
			ret.setDisplayThird(marketUs.getList().get(0));
		}
		return Response.successData(ret);
	}
	
	/**
	 * 短期活跃股票
	 */
	@Override
	public Page<StockData> getActiveStockListFromApi(Integer pageNo, Integer pageSize,Integer stockMarketTypeCode) {
		Page<StockData> page = new Page<>(pageNo, pageSize);
		StockMarketTypeEnum stockMarketTypeEnum = StockMarketTypeEnum.getByCode(stockMarketTypeCode);
		switch(stockMarketTypeEnum) {
		case Market_Hk://港股,成交量排序
			page = getStockQuoteListPage(pageNo, pageSize, "turnover", "desc", RedisKeyPrefix.getHkStockQuoteListKey());
			break;
		case Market_Us://美股，成交量排序
			page = getStockQuoteListPage(pageNo, pageSize, "turnover", "desc", RedisKeyPrefix.getUsStockQuoteListKey());
			break;
		default ://默认A股，成交量排序
			page = getStockQuoteListPage(pageNo, pageSize, "turnover", "desc", RedisKeyPrefix.getAStockQuoteListKey());
			break;
		}
		return page;
	}

	/**
	 * 获取股票涨幅排行榜分页数据
	 * @param pageNo 
	 * @param pageSize
	 * @param sortTerm 排序条件
	 * @param sortType 排序类型
	 * @param reidsKey 缓存key
	 * @return
	 */
	@Override
	public Page<StockData> getStockQuoteListPage(Integer pageNo, Integer pageSize, String sortTerm, String sortType,
			String redisKey) {
		// 1、从缓存获取涨幅数据
		List<StockData> list = redisDao.getBeanList(RedisDbTypeEnum.STOCK_API, redisKey, StockData.class);
		// 2、排序
		if (!StringUtil.isEmpty(sortTerm)) {
			Comparator<? super StockData> c = null;
			switch (sortTerm) {
			case "nowPrice":// 最新价
				c = Comparator.comparing(StockData::getNowPrice);// 最新价-升序;
				if (sortType.equals("desc")) {
					c = Comparator.comparing(StockData::getNowPrice).reversed();// 最新价-降序;
					// list.sort(Comparator.comparing(StockData::getNowPrice));//最新价-升序;
					// list.sort(Comparator.comparing(StockData::getNowPrice).reversed());//最新价-降序;
				}
				break;
			case "percentageIncrease":// 涨幅比例
				c = Comparator.comparing(StockData::getPercentageIncrease);// 涨幅比例-升序;
				if (sortType.equals("desc")) {
					c = Comparator.comparing(StockData::getPercentageIncrease).reversed();// 涨幅比例-降序;
					// list.sort(Comparator.comparing(StockData::getPercentageIncrease));//涨幅比例-升序;
					// list.sort(Comparator.comparing(StockData::getPercentageIncrease).reversed());//涨幅比例-降序;
				}
				break;
			case "turnover":// 成交量
				// c = Comparator.comparing(StockData::getTurnover);//成交量-升序;
				if (sortType.equals("desc")) {
					c = Comparator.comparing(StockData::getTurnover).reversed();// 成交量-降序;
					// list.sort(Comparator.comparing(StockData::getTurnover));//成交量-升序;
					// list.sort(Comparator.comparing(StockData::getTurnover).reversed());//成交量-降序;
				}
				break;
			default:
				break;
			}
			list.sort(c);
		}
		// 3、封装数据返回结果
		if (pageSize > 100) {
			pageSize = 100;
		}
		Page<StockData> page = new Page<>(pageNo, pageSize);
		if (list == null || list.size() == 0) {
			return page;
		}
		int total = list.size();
		page.setTotal(total);
		int offset = (pageNo - 1) * pageSize;
		int limit = pageNo * pageSize;
		if (limit > total) {
			if (total > offset) {
				page.setRecords(new ArrayList<>(list.subList(offset, total)));
			}
		} else {
			page.setRecords(new ArrayList<>(list.subList(offset, limit)));
		}
		return page;
	}

	@Override
	public Response<Page<BlockTradingStockListVO>> getBlockTradingStockList(Integer pageNo, Integer pageSize) {
		if(pageSize > 100) {
			pageSize = 100;
		}
		Page<BlockTradingStockListVO> page = new Page<>(pageNo, pageSize);
		this.stockInfoMapper.getBlockTradingStockList(page);
		if(page.getRecords().size() > 0) {
			List<String> stockGids = new ArrayList<>();
			List<String> hkOrUsStockGids = new ArrayList<>();
			page.getRecords().forEach(i->{
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
				 page.getRecords().forEach(i-> {
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
				 page.getRecords().forEach(i-> {
					 stockQuotesVOList.forEach(stockQuotesVO-> {
						 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
							 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
						     i.setNowPrice(stockQuotesVO.getNowPrice());
						 }
					 });
				 });
			}
		}
		return Response.successData(page);
	}

	@Override
	public Response<BlockTradingStockInfoVO> getBlockTradingStockInfo(String stockCode, String stockType, String blockTradingPwd) {
		StockInfo s = this.lambdaQuery()
				.eq(StockInfo::getStockType, stockType)
				.eq(StockInfo::getStockCode, stockCode)
				.eq(StockInfo::getIsShow, true)
				.eq(StockInfo::getIsBlockTrading, true).one();
		if(s == null) {
			return Response.fail("产品信息错误");
		}
		if(!s.getBlockTradingPwd().equals(blockTradingPwd)) {
			return Response.fail("专享口令错误");
		}
		BlockTradingStockInfoVO info = new BlockTradingStockInfoVO();
		info.setStockCode(stockCode);
		info.setStockType(stockType);
		info.setStockName(s.getStockName());
		info.setStockPlate(s.getStockPlate());
		info.setBlockTradingPrice(s.getBlockTradingPrice());
		info.setBlockTradingBuyingMinNum(s.getBlockTradingBuyingMinNum());
		info.setBlockTradingNum(s.getBlockTradingNum());
		info.setSoldBlockTradingNum(s.getSoldBlockTradingNum());
		StockParamConfig spc = sysParamConfigService.getSysParamConfig();
		String am_begin = "";
		String am_end = "";
		String pm_begin = "";
		String pm_end = "";
		CurrencyEnum currency = null;
		switch(stockType) {
		case "sh":
		case "sz":
		case "bj":
			am_begin = spc.getBulkA_amTradingStart();
        	am_end = spc.getBulkA_amTradingEnd();
        	pm_begin = spc.getBulkA_pmTradingStart();
        	pm_end = spc.getBulkA_pmTradingEnd();
        	
        	currency = CurrencyEnum.CNY;
        	
			StockQuotesVO q = SinaApi.getSinaStock(stockType, stockCode);
			info.setNowPrice(q.getNowPrice());
			info.setBuyingFeeRate(spc.getMarketABuyingFeeRate());
			info.setBuyingStampDutyRate(BigDecimal.ZERO);
			break;
		case "us":
			am_begin = spc.getBulkUs_amTradingStart();
        	am_end = spc.getBulkUs_amTradingEnd();
        	pm_begin = spc.getBulkUs_pmTradingStart();
        	pm_end = spc.getBulkUs_pmTradingEnd();
        	
        	currency = CurrencyEnum.USD;
        	
			info.setBuyingFeeRate(spc.getMarketUsBuyingFeeRate());
			info.setBuyingStampDutyRate(BigDecimal.ZERO);
			
			info.setNowPrice(EastMoneyApiAnalysis.getStockRealTimeData(stockType, stockCode).getNowPrice());
			break;
		case "hk":
			am_begin = spc.getBulkHk_amTradingStart();
        	am_end = spc.getBulkHk_amTradingEnd();
        	pm_begin = spc.getBulkHk_pmTradingStart();
        	pm_end = spc.getBulkHk_pmTradingEnd();
        	
        	currency = CurrencyEnum.HKD;
        	
			info.setBuyingFeeRate(spc.getMarketHkBuyingFeeRate());
			info.setBuyingStampDutyRate(spc.getMarketHkStampDutyRate());
			info.setNowPrice(EastMoneyApiAnalysis.getStockRealTimeData(stockType, stockCode).getNowPrice());
			break;
		}
		int status = BuyAndSellUtils.getTradingStatus(am_begin, am_end, pm_begin, pm_end, stockType,false);
		info.setBulkStatus(status);
		BigDecimal exchangeRate = sysParamConfigService.getExchangeRate(currency);
		info.setExchangeRate(exchangeRate);
		return Response.successData(info);
	}

	/**
	 * 近1个月涨幅榜
	 */
	@Override
	public List<StockData> getIncreaseRateRank(Integer pageNo, Integer pageSize,Integer stockMarketTypeCode) {
		StockMarketTypeEnum stockMarketTypeEnum = StockMarketTypeEnum.getByCode(stockMarketTypeCode);
		if(stockMarketTypeEnum != null) {
			switch(stockMarketTypeEnum) {
			case Market_Hk://港股,涨幅排序
				return getStockQuoteListPage(pageNo, pageSize, "percentageIncrease", "desc", RedisKeyPrefix.getHkStockQuoteListKey()).getRecords();
			case Market_Us://美股，涨幅排序
				return getStockQuoteListPage(pageNo, pageSize, "percentageIncrease", "desc", RedisKeyPrefix.getUsStockQuoteListKey()).getRecords();
			default ://默认A股，涨幅排序
				return getStockQuoteListPage(pageNo, pageSize, "percentageIncrease", "desc", RedisKeyPrefix.getAStockQuoteListKey()).getRecords();
			}
		}else {
			List<StockData> list = new ArrayList<StockData>();
			list.addAll(getStockQuoteListPage(pageNo, pageSize, "percentageIncrease", "desc", RedisKeyPrefix.getHkStockQuoteListKey()).getRecords());
			list.addAll(getStockQuoteListPage(pageNo, pageSize, "percentageIncrease", "desc", RedisKeyPrefix.getUsStockQuoteListKey()).getRecords());
			list.addAll(getStockQuoteListPage(pageNo, pageSize, "percentageIncrease", "desc", RedisKeyPrefix.getAStockQuoteListKey()).getRecords());
			list.sort(Comparator.comparing(StockData::getPercentageIncrease).reversed());//涨幅比例-降序;
			return list;
		}
	}

	@Override
	public Response<StockDetailVO> getStockDetail(Integer userId, String stockCode, String stockType) {
		try {
			StockInfo si = this.lambdaQuery().eq(StockInfo::getStockCode, stockCode).eq(StockInfo::getStockType, stockType).one();
			StockQuotesVO q;
			String am_begin, am_end, pm_begin, pm_end;
			StockParamConfig stockParamConfig = sysParamConfigService.getSysParamConfig();
			switch(stockType) {
			case "us":
				am_begin = stockParamConfig.getMarketUs_amTradingStart();
	        	am_end = stockParamConfig.getMarketUs_amTradingEnd();
	        	pm_begin = stockParamConfig.getMarketUs_pmTradingStart();
	        	pm_end = stockParamConfig.getMarketUs_pmTradingEnd();
	        	q = EastMoneyApiAnalysis.getStockRealTimeData(stockType, stockCode);
	        	break;
			case "hk":
				am_begin = stockParamConfig.getMarketHk_amTradingStart();
	        	am_end = stockParamConfig.getMarketHk_amTradingEnd();
	        	pm_begin = stockParamConfig.getMarketHk_pmTradingStart();
	        	pm_end = stockParamConfig.getMarketHk_pmTradingEnd();
	        	q = EastMoneyApiAnalysis.getStockRealTimeData(stockType, stockCode);
				break;
			default:
				am_begin = stockParamConfig.getMarketA_amTradingStart();
	        	am_end = stockParamConfig.getMarketA_amTradingEnd();
	        	pm_begin = stockParamConfig.getMarketA_pmTradingStart();
	        	pm_end = stockParamConfig.getMarketA_pmTradingEnd();
				q = SinaApi.getSinaStock(stockType, stockCode);
				break;
			}
			if(q.getGid() == null) {
				return Response.fail("产品信息错误,userI:"+ userId+"stockCode:"+ stockCode+"stockType:"+ stockType);
			}
			//数据库无股票信息数据，则增加一条数据
			if(si == null) {
				si = new StockInfo();
				si.setStockCode(stockCode);
				si.setStockName(q.getStockName());
				si.setStockType(stockType);
				if(stockCode.startsWith("688") && stockCode.equals("sh")) {
					si.setStockPlate("科创");
				} else if(stockCode.equals("sz") && stockCode.startsWith("30")) {
					si.setStockPlate("创业");
				}
				si.setIsShow(true);
				si.setIsLock(false);
				si.setCreator("系统");
				si.insert();
				StockDataChangeRecord stockDataChangeRecord = new StockDataChangeRecord();
				stockDataChangeRecord.setStockId(si.getId());
				stockDataChangeRecord.setStockCode(si.getStockCode());
				stockDataChangeRecord.setStockName(si.getStockName());
				stockDataChangeRecord.setDataChangeTypeCode(StockDataChangeTypeEnum.ADD_STOCK.getCode());
				stockDataChangeRecord.setDataChangeTypeName(StockDataChangeTypeEnum.ADD_STOCK.getName());
				stockDataChangeRecord.setNewContent(si.toString());
				stockDataChangeRecord.setIp("127.0.0.1");
				stockDataChangeRecord.setIpAddress("服务器本机ip");
				stockDataChangeRecord.setOperator("系统");
				stockDataChangeRecord.insert();
			}
			if(!si.getIsShow()) {
				return Response.fail("该产品暂时无法显示");
			}
			StockDetailVO d = new StockDetailVO();
			d.setStockName(si.getStockName());
			d.setStockCode(stockCode);
			d.setStockType(stockType);
			d.setStockPlate(si.getStockPlate());
			d.setIsLock(si.getIsLock());
			d.setPercentageIncrease(q.getPercentageIncrease());
			d.setNowPrice(q.getNowPrice());
			d.setHightPrice(q.getHeightPrice());
			d.setLowPrice(q.getLowPrice());
			d.setOpenPrice(q.getOpenPrice());
			d.setVolume(q.getVolume());
			d.setTurnover(q.getTurnover());
			d.setPrevClose(q.getPrevClose());
			if(userId > 0) {
				int count = userFavoriteStockService.lambdaQuery()
					.eq(UserFavoriteStock::getUserId, userId)
					.eq(UserFavoriteStock::getStockCode, stockCode)
					.eq(UserFavoriteStock::getStockType, stockType).count();
				if(count > 0) {
					d.setIsFavorite(true);
				}
			}
			int status = BuyAndSellUtils.getTradingStatus(am_begin, am_end, pm_begin, pm_end, stockType,true);
			d.setMarketStatus(status);
			if(d.getNowPrice() != null && d.getPrevClose() != null && d.getNowPrice() != BigDecimal.ZERO && d.getPrevClose() != BigDecimal.ZERO) {
				d.setIncrease(d.getNowPrice().subtract(d.getPrevClose()));
			}
			//实时获取一手的股票数
			d.setSharesOfHand(EastMoneyApiAnalysis.getSharesOfHand(stockType, stockCode));
			//增加热门搜索数据
			SiteHotStock siteHotStock = siteHotStockService.lambdaQuery().
			eq(SiteHotStock::getStockCode, d.getStockCode()).
			eq(SiteHotStock::getStockType, d.getStockType()).
			one();
			SiteHotStock siteHotStockNew = new SiteHotStock();
			if (siteHotStock != null) {
				siteHotStockNew.setId(siteHotStock.getId());
				siteHotStockNew.setSearchTimes(siteHotStock.getSearchTimes()+1) ;
			}else {
				siteHotStockNew.setStockName(d.getStockName());
				siteHotStockNew.setStockCode(d.getStockCode());
				siteHotStockNew.setStockType(d.getStockType());
				siteHotStockNew.setStockPlate(d.getStockPlate());
				siteHotStockNew.setSearchTimes(1);
			}
			siteHotStockNew.insertOrUpdate();
			return Response.successData(d);
		} catch (Exception e) {
			return Response.fail("产品信息错误,userI:"+ userId+"stockCode:"+ stockCode+"stockType:"+ stockType);
		}
	}

	/**
	 * 股票数据初始化
	 */
	@Override
	public Response<String> stockDataInitialize() {
		String aCount = stockDataInitializeBystockType(null);
		String usCount = stockDataInitializeBystockType(StockTypeEnum.US.getCode());
		String hkCount = stockDataInitializeBystockType(StockTypeEnum.HK.getCode());
		return Response.success("A股-"+aCount+"/n美股-"+usCount+"/n港股-"+hkCount);
	}
	
	/**
	 * 执行东财API，根据股票类型，获取股票数据【（暂时弃用）访问频繁，会导致连接超时的问题】
	 * @param stockType 股票类型，可选择为（sh：沪股、sz：深股、bj：北证、us：美股、hk：港股），为空则处理所有A股
	 */
	private String stockDataInitializeBystockType(String stockType) {
		List<String> stockTypeIn = new ArrayList<String>();
		if (stockType != null) {
			stockTypeIn.add(stockType);
		}else {//查所有A股数据
			stockTypeIn.add(StockTypeEnum.SH.getCode());
			stockTypeIn.add(StockTypeEnum.SZ.getCode());
			stockTypeIn.add(StockTypeEnum.BJ.getCode());
		}
		int stockInfoCount = this.lambdaQuery().in(StockInfo::getStockType, stockTypeIn).count();
		if (stockInfoCount == 0) {
			List<StockInfo> stockInfoList = (List<StockInfo>) EastMoneyApiAnalysis.getStockList(stockType,StockInfo.class);//执行东财API，根据股票类型-获取-股票列表信息【暂时弃用，因请求频繁，存在访问连接超时问题】
			//List<StockInfo> stockInfoList = NowStockApi.getFinanceStockList(stockType);//执行NOWApi，根据股票类型-获取-股票列表信息
			int processedCount = 0;
			for (StockInfo stockInfo : stockInfoList) {
				if (StringUtil.isNotEmpty(stockInfo.getStockType()) && 
						StringUtil.isNotEmpty(stockInfo.getStockCode()) && 
						StringUtil.isNotEmpty(stockInfo.getStockName())) {
					if(stockInfo.getStockCode().startsWith("688") && stockInfo.getStockCode().equals("sh")) {
						stockInfo.setStockPlate("科创");
					} else if(stockInfo.getStockCode().equals("sz") && stockInfo.getStockCode().startsWith("30")) {
						stockInfo.setStockPlate("创业");
					}
					stockInfo.setIsShow(true);
					stockInfo.setIsLock(false);
					stockInfo.setCreator("系统-初始化");
					stockInfo.insert();
					StockDataChangeRecord stockDataChangeRecord = new StockDataChangeRecord();
					stockDataChangeRecord.setStockId(stockInfo.getId());
					stockDataChangeRecord.setStockCode(stockInfo.getStockCode());
					stockDataChangeRecord.setStockName(stockInfo.getStockName());
					stockDataChangeRecord.setDataChangeTypeCode(StockDataChangeTypeEnum.ADD_STOCK.getCode());
					stockDataChangeRecord.setDataChangeTypeName(StockDataChangeTypeEnum.ADD_STOCK.getName());
					stockDataChangeRecord.setNewContent(stockInfo.toString());
					stockDataChangeRecord.setIp("127.0.0.1");
					stockDataChangeRecord.setIpAddress("服务器本机ip");
					stockDataChangeRecord.setOperator("系统-初始化");
					stockDataChangeRecord.insert();
					processedCount++;
				}
			}
			return "总数："+stockInfoList.size()+",处理成功："+processedCount;
		}
		return "当前已有数据，无需初始化";
	}
}
