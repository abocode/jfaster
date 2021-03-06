package com.abocode.jfaster.admin.system.web;

import com.abocode.jfaster.admin.system.dto.FileUploadDto;
import com.abocode.jfaster.admin.system.repository.ResourceRepository;
import com.abocode.jfaster.admin.system.repository.SystemRepository;
import com.abocode.jfaster.admin.system.service.IconService;
import com.abocode.jfaster.api.system.IconDTO;
import com.abocode.jfaster.core.common.exception.BusinessException;
import com.abocode.jfaster.core.common.model.json.AjaxJson;
import com.abocode.jfaster.core.common.model.json.AjaxJsonBuilder;
import com.abocode.jfaster.core.common.util.ContextHolderUtils;
import com.abocode.jfaster.core.common.util.ConvertUtils;
import com.abocode.jfaster.core.common.util.FileUtils;
import com.abocode.jfaster.core.common.util.StrUtils;
import com.abocode.jfaster.core.persistence.hibernate.qbc.CriteriaQuery;
import com.abocode.jfaster.core.platform.utils.LanguageUtils;
import com.abocode.jfaster.core.platform.utils.SysThemesUtils;
import com.abocode.jfaster.core.repository.DataGridData;
import com.abocode.jfaster.core.repository.DataGridParam;
import com.abocode.jfaster.system.entity.Icon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;


/**
 * 图标信息处理类
 *
 * @author 张代浩
 */
@Scope("prototype")
@Controller
@RequestMapping("/iconController")
public class IconController {
    public static final String PLUG_IN_ACCORDION_CSS_ICONS_CSS = "/plug-in/accordion/css/icons.css";
    @Autowired
    private SystemRepository systemService;
    @Autowired
    private ResourceRepository resourceService;
    @Autowired
    private IconService iconService;

    /**
     * 图标列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "icon")
    public ModelAndView icon() {
        return new ModelAndView("system/icon/iconList");
    }

    /**
     * easyuiAJAX请求数据
     *
     * @param request
     * @param dataGridParam
     * @return
     */
    @RequestMapping(params = "findDataGridData")
    @ResponseBody
    public DataGridData findDataGridData(IconDTO icon, HttpServletRequest request, DataGridParam dataGridParam) {
        CriteriaQuery cq = new CriteriaQuery(Icon.class).buildParameters(icon, dataGridParam);
        DataGridData data = this.systemService.findDataGridData(cq, true);
        FileUtils.convertDataGrid(data, request);//先把数据库的byte存成图片到临时目录，再给每个TsIcon设置目录路径
        return data;
    }

    /**
     * 上传图标
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(params = "saveOrUpdateIcon")
    @ResponseBody
    public AjaxJson saveOrUpdateIcon(HttpServletRequest request) {
        Icon icon = new Icon();
        Short iconType = ConvertUtils.getShort(request.getParameter("iconType"));
        String iconName = ConvertUtils.getString(request.getParameter("iconName"));
        String id = request.getParameter("id");
        icon.setId(id);
        icon.setIconName(iconName);
        icon.setIconType(iconType);
        FileUploadDto uploadFile = new FileUploadDto(request, icon);
        uploadFile.setFolderPath("plug-in/accordion/images");
        uploadFile.setExtend("extend");
        uploadFile.setTitleField("iconclas");
        uploadFile.setRealPath("iconPath");
        uploadFile.setObject(icon);
        uploadFile.setByteField("iconContent");
        uploadFile.setRename(false);
        resourceService.uploadFile(uploadFile);
        // 图标的css样式
        String cssBody = build(icon);
        String path = request.getSession().getServletContext().getRealPath(PLUG_IN_ACCORDION_CSS_ICONS_CSS);
        SysThemesUtils.write(path, cssBody);
        String message = LanguageUtils.paramAddSuccess("common.icon");
        return AjaxJsonBuilder.success(message);
    }


    /**
     * 没有上传文件时更新信息
     *
     * @return
     * @throws Exception
     */
    @PostMapping(params = "update")
    @ResponseBody
    public AjaxJson update(@RequestParam short iconType, String iconName, String id) {
        Icon icon = new Icon();
        if (StrUtils.isNotEmpty(id)) {
            icon = systemService.find(Icon.class, id);
            icon.setId(id);
        }
        icon.setIconName(iconName);
        icon.setIconType(iconType);
        systemService.saveOrUpdate(icon);
        // 图标的css样式
		String cssBody = build(icon);
		String path = ContextHolderUtils.getRequest().getServletContext().getRealPath(PLUG_IN_ACCORDION_CSS_ICONS_CSS);
		SysThemesUtils.write(path, cssBody);
        return AjaxJsonBuilder.success();
    }


    /**
     * 恢复图标（将数据库图标数据写入图标存放的路径下）
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(params = "repair")
    @ResponseBody
    public AjaxJson repair(HttpServletRequest request)  {
        List<Icon> icons = systemService.findAll(Icon.class);
        String rootPath = request.getSession().getServletContext().getRealPath("/");
		String path = ContextHolderUtils.getRequest().getServletContext().getRealPath(PLUG_IN_ACCORDION_CSS_ICONS_CSS);
        // 清空CSS文件内容
        SysThemesUtils.clearFile(path);
        for (Icon icon : icons) {
            File file = new File(rootPath.concat(icon.getIconPath()));
            if (!file.exists()) {
                byte[] content = icon.getIconContent();
                if (content != null) {
                	try {
						BufferedImage imag = ImageIO.read(new ByteArrayInputStream(content));
						ImageIO.write(imag, "PNG", file);// 输出到 png 文件
					}catch (Exception e){
                		throw  new BusinessException("写文件失败",e);
					}

                }
            }
			String cssBody = build(icon);
			SysThemesUtils.write(path, cssBody);
        }
        return AjaxJsonBuilder.success(LanguageUtils.paramAddSuccess("common.icon.style"));
    }


    /**
     * 删除图标
     *
     * @param id
     * @return
     */
    @RequestMapping(params = "del")
    @ResponseBody
    public AjaxJson del(String id) {
        Icon icon = systemService.find(Icon.class, id);
        boolean isPermit = iconService.isPermitDel(icon);
        Assert.isTrue(isPermit, LanguageUtils.paramDelFail("common.icon,common.icon.isusing"));
        iconService.save(icon);
        return AjaxJsonBuilder.success();
    }

    /**
     * 图标页面跳转
     *
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(params = "detail")
    public ModelAndView detail(String id, HttpServletRequest request) {
        if (StrUtils.isNotEmpty(id)) {
            Icon icon = systemService.find(Icon.class, id);
            request.setAttribute("icon", icon);
        }
        return new ModelAndView("system/icon/icons");
    }


	private String build(Icon icon) {
		return "." + icon.getIconClazz() + "{background:url('../images/" + icon.getIconClazz() + "." + icon.getIconExtend() + "') no-repeat}";
	}
}
