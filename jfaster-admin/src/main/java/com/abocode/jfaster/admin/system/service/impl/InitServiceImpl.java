package com.abocode.jfaster.admin.system.service.impl;

import com.abocode.jfaster.admin.system.repository.LanguageRepository;
import com.abocode.jfaster.admin.system.repository.SystemRepository;
import com.abocode.jfaster.admin.system.service.FunctionService;
import com.abocode.jfaster.admin.system.service.InitService;
import com.abocode.jfaster.core.common.util.ConfigUtils;
import com.abocode.jfaster.core.common.util.DateUtils;
import com.abocode.jfaster.core.common.util.StreamUtils;
import com.abocode.jfaster.system.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class InitServiceImpl implements InitService {
    public static final String ADMIN = "admin";
    public static final String ROLE_CODE = "roleCode";
    public static final String MANAGER = "manager";
    public static final String ICON_NAME = "iconName";
    public static final String TYPE_GROUP_NAME = "typeGroupName";
    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private SystemRepository systemRepository;
    @Autowired
    private FunctionService functionService;

    public void deleteAndRepair() {
        // 由于表中有主外键关系，清空数据库需注意
        systemRepository.executeHql("delete Log");
        systemRepository.executeHql("delete Operation");
        systemRepository.executeHql("delete RoleFunction");
        systemRepository.executeHql("delete RoleUser");
        systemRepository.executeHql("delete User");
        systemRepository.executeHql("update Function  set parentFunction = null");
        systemRepository.executeHql("delete Function");
        systemRepository.executeHql("update Org t set t.parentOrg = null");
        systemRepository.executeHql("delete Org");
        systemRepository.executeHql("delete Icon");
        systemRepository.executeHql("delete Role");
        systemRepository.executeHql("delete Type");
        systemRepository.executeHql("delete TypeGroup");
        systemRepository.executeHql("update Territory t set t.parentTerritory = null");
        systemRepository.executeHql("delete Territory");
        systemRepository.executeHql("delete Template");
        systemRepository.executeHql("delete Language");
        repair();
    }

    @Override
    public void contextInitialized() {
        /**
         * 第一部分：对数据字典进行缓存
         */
        systemRepository.initAllTypeGroups();
        //初始化图标
        systemRepository.initAllTSIcons();
        systemRepository.initOperations();
        /**
         * 第二部分：自动加载新增菜单和菜单操作权限
         * 说明：只会添加，不会删除（添加在代码层配置，但是在数据库层未配置的）
         */
        if ("true".equalsIgnoreCase(ConfigUtils.getConfigByName("auto.scan.menu.flag"))) {
            functionService.initMenu();
        }

        /**
         * 第三部分：加载多语言内容
         */
        languageRepository.initLanguage();
    }

    /**
     * @Description 修复数据库
     * @author tanghan 2013-7-19
     */

    public synchronized  void repair() {
        repaireIcon(); // 修复图标
        repairOrg();// 修复部门表
        repairRole();// 修复角色
        repairUser(); // 修复基本用户
        repairUserRole();// 修复用户和角色的关系
        repairTypeAndGroup();// 修复字典类型
        repairType();// 修复字典值
        repairLog();// 修复日志表
        repairMenu();// 修复菜单权限
        repairOperation(); // 修复操作表
        repairRoleFunction();// 修复角色和权限的关系
        repairTemplate();// 修复模版
        repairLanguage();// 修复多国语言
        repairTerritory();// 修复地域
    }

    private void repairTerritory() {
        try {
            ClassPathResource sqlFile = new ClassPathResource("sql/repair/RepairDao_batchRepairTerritory.sql");
            String str = StreamUtils.inputStreamToStr(sqlFile.getInputStream());
            systemRepository.updateBySql(str);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void repairLanguage() {
        try {
            ClassPathResource sqlFile = new ClassPathResource("sql/repair/RepairDao_batchRepairLanguage.sql");
            String str = StreamUtils.inputStreamToStr(sqlFile.getInputStream());
            systemRepository.updateBySql(str);
            languageRepository.refreshLanguageCache();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void repairTemplate() {
        try {
            ClassPathResource sqlFile = new ClassPathResource("sql/repair/RepairDao_batchRepairTemplate.sql");
            String str = StreamUtils.inputStreamToStr(sqlFile.getInputStream());
            systemRepository.updateBySql(str);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * @Description 修复日志表
     * @author tanghan 2013-7-28
     */
    private void repairLog() {
        User admin = systemRepository.findAllByProperty(User.class, "signatureFile", "images/renfang/qm/licf.gif").get(0);
        Log log1 = new Log();
        log1.setLogContent("用户: admin登录成功");
        log1.setBroswer("Chrome");
        log1.setNote("169.254.200.136");
        log1.setUser(admin);
        log1.setOperationTime(DateUtils.getTimestamp());
        log1.setOperationType((short) 1);
        log1.setLoglevel((short) 1);
        systemRepository.saveOrUpdate(log1);
    }

    /**
     * @Description 修复部门表
     * @author tanghan 2013-7-20
     */
    private void repairOrg() {
        Org eiu = new Org();
        eiu.setOrgName("系统管理");
        eiu.setDescription("12");
        systemRepository.saveOrUpdate(eiu);
    }

    /**
     * @Description 修复User表
     * @author tanghan 2013-7-23
     */
    private void repairUser() {
        Org eiu =systemRepository.findAllByProperty(Org.class, "orgName", "系统管理").get(0);

        User admin = new User();
        admin.setSignatureFile("images/renfang/qm/licf.gif");
        admin.setStatus((short) 1);
        admin.setRealName("管理员");
        admin.setUsername(ADMIN);
        admin.setPassword("c44b01947c9e6e3f");
        systemRepository.save(admin);

        UserOrg adminUserOrg = new UserOrg();
        adminUserOrg.setUser(admin);
        adminUserOrg.setOrg(eiu);
        systemRepository.save(adminUserOrg);

        User scott = new User();
        scott.setEmail("guanxf_m@126.com");
        scott.setStatus((short) 1);
        scott.setRealName("scott");
        scott.setUsername("scott");
        scott.setPassword("97c07a884bf272b5");
        systemRepository.saveOrUpdate(scott);
        UserOrg scottUserOrg = new UserOrg();
        scottUserOrg.setUser(scott);
        scottUserOrg.setOrg(eiu);
        systemRepository.save(scottUserOrg);

    }

    /**
     * @Description 修复用户角色表
     * @author tanghan 2013-7-23
     */
    private void repairUserRole() {
        Role admin = systemRepository.findAllByProperty(Role.class, ROLE_CODE, ADMIN).get(0);
        Role manager = systemRepository.findAllByProperty(Role.class, ROLE_CODE, MANAGER).get(0);
        List<User> user = systemRepository.findAll(User.class);
        for (int i = 0; i < user.size(); i++) {
            if (user.get(i).getEmail() != null) {
                RoleUser roleuser = new RoleUser();
                roleuser.setUser(user.get(i));
                roleuser.setRole(manager);
                systemRepository.saveOrUpdate(roleuser);
            } else {
                RoleUser roleuser = new RoleUser();
                roleuser.setUser(user.get(i));
                roleuser.setRole(admin);
                systemRepository.saveOrUpdate(roleuser);
            }
            if (user.get(i).getSignatureFile() != null) {
                RoleUser roleuser = new RoleUser();
                roleuser.setUser(user.get(i));
                roleuser.setRole(admin);
                systemRepository.saveOrUpdate(roleuser);
            }
        }

    }

    /**
     * @Description 修复角色权限表
     * @author tanghan 2013-7-23
     */
    private void repairRoleFunction() {
        Role admin = systemRepository.findAllByProperty(Role.class, ROLE_CODE, ADMIN).get(0);
        Role manager = systemRepository.findAllByProperty(Role.class, ROLE_CODE, MANAGER).get(0);
        List<Function> list = systemRepository.findAll(Function.class);
        for (int i = 0; i < list.size(); i++) {
            RoleFunction adminroleFunction = new RoleFunction();
            RoleFunction managerFunction = new RoleFunction();
            adminroleFunction.setFunction(list.get(i));
            managerFunction.setFunction(list.get(i));
            adminroleFunction.setRole(admin);
            managerFunction.setRole(manager);
            systemRepository.saveOrUpdate(adminroleFunction);
            systemRepository.saveOrUpdate(managerFunction);
        }
    }

    /**
     * @Description 修复操作按钮表
     * @author tanghan 2013-7-23
     */
    private void repairOperation() {
        Icon back = systemRepository.findAllByProperty(Icon.class, ICON_NAME, "返回").get(0);
        Function function = systemRepository.findAllByProperty(Function.class, "functionName", "系统管理").get(0);

        Operation add = new Operation();
        add.setOperationName("录入");
        add.setOperationCode("add");
        add.setIcon(back);
        add.setFunction(function);
        systemRepository.saveOrUpdate(add);

        Operation edit = new Operation();
        edit.setOperationName("编辑");
        edit.setOperationCode("edit");
        edit.setIcon(back);
        edit.setFunction(function);
        systemRepository.saveOrUpdate(edit);

        Operation del = new Operation();
        del.setOperationName("删除");
        del.setOperationCode("del");
        del.setIcon(back);
        del.setFunction(function);
        systemRepository.saveOrUpdate(del);

        Operation szqm = new Operation();
        szqm.setOperationName("审核");
        szqm.setOperationCode("szqm");
        szqm.setIcon(back);
        szqm.setFunction(function);
        systemRepository.saveOrUpdate(szqm);
    }

    /**
     * @Description 修复类型分组表
     * @author tanghan 2013-7-20
     */
    private void repairTypeAndGroup() {
        TypeGroup icontype = new TypeGroup();
        icontype.setTypeGroupName("图标类型");
        icontype.setTypeGroupCode("icontype");
        systemRepository.saveOrUpdate(icontype);

        TypeGroup ordertype = new TypeGroup();
        ordertype.setTypeGroupName("订单类型");
        ordertype.setTypeGroupCode("order");
        systemRepository.saveOrUpdate(ordertype);

        TypeGroup custom = new TypeGroup();
        custom.setTypeGroupName("客户类型");
        custom.setTypeGroupCode("custom");
        systemRepository.saveOrUpdate(custom);

        TypeGroup servicetype = new TypeGroup();
        servicetype.setTypeGroupName("服务项目类型");
        servicetype.setTypeGroupCode("service");
        systemRepository.saveOrUpdate(servicetype);

        TypeGroup searchMode = new TypeGroup();
        searchMode.setTypeGroupName("查询模式");
        searchMode.setTypeGroupCode("searchmode");
        systemRepository.saveOrUpdate(searchMode);

        TypeGroup yesOrno = new TypeGroup();
        yesOrno.setTypeGroupName("逻辑条件");
        yesOrno.setTypeGroupCode("yesorno");
        systemRepository.saveOrUpdate(yesOrno);

        TypeGroup fieldtype = new TypeGroup();
        fieldtype.setTypeGroupName("字段类型");
        fieldtype.setTypeGroupCode("fieldtype");
        systemRepository.saveOrUpdate(fieldtype);

        TypeGroup datatable = new TypeGroup();
        datatable.setTypeGroupName("数据表");
        datatable.setTypeGroupCode("database");
        systemRepository.saveOrUpdate(datatable);

        TypeGroup filetype = new TypeGroup();
        filetype.setTypeGroupName("文档分类");
        filetype.setTypeGroupCode("fieltype");
        systemRepository.saveOrUpdate(filetype);

        TypeGroup sex = new TypeGroup();
        sex.setTypeGroupName("性别类");
        sex.setTypeGroupCode("sex");
        systemRepository.saveOrUpdate(sex);
    }

    /**
     * @Description 修复类型表
     * @author tanghan 2013-7-22
     */
    private void repairType() {
        TypeGroup icontype = systemRepository.findAllByProperty(TypeGroup.class, TYPE_GROUP_NAME, "图标类型").get(0);
        TypeGroup ordertype = systemRepository.findAllByProperty(TypeGroup.class, TYPE_GROUP_NAME, "订单类型").get(0);
        TypeGroup custom = systemRepository.findAllByProperty(TypeGroup.class, TYPE_GROUP_NAME, "客户类型").get(0);
        TypeGroup servicetype = systemRepository.findAllByProperty(TypeGroup.class, TYPE_GROUP_NAME, "服务项目类型").get(0);
        TypeGroup datatable = systemRepository.findAllByProperty(TypeGroup.class, TYPE_GROUP_NAME, "数据表").get(0);
        TypeGroup filetype = systemRepository.findAllByProperty(TypeGroup.class, TYPE_GROUP_NAME, "文档分类").get(0);
        TypeGroup sex = systemRepository.findAllByProperty(TypeGroup.class, TYPE_GROUP_NAME, "性别类").get(0);
        TypeGroup searchmode = systemRepository.findAllByProperty(TypeGroup.class, TYPE_GROUP_NAME, "查询模式").get(0);
        TypeGroup yesorno = systemRepository.findAllByProperty(TypeGroup.class, TYPE_GROUP_NAME, "逻辑条件").get(0);
        TypeGroup fieldtype = systemRepository.findAllByProperty(TypeGroup.class, TYPE_GROUP_NAME, "字段类型").get(0);

        Type menu = new Type();
        menu.setTypeName("菜单图标");
        menu.setTypeCode("2");
        menu.setTypeGroup(icontype);
        systemRepository.saveOrUpdate(menu);

        Type systemicon = new Type();
        systemicon.setTypeName("系统图标");
        systemicon.setTypeCode("1");
        systemicon.setTypeGroup(icontype);
        systemRepository.saveOrUpdate(systemicon);

        Type file = new Type();
        file.setTypeName("附件");
        file.setTypeCode("files");
        file.setTypeGroup(filetype);
        systemRepository.saveOrUpdate(file);

        Type goodorder = new Type();
        goodorder.setTypeName("优质订单");
        goodorder.setTypeCode("1");
        goodorder.setTypeGroup(ordertype);
        systemRepository.saveOrUpdate(goodorder);

        Type general = new Type();
        general.setTypeName("普通订单");
        general.setTypeCode("2");
        general.setTypeGroup(ordertype);
        systemRepository.saveOrUpdate(general);

        Type sign = new Type();
        sign.setTypeName("签约客户");
        sign.setTypeCode("1");
        sign.setTypeGroup(custom);
        systemRepository.saveOrUpdate(sign);

        Type commoncustom = new Type();
        commoncustom.setTypeName("普通客户");
        commoncustom.setTypeCode("2");
        commoncustom.setTypeGroup(custom);
        systemRepository.saveOrUpdate(commoncustom);

        Type vipservice = new Type();
        vipservice.setTypeName("特殊服务");
        vipservice.setTypeCode("1");
        vipservice.setTypeGroup(servicetype);
        systemRepository.saveOrUpdate(vipservice);

        Type commonservice = new Type();
        commonservice.setTypeName("普通服务");
        commonservice.setTypeCode("2");
        commonservice.setTypeGroup(servicetype);
        systemRepository.saveOrUpdate(commonservice);

        Type single = new Type();
        single.setTypeName("单条件查询");
        single.setTypeCode("single");
        single.setTypeGroup(searchmode);
        systemRepository.saveOrUpdate(single);

        Type group = new Type();
        group.setTypeName("范围查询");
        group.setTypeCode("group");
        group.setTypeGroup(searchmode);
        systemRepository.saveOrUpdate(group);

        Type yes = new Type();
        yes.setTypeName("是");
        yes.setTypeCode("Y");
        yes.setTypeGroup(yesorno);
        systemRepository.saveOrUpdate(yes);

        Type no = new Type();
        no.setTypeName("否");
        no.setTypeCode("N");
        no.setTypeGroup(yesorno);
        systemRepository.saveOrUpdate(no);

        Type type = new Type();
        type.setTypeName("Integer");
        type.setTypeCode("Integer");
        type.setTypeGroup(fieldtype);
        systemRepository.saveOrUpdate(type);

        Type typeDate = new Type();
        typeDate.setTypeName("Date");
        typeDate.setTypeCode("Date");
        typeDate.setTypeGroup(fieldtype);
        systemRepository.saveOrUpdate(typeDate);

        Type typeString = new Type();
        typeString.setTypeName("String");
        typeString.setTypeCode("String");
        typeString.setTypeGroup(fieldtype);
        systemRepository.saveOrUpdate(typeString);

        Type typeLong = new Type();
        typeLong.setTypeName("Long");
        typeLong.setTypeCode("Long");
        typeLong.setTypeGroup(fieldtype);
        systemRepository.saveOrUpdate(typeLong);

        Type sys = new Type();
        sys.setTypeName("系统基础表");
        sys.setTypeCode("t_s");
        sys.setTypeGroup(datatable);
        systemRepository.saveOrUpdate(sys);

        Type business = new Type();
        business.setTypeName("业务表");
        business.setTypeCode("t_b");
        business.setTypeGroup(datatable);
        systemRepository.saveOrUpdate(business);

        Type news = new Type();
        news.setTypeName("新闻");
        news.setTypeCode("news");
        news.setTypeGroup(filetype);
        systemRepository.saveOrUpdate(news);

        Type man = new Type();
        man.setTypeName("男性");
        man.setTypeCode("0");
        man.setTypeGroup(sex);
        systemRepository.saveOrUpdate(man);

        Type woman = new Type();
        woman.setTypeName("女性");
        woman.setTypeCode("1");
        woman.setTypeGroup(sex);
        systemRepository.saveOrUpdate(woman);
    }

    /**
     * @Description 修复角色表
     * @author tanghan 2013-7-20
     */
    private void repairRole() {
        Role admin = new Role();
        admin.setRoleName("管理员");
        admin.setRoleCode(ADMIN);
        systemRepository.saveOrUpdate(admin);

        Role manager = new Role();
        manager.setRoleName("普通用户");
        manager.setRoleCode(MANAGER);
        systemRepository.saveOrUpdate(manager);

    }

    /**
     * @Description 修复图标表
     * @author tanghan 2013-7-19
     */
    private void repaireIcon() {
        log.info("修复图标中");

        Icon defaultIcon = new Icon();
        defaultIcon.setIconName("默认图");
        defaultIcon.setIconType((short) 1);
        defaultIcon.setIconPath("plug-in/accordion/images/default.png");
        defaultIcon.setIconClazz("default");
        defaultIcon.setIconExtend("png");
        systemRepository.saveOrUpdate(defaultIcon);

        Icon back = new Icon();
        back.setIconName("返回");
        back.setIconType((short) 1);
        back.setIconPath("plug-in/accordion/images/back.png");
        back.setIconClazz("back");
        back.setIconExtend("png");
        systemRepository.saveOrUpdate(back);

        Icon pie = new Icon();

        pie.setIconName("饼图");
        pie.setIconType((short) 1);
        pie.setIconPath("plug-in/accordion/images/pie.png");
        pie.setIconClazz("pie");
        pie.setIconExtend("png");
        systemRepository.saveOrUpdate(pie);

        Icon pictures = new Icon();
        pictures.setIconName("图片");
        pictures.setIconType((short) 1);
        pictures.setIconPath("plug-in/accordion/images/pictures.png");
        pictures.setIconClazz("pictures");
        pictures.setIconExtend("png");
        systemRepository.saveOrUpdate(pictures);

        Icon pencil = new Icon();
        pencil.setIconName("笔");
        pencil.setIconType((short) 1);
        pencil.setIconPath("plug-in/accordion/images/pencil.png");
        pencil.setIconClazz("pencil");
        pencil.setIconExtend("png");
        systemRepository.saveOrUpdate(pencil);

        Icon map = new Icon();
        map.setIconName("地图");
        map.setIconType((short) 1);
        map.setIconPath("plug-in/accordion/images/map.png");
        map.setIconClazz("map");
        map.setIconExtend("png");
        systemRepository.saveOrUpdate(map);

        Icon icon = new Icon();
        icon.setIconName("组");
        icon.setIconType((short) 1);
        icon.setIconPath("plug-in/accordion/images/group_add.png");
        icon.setIconClazz("group_add");
        icon.setIconExtend("png");
        systemRepository.saveOrUpdate(icon);

        Icon calculator = new Icon();
        calculator.setIconName("计算器");
        calculator.setIconType((short) 1);
        calculator.setIconPath("plug-in/accordion/images/calculator.png");
        calculator.setIconClazz("calculator");
        calculator.setIconExtend("png");
        systemRepository.saveOrUpdate(calculator);

        Icon folder = new Icon();
        folder.setIconName("文件夹");
        folder.setIconType((short) 1);
        folder.setIconPath("plug-in/accordion/images/folder.png");
        folder.setIconClazz("folder");
        folder.setIconExtend("png");
        systemRepository.saveOrUpdate(folder);
    }

    /**
     * 修复桌面默认图标
     *
     * @param iconName      图标名称
     * @param iconLabelName 图标展示名称
     * @return 图标实体
     */
    private Icon repairInconForDesk(String iconName, String iconLabelName) {
        String iconPath = "plug-in/sliding/icon/" + iconName + ".png";
        Icon deskIncon = new Icon();
        deskIncon.setIconName(iconLabelName);
        deskIncon.setIconType((short) 3);
        deskIncon.setIconPath(iconPath);
        deskIncon.setIconClazz("deskIcon");
        deskIncon.setIconExtend("png");
        systemRepository.saveOrUpdate(deskIncon);

        return deskIncon;
    }

    /**
     * 修复桌面默认图标
     *
     * @return 图标实体
     */
    private Icon getDefaultInconForDesk() {
        String iconPath = "plug-in/sliding/icon/default.png";
        Icon deskIncon = new Icon();
        deskIncon.setIconName("默认图标");
        deskIncon.setIconType((short) 3);
        deskIncon.setIconPath(iconPath);
        deskIncon.setIconClazz("deskIcon");
        deskIncon.setIconExtend("png");
        systemRepository.saveOrUpdate(deskIncon);

        return deskIncon;
    }

    /**
     * @Description 修复菜单权限
     * @author tanghan 2013-7-19
     */
    private void repairMenu() {
        Icon defaultIcon = systemRepository.findAllByProperty(Icon.class, ICON_NAME, "默认图").get(0);
        Icon groupAdd = systemRepository.findAllByProperty(Icon.class, ICON_NAME, "组").get(0);
        Icon pie = systemRepository.findAllByProperty(Icon.class, ICON_NAME, "饼图").get(0);
        Icon folder = systemRepository.findAllByProperty(Icon.class, ICON_NAME, "文件夹").get(0);

        Function sys = new Function();
        sys.setFunctionName("系统管理");
        sys.setFunctionUrl("");
        sys.setFunctionLevel((short) 0);
        sys.setFunctionOrder("5");
        sys.setIconDesk(getDefaultInconForDesk());
        sys.setIcon(groupAdd);
        systemRepository.saveOrUpdate(sys);

        Function state = new Function();
        state.setFunctionName("统计查询");
        state.setFunctionUrl("");
        state.setFunctionLevel((short) 0);
        state.setFunctionOrder("3");
        state.setIcon(folder);
        state.setIconDesk(getDefaultInconForDesk());
        systemRepository.saveOrUpdate(state);

        Function syscontrol = new Function();
        syscontrol.setFunctionName("系统监控");
        syscontrol.setFunctionUrl("");
        syscontrol.setFunctionLevel((short) 0);
        syscontrol.setFunctionOrder("11");
        syscontrol.setIcon(defaultIcon);
        syscontrol.setIconDesk(getDefaultInconForDesk());
        systemRepository.saveOrUpdate(syscontrol);

        Function user = new Function();
        user.setFunctionName("用户管理");
        user.setFunctionUrl("userController.do?user");
        user.setFunctionLevel((short) 1);
        user.setFunctionOrder("5");
        user.setParentFunction(sys);
        user.setIcon(defaultIcon);
        user.setIconDesk(repairInconForDesk("Finder", "用户管理"));
        systemRepository.saveOrUpdate(user);

        Function role = new Function();
        role.setFunctionName("角色管理");
        role.setFunctionUrl("roleController.do?role");
        role.setFunctionLevel((short) 1);
        role.setFunctionOrder("6");
        role.setParentFunction(sys);
        role.setIcon(defaultIcon);
        role.setIconDesk(repairInconForDesk("friendgroup", "角色管理"));
        systemRepository.saveOrUpdate(role);

        Function menu = new Function();
        menu.setFunctionName("菜单管理");
        menu.setFunctionUrl("functionController.do?function");
        menu.setFunctionLevel((short) 1);
        menu.setFunctionOrder("7");
        menu.setParentFunction(sys);
        menu.setIcon(defaultIcon);
        menu.setIconDesk(repairInconForDesk("kaikai", "菜单管理"));
        systemRepository.saveOrUpdate(menu);

        Function typegroup = new Function();
        typegroup.setFunctionName("数据字典");
        typegroup.setFunctionUrl("typeController.do?typeGroupList");
        typegroup.setFunctionLevel((short) 1);
        typegroup.setFunctionOrder("6");
        typegroup.setParentFunction(sys);
        typegroup.setIcon(defaultIcon);
        typegroup.setIconDesk(repairInconForDesk("friendnear", "数据字典"));
        systemRepository.saveOrUpdate(typegroup);

        Function icon = new Function();
        icon.setFunctionName("图标管理");
        icon.setFunctionUrl("iconController.do?icon");
        icon.setFunctionLevel((short) 1);
        icon.setFunctionOrder("18");
        icon.setParentFunction(sys);
        icon.setIcon(defaultIcon);
        icon.setIconDesk(repairInconForDesk("kxjy", "图标管理"));
        systemRepository.saveOrUpdate(icon);

        Function depart = new Function();
        depart.setFunctionName("部门管理");
        depart.setFunctionUrl("departController.do?depart");
        depart.setFunctionLevel((short) 1);
        depart.setFunctionOrder("22");
        depart.setParentFunction(sys);
        depart.setIcon(defaultIcon);
        depart.setIconDesk(getDefaultInconForDesk());
        systemRepository.saveOrUpdate(depart);

        Function territory = new Function();
        territory.setFunctionName("地域管理");
        territory.setFunctionUrl("territoryController.do?territory");
        territory.setFunctionLevel((short) 1);
        territory.setFunctionOrder("22");
        territory.setParentFunction(sys);
        territory.setIcon(pie);
        territory.setIconDesk(getDefaultInconForDesk());
        systemRepository.saveOrUpdate(territory);

        Function language = new Function();
        language.setFunctionName("语言管理");
        language.setFunctionUrl("languageController.do?language");
        language.setFunctionLevel((short) 1);
        language.setFunctionOrder("30");
        language.setParentFunction(sys);
        language.setIcon(pie);
        language.setIconDesk(getDefaultInconForDesk());
        systemRepository.saveOrUpdate(language);

        Function template = new Function();
        template.setFunctionName("模版管理");
        template.setFunctionUrl("templateController.do?template");
        template.setFunctionLevel((short) 1);
        template.setFunctionOrder("28");
        template.setParentFunction(sys);
        template.setIcon(pie);
        template.setIconDesk(getDefaultInconForDesk());
        systemRepository.saveOrUpdate(template);

        Function useranalyse = new Function();
        useranalyse.setFunctionName("用户分析");
        useranalyse.setFunctionUrl("logController.do?statisticTabs&isIframe");
        useranalyse.setFunctionLevel((short) 1);
        useranalyse.setFunctionOrder("17");
        useranalyse.setParentFunction(state);
        useranalyse.setIcon(pie);
        useranalyse.setIconDesk(repairInconForDesk("user", "用户分析"));
        systemRepository.saveOrUpdate(useranalyse);

        Function druid = new Function();
        druid.setFunctionName("数据监控");
        druid.setFunctionUrl("dataSourceController.do?druid&isIframe");
        druid.setFunctionLevel((short) 1);
        druid.setFunctionOrder("11");
        druid.setParentFunction(syscontrol);
        druid.setIcon(defaultIcon);
        druid.setIconDesk(repairInconForDesk("Super Disk", "数据监控"));
        systemRepository.saveOrUpdate(druid);

        Function log = new Function();
        log.setFunctionName("系统日志");
        log.setFunctionUrl("logController.do?log");
        log.setFunctionLevel((short) 1);
        log.setFunctionOrder("21");
        log.setParentFunction(syscontrol);
        log.setIcon(defaultIcon);
        log.setIconDesk(repairInconForDesk("fasearch", "系统日志"));
        systemRepository.saveOrUpdate(log);

        Function timeTask = new Function();
        timeTask.setFunctionName("定时任务");
        timeTask.setFunctionUrl("jobController.do?job");
        timeTask.setFunctionLevel((short) 1);
        timeTask.setFunctionOrder("21");
        timeTask.setParentFunction(syscontrol);
        timeTask.setIcon(defaultIcon);
        timeTask.setIconDesk(repairInconForDesk("Utilities", "定时任务"));
        systemRepository.saveOrUpdate(timeTask);

        Function reportdemo = new Function();
        reportdemo.setFunctionName("报表示例");
        reportdemo.setFunctionUrl("reportDemoController.do?studentatisticTabs&isIframe");
        reportdemo.setFunctionLevel((short) 1);
        reportdemo.setFunctionOrder("21");
        reportdemo.setParentFunction(state);
        reportdemo.setIcon(pie);
        reportdemo.setIconDesk(getDefaultInconForDesk());
        systemRepository.saveOrUpdate(reportdemo);

    }
}
