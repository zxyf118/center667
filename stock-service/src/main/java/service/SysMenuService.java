package service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.service.IService;

import entity.SysMenu;
import entity.SysUser;
import entity.common.Response;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2024-10-28
 */
public interface SysMenuService extends IService<SysMenu> {
	
	List<SysMenu> selectAllMenuIdByUserId(Integer userId, String title, boolean setChildren);
	
	Set<String> selectAllMenuAjaxByUserId(Integer userId);
	
	Response<Void> add(SysMenu sysMenu, SysUser sysUser);
	
	Response<Void> edit(SysMenu sysMenu);
	
	Response<Void> delete(Integer menuId);
}
