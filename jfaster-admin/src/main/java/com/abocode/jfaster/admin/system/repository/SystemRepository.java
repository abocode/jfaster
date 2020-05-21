package com.abocode.jfaster.admin.system.repository;

import com.abocode.jfaster.core.repository.CommonRepository;
import com.abocode.jfaster.system.entity.*;

import java.util.List;
import java.util.Set;
 public  interface SystemRepository extends CommonRepository {

	/**
	 * 日志添加
	 * @param LogContent 内容
	 * @param loglevel 级别
	 * @param operationType 类型
	 */
	 void addLog(String LogContent, Short loglevel,Short operationType);
	/**
	 * 根据类型编码和类型名称获取Type,如果为空则创建一个
	 * @param typecode
	 * @param typename
	 * @return
	 */
	 Type getType(String typecode, String typename, TypeGroup tsTypegroup);
	/**
	 * 根据类型分组编码和名称获取TypeGroup,如果为空则创建一个
	 * @param typegroupcode
	 * @param typgroupename
	 * @return
	 */
	 TypeGroup getTypeGroup(String typegroupcode, String typgroupename);
	/**
	 * 根据用户ID 和 菜单Id 获取 具有操作权限的按钮Codes
	 * @param userId
	 * @param functionId
	 * @return
	 */
	  Set<String> getOperationCodesByUserIdAndFunctionId(String userId,String functionId);
	/**
	 * 根据角色ID 和 菜单Id 获取 具有操作权限的按钮Codes
	 * @param roleId
	 * @param functionId
	 * @return
	 */
	  Set<String> getOperationCodesByRoleIdAndFunctionId(String roleId,String functionId);
	/**
	 * 根据编码获取字典组
	 * 
	 * @param typegroupCode
	 * @return
	 */
	 TypeGroup getTypeGroupByCode(String typegroupCode);
	/**
	 * 对数据字典进行缓存
	 */
	 void initAllTypeGroups();
	
	/**
	 * 刷新字典缓存
	 * @param type
	 */
	 void refleshTypesCach(Type type);
	/**
	 * 刷新字典分组缓存
	 */
	 void refleshTypeGroupCach();
	/**
	 * 刷新菜单
	 * 
	 * @param id
	 */
	 void flushRoleFunciton(String id, Function newFunciton);

    /**
     * 生成组织机构编码
     * @param id 组织机构主键
     * @param pid 组织机构的父级主键
     * @return 组织机构编码
     */
	String generateOrgCode(String id, String pid);
	
	/**
	 * 
	  * getOperationCodesByRoleIdAndruleDataId
	  * 根据角色id 和 菜单Id 获取 具有操作权限的数据规则
	  *
	  * @Title: getOperationCodesByRoleIdAndruleDataId
	  * @Description: TODO
	  * @param @param roleId
	  * @param @param functionId
	  * @param @return    设定文件
	  * @return Set<String>    返回类型
	  * @throws
	 */
	
	  Set<String> getOperationCodesByRoleIdAndruleDataId(String roleId,String functionId);
	
	  Set<String> getOperationCodesByUserIdAndDataId(String userId,String functionId);
	
	/**
	 * 加载所有图标
	 * @return
	 */
	  void initAllTSIcons();
	
	/**
	 * 更新图标
	 * @param icon
	 */
	  void updateTSIcons(Icon icon);
	/**
	 * 删除图标
	 * @param icon
	 */
	  void delTSIcons(Icon icon);

	 void initOperations();

	/***
	 * 获取角色菜单
	 * @param roleId
	 * @return
     */
	List<Function> getFucntionList(String roleId);
}
