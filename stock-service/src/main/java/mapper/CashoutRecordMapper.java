package mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.CashoutRecord;
import vo.manager.CashoutRecordListParamVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashoutRecordVO;

/**
 * <p>
 * 提现记录表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-11-20
 */
public interface CashoutRecordMapper extends BaseMapper<CashoutRecord> {
	Page<CashoutRecord> managerList(Page<CashoutRecord> page, @Param("param") CashoutRecordListParamVO param);
	
	Page<CashoutRecordVO> record(Page<CashoutRecordVO> page, @Param("param") CashinAndOutRecordParamVO param);
}
