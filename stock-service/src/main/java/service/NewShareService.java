package service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.NewShare;
import entity.common.Response;
import vo.manager.NewShareVO;
import vo.manager.ServerNewShareSearchParamVO;
import vo.server.ServerNewShareVO;

/**
 * <p>
 * 新股表 服务类
 * </p>
 *
 * @author 
 * @since 2024-11-21
 */
public interface NewShareService extends IService<NewShare> {
	/**
	 * 新增新股
	 * @param param
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> add(NewShareVO param, String ip, String operator);
	/**
	 * 修改新股
	 * @param param
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> edit(NewShareVO param, String ip, String operator);
	/**
	 * 删除新股
	 * @param id
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> delete(Integer id, String ip, String operator);
	
	void getNewSharePageByStockType(Page<ServerNewShareVO> page,  ServerNewShareSearchParamVO param);
}
