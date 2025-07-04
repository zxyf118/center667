package service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import config.RedisDbTypeEnum;
import entity.Country;
import mapper.CountryMapper;
import redis.RedisKeyPrefix;
import service.CountryService;
import utils.RedisDao;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2024-10-22
 */
@Service
public class CountryServiceImpl extends ServiceImpl<CountryMapper, Country> implements CountryService {
	
	@Resource
	private RedisDao redisDao;

	@Override
	public List<Country> countryList() {
		String key = RedisKeyPrefix.getCountryListKey();
		List<Country> list = redisDao.getBeanList(RedisDbTypeEnum.DEFAULT, key, Country.class);
		if(list == null || list.size() == 0) {
			list = this.lambdaQuery().orderByAsc(Country::getSort).list();
			redisDao.setBean(RedisDbTypeEnum.DEFAULT, key, list, 7, TimeUnit.DAYS);
		}
		return list;
	}

	@Override
	public Country getCountryByAreaCode(String areaCode) {
		String key = RedisKeyPrefix.getCountryListKey();
		List<Country> list = redisDao.getBeanList(RedisDbTypeEnum.DEFAULT, key, Country.class);
		Country c = null;
		if(list == null || list.size() == 0) {
			c = this.lambdaQuery().eq(Country::getAreaCode, areaCode).one();
		} else {
			for(Country i : list) {
				if(i.getAreaCode().equals(areaCode)) {
					c = i;
				}
			}
		}
		return c;
	}
}
