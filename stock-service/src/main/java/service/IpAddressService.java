package service;

import entity.IpAddress;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * IP地址表 服务类
 * </p>
 *
 * @author 
 * @since 2024-10-22
 */
public interface IpAddressService extends IService<IpAddress> {
	IpAddress getIpAddress(String ip);
}
