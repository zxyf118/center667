package mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserFinancingCertification;
import vo.manager.UserFinancingCertificationListSearchVO;

/**
 * <p>
 * 用户融资认证信息 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-12-15
 */
public interface UserFinancingCertificationMapper extends BaseMapper<UserFinancingCertification> {
	Page<UserFinancingCertification> managerUserFinancingCertificationList(Page<UserFinancingCertification> page, @Param("param")UserFinancingCertificationListSearchVO param);
}
