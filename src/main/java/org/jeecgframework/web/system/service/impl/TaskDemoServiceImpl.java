package org.jeecgframework.web.system.service.impl;

import java.util.Date;

import org.jeecgframework.web.system.service.TaskDemoServiceI;
import org.springframework.stereotype.Service;
@Service("taskDemoService")
public class TaskDemoServiceImpl implements TaskDemoServiceI {

	
	public void work() {
		org.jeecgframework.core.util.LogUtil.info(new Date().getTime());
		org.jeecgframework.core.util.LogUtil.info("----------任务测试-------");
	}

}