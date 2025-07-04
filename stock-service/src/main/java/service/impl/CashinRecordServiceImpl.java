package service.impl;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.CashinRecord;
import entity.common.Response;
import mapper.CashinRecordMapper;
import service.CashinRecordService;
import vo.manager.CashinRecordListParamVO;
import vo.manager.CashinRecordListVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashinRecordVO;

/**
 * <p>
 * 会员充值记录表 服务实现类
 * </p>
 *
 * @author
 * @since 2024-10-31
 */
@Service
public class CashinRecordServiceImpl extends ServiceImpl<CashinRecordMapper, CashinRecord>
		implements CashinRecordService {
	@Resource
	private CashinRecordMapper cashinRecordMapper;

	@Override
	public Response<CashinRecordListVO> managerList(CashinRecordListParamVO param) {
		CashinRecordListVO vo = new CashinRecordListVO();
		Page<CashinRecord> page = new Page<>(param.getPageNo(), param.getPageSize());
		cashinRecordMapper.managerList(page, param);
		QueryWrapper<CashinRecord> qw = new QueryWrapper<>();
		qw.select("ifnull(sum(order_amount),0) as orderAmount, ifnull(sum(final_amount),0) as finalAmount");
		Map<String, Object> map = this.getMap(qw);
		vo.setTotalOrderAmount((BigDecimal) map.get("orderAmount"));
		vo.setTotalFinalAmount((BigDecimal) map.get("finalAmount"));
		vo.setPage(page);
		return Response.successData(vo);
	}

	@Override
	public void record(Page<CashinRecordVO> page, CashinAndOutRecordParamVO param) {
		cashinRecordMapper.record(page, param);
	}
}
