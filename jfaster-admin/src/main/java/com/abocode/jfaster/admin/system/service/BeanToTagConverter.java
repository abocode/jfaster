package com.abocode.jfaster.admin.system.service;

import com.abocode.jfaster.admin.system.dto.FunctionDto;
import com.abocode.jfaster.core.common.util.StrUtils;
import com.abocode.jfaster.core.platform.view.FunctionView;
import com.abocode.jfaster.core.platform.view.IconView;
import com.abocode.jfaster.core.platform.view.TypeGroupView;
import com.abocode.jfaster.core.platform.view.TypeView;
import com.abocode.jfaster.system.entity.Function;
import com.abocode.jfaster.system.entity.Icon;
import com.abocode.jfaster.system.entity.Type;
import com.abocode.jfaster.system.entity.TypeGroup;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Franky on 2016/3/15.
 */
public class BeanToTagConverter {
	private BeanToTagConverter() {
	}

	/**
	 * 转换菜单
	 * @param function
	 */
	public static FunctionView convertFunction(Function function) {
		FunctionView functionBean=null;
		if(!StrUtils.isEmpty(function)){
			functionBean=new FunctionView();
			FunctionDto functionVo=new FunctionDto();
			BeanUtils.copyProperties(function, functionVo);
			BeanUtils.copyProperties(functionVo, functionBean);

			//设置
			Function parentFunction=function.getParentFunction();
			if(parentFunction!=null){
				FunctionDto parentFunctionVo=new FunctionDto();
				FunctionView parentFunctionBean=new FunctionView();
				BeanUtils.copyProperties(parentFunction, parentFunctionVo);
				BeanUtils.copyProperties(parentFunctionVo, parentFunctionBean);
				functionBean.setParentFunction(parentFunctionBean);
			}

			List<Function> functionLists=function.getFunctions();
			functionBean.setFunctions(BeanToTagConverter.convertFunctions(functionLists));
			if(!StrUtils.isEmpty(function.getIcon())){
				IconView iconBean=new IconView();
				BeanUtils.copyProperties(function.getIcon(), iconBean);
				functionBean.setIcon(iconBean);
			}

			Icon conDeskBean=function.getIconDesk();
			if(!StrUtils.isEmpty(conDeskBean)){
				IconView iconView=new IconView();
				BeanUtils.copyProperties(conDeskBean, iconView);
				functionBean.setIconDesk(iconView);
			}
		}
		  return functionBean;
		}
	
	/**
	 * 转换所有菜单
	 * @param functionList
	 */
	public static List<FunctionView> convertFunctions(List<Function> functionList) {
		List<FunctionView> functionBeanList=new ArrayList<>();
		for(Function function:functionList){
			FunctionView functionBean=convertFunction(function);
			functionBeanList.add(functionBean);
		}
		return functionBeanList;
	}
   /**
    * 转换typeGroup
    * @param tsTypegroup
    * @return
    */
	public static TypeGroupView convertTypeGroup(TypeGroup tsTypegroup) {
		
		TypeGroupView typeGroupBean=null;
		if(null!=tsTypegroup){
			typeGroupBean=new TypeGroupView();
			BeanUtils.copyProperties(tsTypegroup, typeGroupBean);
		}	
		return typeGroupBean;
	}
 /**
  * 转换types
  * @param tsTypes
  * @return
  */
public static List<TypeView> convertTypes(List<Type> tsTypes) {
	List<TypeView> types=new ArrayList<>();
	for(Type tsType:tsTypes){
		TypeView typeBean= BeanToTagConverter.convertType(tsType);
		types.add(typeBean);
	}
	return types;
}
/**
 * 转换type
 * @param tsType
 * @return
 */
public static TypeView convertType(Type tsType) {
	TypeView type=null;
	if(tsType!=null){
		 type=new TypeView();
		type.setId(tsType.getId());
		type.setType(BeanToTagConverter.convertType(tsType.getPType()));
		type.setTypeGroup(BeanToTagConverter.convertTypeGroup(tsType.getTypeGroup()));
		type.setTypes(BeanToTagConverter.convertTypes(tsType.getTypes()));
		type.setTypeCode(tsType.getTypeCode());
		type.setTypeName(tsType.getTypeName());
	}
	return type;
}
/***
 * 转换图标
 * @param tsIcon
 * @return
 */
public static IconView convertIcon(Icon tsIcon) {
	if(tsIcon!=null){
		IconView icon=new IconView();
		BeanUtils.copyProperties(tsIcon, icon);
		return icon;
	}
	return null;
}
}
