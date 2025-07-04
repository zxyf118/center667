package service;

import entity.SiteInternalMessage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 站内消息 服务类
 * </p>
 *
 * @author 
 * @since 2024-11-25
 */
public interface SiteInternalMessageService extends IService<SiteInternalMessage> {
	
	/**
	 * 发送站内信
	 * @param userId 用户id
	 * @param title 消息标题
	 * @param operator 创建者
	 * @param content 消息内容
	 */
	void sendSiteInternalMessage(Integer userId, String title, String operator, String content);

}
