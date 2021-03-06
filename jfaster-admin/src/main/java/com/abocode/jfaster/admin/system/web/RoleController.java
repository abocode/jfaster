package com.abocode.jfaster.admin.system.web;

import com.abocode.jfaster.admin.system.repository.ResourceRepository;
import com.abocode.jfaster.admin.system.repository.SystemRepository;
import com.abocode.jfaster.admin.system.repository.UserRepository;
import com.abocode.jfaster.admin.system.service.RoleService;
import com.abocode.jfaster.core.common.model.json.*;
import com.abocode.jfaster.core.common.util.ConvertUtils;
import com.abocode.jfaster.core.common.util.IdUtils;
import com.abocode.jfaster.core.persistence.hibernate.hql.HqlGenerateUtil;
import com.abocode.jfaster.core.persistence.hibernate.qbc.CriteriaQuery;
import com.abocode.jfaster.core.repository.DataGridData;
import com.abocode.jfaster.core.repository.DataGridParam;
import com.abocode.jfaster.system.entity.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * 角色
 */
@Scope("prototype")
@Controller
@RequestMapping("/roleController")
public class RoleController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SystemRepository systemRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private RoleService roleService;

    /**
     * 角色列表页
     *
     * @return
     */
    @RequestMapping(params = "role")
    public ModelAndView role() {
        return new ModelAndView("system/role/roleList");
    }

    /**
     * easyuiAJAX请求数据
     * @param dataGridParam
     * @return
     */

    @RequestMapping(params = "roleGrid")
    @ResponseBody
    public DataGridData roleGrid(Role role, DataGridParam dataGridParam) {
        CriteriaQuery cq = new CriteriaQuery(Role.class).buildParameters(role, dataGridParam);
        return this.systemRepository.findDataGridData(cq);
    }

    /**
     * 删除角色
     *
     * @param role
     * @return
     */
    @RequestMapping(params = "delRole")
    @ResponseBody
    public AjaxJson delRole(Role role) {
        int count = userRepository.getUsersOfThisRole(role.getId());
        Assert.isTrue(count == 0, "角色: 仍被用户使用，请先删除关联关系");
        roleService.del(role);
        return AjaxJsonBuilder.success();
    }

    /**
     * 检查角色
     *
     * @return
     */
    @RequestMapping(params = "checkRole")
    @ResponseBody
    public ValidForm checkRole(HttpServletRequest request) {
        ValidForm v = new ValidForm();
        String roleCode = ConvertUtils.getString(request.getParameter("param"));
        String code = ConvertUtils.getString(request.getParameter("code"));
        List<Role> roles = systemRepository.findAllByProperty(Role.class, "roleCode", roleCode);
        if (roles.size() > 0 && !code.equals(roleCode)) {
            v.setInfo("角色编码已存在");
            v.setStatus("n");
        }
        return v;
    }

    /**
     * 角色录入
     *
     * @param role
     * @return
     */
    @RequestMapping(params = "saveRole")
    @ResponseBody
    public AjaxJson saveRole(Role role) {
        roleService.saveRole(role);
        return AjaxJsonBuilder.success();
    }

    /**
     * 角色列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "fun")
    public ModelAndView fun(HttpServletRequest request) {
        String roleId = request.getParameter("roleId");
        request.setAttribute("roleId", roleId);
        return new ModelAndView("system/role/roleSet");
    }

    /**
     * 角色所有用户信息列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "userList")
    public ModelAndView userList(HttpServletRequest request) {
        request.setAttribute("roleId", request.getParameter("roleId"));
        return new ModelAndView("system/role/roleUserList");
    }

    /**
     * 用户列表查询
     *  @param request
     * @param dataGridParam
     * @return
     */
    @RequestMapping(params = "roleUserDatagrid")
    @ResponseBody
    public DataGridData roleUserDatagrid(User user, HttpServletRequest request, DataGridParam dataGridParam) {
        CriteriaQuery cq = new CriteriaQuery(User.class).buildDataGrid(dataGridParam);
        //查询条件组装器
        String roleId = request.getParameter("roleId");
        Criterion cc = roleService.buildCriterion(roleId);
        cq.add(cc);
        HqlGenerateUtil.installHql(cq, user);
       return this.systemRepository.findDataGridData(cq);
    }

    /**
     * 获取用户列表
     *
     * @param user
     * @param request
     * @return
     */
    @RequestMapping(params = "getUserList")
    @ResponseBody
    public List<ComboTree> getUserList(User user, HttpServletRequest request) {
        String roleId = request.getParameter("roleId");
        return roleService.findComboTree(roleId, user);
    }

    /**
     * 角色树列表页面跳转
     *
     * @return
     */
    @RequestMapping(params = "roleTree")
    public ModelAndView roleTree(HttpServletRequest request) {
        request.setAttribute("orgId", request.getParameter("orgId"));
        return new ModelAndView("system/role/roleTree");
    }

    /**
     * 获取 组织机构的角色树
     *
     * @param request
     * @return 组织机构的角色树
     */
    @RequestMapping(params = "getRoleTree")
    @ResponseBody
    public List<ComboTree> getRoleTree(HttpServletRequest request) {
        String orgId = request.getParameter("orgId");
        return resourceRepository.findComboTree(orgId);
    }

    /**
     * 更新 组织机构的角色列表
     *
     * @param request request
     * @return 操作结果
     */
    @RequestMapping(params = "updateOrgRole")
    @ResponseBody
    public AjaxJson updateOrgRole(HttpServletRequest request) {
        String orgId = request.getParameter("orgId");
        String roleIds = request.getParameter("roleIds");
        List<String> roleIdList = IdUtils.extractIdListByComma(roleIds);
        roleService.updateOrgRole(orgId, roleIdList);
        return AjaxJsonBuilder.success();
    }


    /**
     * 设置权限
     *
     * @param request
     * @param comboTree
     * @return
     */
    @RequestMapping(params = "setAuthority")
    @ResponseBody
    public List<ComboTree> setAuthority(HttpServletRequest request, ComboTree comboTree) {
        String roleId = request.getParameter("roleId");
        return roleService.findComboTree(roleId, comboTree);
    }

    /**
     * 更新权限
     *
     * @param request
     * @return
     */
    @RequestMapping(params = "updateAuthority")
    @ResponseBody
    public AjaxJson updateAuthority(HttpServletRequest request) {
        String roleId = request.getParameter("roleId");
        String roleFunction = request.getParameter("rolefunctions");
        roleService.updateAuthority(roleId, roleFunction);
        return AjaxJsonBuilder.success();
    }


    /**
     * 角色页面跳转
     *
     * @param role
     * @param request
     * @return
     */
    @RequestMapping(params = "detail")
    public ModelAndView detail(Role role, HttpServletRequest request) {
        if (role.getId() != null) {
            role = systemRepository.find(Role.class, role.getId());
            request.setAttribute("roleView", role);
        }
        return new ModelAndView("system/role/role");
    }

    /**
     * 权限操作列表
     *
     * @param treegrid
     * @param request
     * @return
     */
    @RequestMapping(params = "setOperate")
    @ResponseBody
    public List<TreeGrid> setOperate(HttpServletRequest request, TreeGrid treegrid) {
        String roleId = request.getParameter("roleId");
        return roleService.setOperate(roleId, treegrid.getId());

    }

    /**
     * 操作录入
     *
     * @param request
     * @return
     */
    @RequestMapping(params = "saveOperate")
    @ResponseBody
    public AjaxJson saveOperate(HttpServletRequest request) {
        String fop = request.getParameter("fp");
        String roleId = request.getParameter("roleId");
        roleService.saveOperate(roleId, fop);
        return AjaxJsonBuilder.success();
    }

    /**
     * 按钮权限展示
     *
     * @param request
     * @param functionId
     * @param roleId
     * @return
     */
    @RequestMapping(params = "operationListForFunction")
    public ModelAndView operationListForFunction(HttpServletRequest request, String functionId, String roleId) {
        CriteriaQuery cq = new CriteriaQuery(Operation.class);
        cq.eq("function.id", functionId);
        cq.eq("status", Short.valueOf("0"));
        cq.add();
        List<Operation> operationList = this.systemRepository.findListByCq(cq, false);
        String[] operationCodes = systemRepository.getOperationCodesByRoleIdAndFunctionId(roleId, functionId);
        request.setAttribute("operationList", operationList);
        request.setAttribute("operationcodes", operationCodes);
        request.setAttribute("functionId", functionId);
        return new ModelAndView("system/role/operationListForFunction");
    }

    /**
     * 更新按钮权限
     *
     * @return
     */
    @RequestMapping(params = "updateOperation")
    @ResponseBody
    public AjaxJson updateOperation( String roleId, String functionId,String operationcodes) {
        roleService.updateOperation(roleId, functionId, operationcodes);
        return AjaxJsonBuilder.success();
    }

    /**
     * 按钮权限展示
     *
     * @param request
     * @param functionId
     * @param roleId
     * @return
     */
    @RequestMapping(params = "dataRuleListForFunction")
    public ModelAndView dataRuleListForFunction(HttpServletRequest request, String functionId, String roleId) {
        CriteriaQuery cq = new CriteriaQuery(DataRule.class);
        cq.eq("function.id", functionId);
        cq.add();
        List<DataRule> dataRuleList = this.systemRepository.findListByCq(cq, false);
        Set<String> dataRulecodes = systemRepository.getOperationCodesByRoleIdAndruleDataId(roleId, functionId);
        request.setAttribute("dataRuleList", dataRuleList);
        request.setAttribute("dataRulecodes", dataRulecodes);
        request.setAttribute("functionId", functionId);
        return new ModelAndView("system/role/dataRuleListForFunction");
    }


    /**
     * 更新按钮权限
     * @return
     */
    @RequestMapping(params = "updateDataRule")
    @ResponseBody
    public AjaxJson updateDataRule( String roleId,  String functionId, String dataRulecodes) {
        roleService.updateDataRule(roleId, functionId, dataRulecodes);
        return AjaxJsonBuilder.success();
    }


    /**
     * 添加 用户到角色 的页面  跳转
     *
     * @param request
     * @return 处理结果信息
     */
    @RequestMapping(params = "goAddUserToRole")
    public ModelAndView goAddUserToOrg(HttpServletRequest request) {
        return new ModelAndView("system/role/noCurRoleUserList");
    }

    /**
     * 获取 除当前 角色之外的用户信息列表
     *
     * @param request
     * @return 处理结果信息
     */
    @RequestMapping(params = "addUserToRoleList")
    @ResponseBody
    public DataGridData addUserToOrgList(User user, HttpServletRequest request, HttpServletResponse response, DataGridParam dataGridParam) {
        String roleId = request.getParameter("roleId");
        CriteriaQuery cq = new CriteriaQuery(User.class).buildParameters(user,dataGridParam);
        // 获取 当前组织机构的用户信息
        CriteriaQuery subCq = new CriteriaQuery(RoleUser.class);
        subCq.setProjection(Property.forName("user.id"));
        subCq.eq("role.id", roleId);
        subCq.add();
        cq.add(Property.forName("id").notIn(subCq.getDetachedCriteria()));
        cq.add();
       return this.systemRepository.findDataGridData(cq);
    }

    /**
     * 添加 用户到角色
     *
     * @param request
     * @return 处理结果信息
     */
    @RequestMapping(params = "doAddUserToRole")
    @ResponseBody
    public AjaxJson doAddUserToOrg(HttpServletRequest request) {
        String userIds = ConvertUtils.getString(request.getParameter("userIds"));
        String roleId = request.getParameter("roleId");
        roleService.doAddUserToOrg(roleId, userIds);
        return AjaxJsonBuilder.success();
    }
}
