package service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import entity.Country;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2024-10-22
 */
public interface CountryService extends IService<Country> {
	List<Country> countryList();
	Country getCountryByAreaCode(String areaCode);
}
