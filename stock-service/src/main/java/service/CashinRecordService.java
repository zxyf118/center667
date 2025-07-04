package service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.CashinRecord;
import entity.common.Response;
import vo.manager.CashinRecordListParamVO;
import vo.manager.CashinRecordListVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashinRecordVO;

/**
 * <p>
 * 会员充值记录表 服务类
 * </p>
 *
 * @author 
 * @since 2024-10-31
 */
public interface CashinRecordService extends IService<CashinRecord> {
	Response<CashinRecordListVO> managerList(CashinRecordListParamVO param);
	
	void record(Page<CashinRecordVO> page, CashinAndOutRecordParamVO param);
}
