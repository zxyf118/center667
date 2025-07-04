package service;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.CashoutRecord;
import entity.common.Response;
import vo.manager.CashoutRecordListParamVO;
import vo.manager.CashoutRecordListVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashoutRecordVO;

/**
 * <p>
 * 提现记录表 服务类
 * </p>
 *
 * @author 
 * @since 2024-11-20
 */
public interface CashoutRecordService extends IService<CashoutRecord> {
	Response<CashoutRecordListVO> managerList(CashoutRecordListParamVO param);
	
	Response<Void> submit(Integer userId, BigDecimal amount, String fundPwd, String ip);
	
	void record(Page<CashoutRecordVO> page, CashinAndOutRecordParamVO param);
}
