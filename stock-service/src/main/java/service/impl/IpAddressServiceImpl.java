package service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import config.RedisDbTypeEnum;
import entity.IpAddress;
import mapper.IpAddressMapper;
import redis.RedisKeyPrefix;
import service.IpAddressService;
import utils.RedisDao;
import utils.StringUtil;

/**
 * <p>
 * IP地址表 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-10-22
 */
@Service
public class IpAddressServiceImpl extends ServiceImpl<IpAddressMapper, IpAddress> implements IpAddressService {
	
	@Resource
	private RedisDao redisDao;
	
	@Override
	public IpAddress getIpAddress(String ip) {
		IpAddress ipAddress;
		if(ip.equals("0:0:0:0:0:0:0:1") || ip.equals("localhost") || ip.equals("127.0.0.1")) {
			ipAddress = new IpAddress();
        	ipAddress.setAddress2("本机ip地址");
        	return ipAddress;
		}
		if(ip.indexOf(":") > -1) {
        	ipAddress = new IpAddress();
        	ipAddress.setAddress2("未知");
        } else {
			String key = RedisKeyPrefix.getIpAddressKey(ip);
			ipAddress = redisDao.getBean(RedisDbTypeEnum.IP, key, IpAddress.class);
			if(ipAddress == null || ipAddress.getAddress2() == null) {
				long ipNumber = StringUtil.ip2Number(ip);
				QueryWrapper<IpAddress> qw = new QueryWrapper<>();
				qw.select("address2");
				qw.ge("ip2_num", ipNumber);
				qw.le("ip1_num", ipNumber);
				qw.last("limit 1");
				ipAddress = this.getOne(qw);
				if(ipAddress == null) {
					ipAddress = new IpAddress();
					ipAddress.setAddress2("未知");
				}
				redisDao.setBean(RedisDbTypeEnum.IP, key, ipAddress);
			}		
        }
		return ipAddress;
	}
}
