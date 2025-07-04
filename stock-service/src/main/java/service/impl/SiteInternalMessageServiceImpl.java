package service.impl;

import entity.SiteInternalMessage;
import mapper.SiteInternalMessageMapper;
import service.SiteInternalMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 站内消息 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-11-25
 */
@Service
public class SiteInternalMessageServiceImpl extends ServiceImpl<SiteInternalMessageMapper, SiteInternalMessage> implements SiteInternalMessageService {

	@Override
	public void sendSiteInternalMessage(Integer userId, String title, String operator, String content) {
		SiteInternalMessage sim = new SiteInternalMessage();
		sim.setUserId(userId);
		sim.setTitle(title);
		sim.setCreator(operator);
		sim.setContent(content);
		this.save(sim);
	}

}
