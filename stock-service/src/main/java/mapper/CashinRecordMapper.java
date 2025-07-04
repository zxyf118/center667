package mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.CashinRecord;
import vo.manager.CashinRecordListParamVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashinRecordVO;

/**
 * <p>
 * 会员充值记录表 Mapper 接口
 * </p>
 *
 * @author
 * @since 2024-10-31
 */
public interface CashinRecordMapper extends BaseMapper<CashinRecord> {
	Page<CashinRecord> managerList(Page<CashinRecord> page, @Param("param") CashinRecordListParamVO param);
	
	Page<CashinRecordVO> record(Page<CashinRecordVO> page, @Param("param") CashinAndOutRecordParamVO param);
}
