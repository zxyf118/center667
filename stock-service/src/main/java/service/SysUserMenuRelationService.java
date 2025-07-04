package service;

import com.baomidou.mybatisplus.extension.service.IService;

import entity.SysUser;
import entity.SysUserMenuRelation;
import entity.common.Response;
import vo.manager.SysUserMenuSaveParamVO;

/**
 * <p>
 * 管理系统用户菜单权限对应表 服务类
 * </p>
 *
 * @author 
 * @since 2024-10-28
 */
public interface SysUserMenuRelationService extends IService<SysUserMenuRelation> {
	Response<Void> saveSysUserMenuRelation(SysUserMenuSaveParamVO param, SysUser operator);
}
