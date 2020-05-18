package com.abocode.jfaster.admin.system.web;

import com.abocode.jfaster.admin.system.repository.RepairRepository;
import com.abocode.jfaster.admin.system.repository.SystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 第一次启动时执行
 * @Description   修复数据库
 */
@Scope("prototype")
@Controller
@RequestMapping("/repairController")
public class RepairController{
	@Autowired
	private SystemRepository systemService;
	@Autowired
	private RepairRepository repairService;
	@RequestMapping(params = "repair")
	public ModelAndView repair() {
		repairService.deleteAndRepair();
		systemService.initAllTypeGroups();   //初始化缓存
		return new ModelAndView("login/login");
	}
	
}
