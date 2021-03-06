package com.abocode.jfaster.admin.system.service.impl;

import com.abocode.jfaster.admin.system.dto.ExlUserDto;
import com.abocode.jfaster.admin.system.repository.SystemRepository;
import com.abocode.jfaster.admin.system.repository.UserRepository;
import com.abocode.jfaster.admin.system.service.BeanToTagConverter;
import com.abocode.jfaster.admin.system.service.UserService;
import com.abocode.jfaster.api.core.AvailableEnum;
import com.abocode.jfaster.api.system.UserDto;
import com.abocode.jfaster.core.common.constants.Globals;
import com.abocode.jfaster.core.common.exception.BusinessException;
import com.abocode.jfaster.core.common.model.json.AjaxJson;
import com.abocode.jfaster.core.common.model.json.ComboBox;
import com.abocode.jfaster.core.common.util.IdUtils;
import com.abocode.jfaster.core.common.util.PasswordUtils;
import com.abocode.jfaster.core.common.util.StrUtils;
import com.abocode.jfaster.core.persistence.hibernate.qbc.CriteriaQuery;
import com.abocode.jfaster.core.platform.poi.excel.ExcelImportUtil;
import com.abocode.jfaster.core.platform.poi.excel.entity.ImportParams;
import com.abocode.jfaster.core.platform.utils.FunctionSortUtils;
import com.abocode.jfaster.core.platform.utils.SystemMenuUtils;
import com.abocode.jfaster.core.platform.view.FunctionView;
import com.abocode.jfaster.core.repository.DataGridData;
import com.abocode.jfaster.core.repository.DataGridParam;
import com.abocode.jfaster.core.repository.TagUtil;
import com.abocode.jfaster.system.entity.*;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    public static final String USER_ID = "user.id";
    public static final String FONT_COLOR_RED_FONT = "<font color='red'>失败!</font>";
    @Resource
    private SystemRepository systemRepository;
    @Resource
    private UserRepository userRepository;

    public String getMenus(User u) {
        // 登陆者的权限
        Set<Function> actionList = new HashSet<>();// 已有权限菜单
        List<RoleUser> rUsers = systemRepository.findAllByProperty(RoleUser.class, USER_ID, u.getId());
        for (RoleUser ru : rUsers) {
            Role role = ru.getRole();
            List<RoleFunction> roleFunctionList = systemRepository.findAllByProperty(RoleFunction.class, "role.id", role.getId());
            for (RoleFunction roleFunction : roleFunctionList) {
                Function function = roleFunction.getFunction();
                actionList.add(function);
            }
        }
        // 一级权限菜单
        List<FunctionView> pActionList = new ArrayList<>();
        // 二级权限菜单
        List<FunctionView> cActionList = new ArrayList<>();
        for (Function function : actionList) {
            FunctionView functionBean = BeanToTagConverter.convertFunction(function);
            if (function.getFunctionLevel() == 0) {
                pActionList.add(functionBean);
            } else if (function.getFunctionLevel() == 1) {
                cActionList.add(functionBean);
            }
        }
        // 菜单栏排序
        FunctionSortUtils.sortView(pActionList);
        FunctionSortUtils.sortView(cActionList);
        return SystemMenuUtils.getMenu(pActionList, cActionList);
    }

    @Override
    public Object getAll() {
        return systemRepository.findAll(User.class);
    }

    @Override
    public void restPassword(String id, String password) {
        User users = systemRepository.find(User.class, id);
        users.setPassword(PasswordUtils.encrypt(users.getUsername(), password, PasswordUtils.getStaticSalt()));
        users.setStatus(Globals.USER_NORMAL);
        systemRepository.update(users);
    }

    @Override
    public void updatePwd(User user, String password, String newPassword) {
        String pString = PasswordUtils.encrypt(user.getUsername(), password, PasswordUtils.getStaticSalt());
        Assert.isTrue(!pString.equals(user.getPassword()), "原密码不正确");
        user.setPassword(PasswordUtils.encrypt(user.getUsername(), newPassword, PasswordUtils.getStaticSalt()));
        systemRepository.update(user);
    }

    @Override
    public void lockById(String id) {
        User user = userRepository.find(User.class, id);
        Assert.isTrue("admin".equals(user.getUsername()), "超级管理员[admin]不可锁定");
        Assert.isTrue(Globals.USER_FORBIDDEN.equals(user.getStatus()), "锁定账户已经锁定");
        user.setStatus(Globals.USER_FORBIDDEN);
        userRepository.update(user);
    }

    @Override
    public List<ComboBox> findComboBox(String id, String[] fields) {
        List<Org> departs = new ArrayList<>();
        if (StrUtils.isNotEmpty(id)) {
            Object[] object = new Object[]{id};
            List<Org[]> resultList = userRepository.findByHql("from Org d,UserOrg uo where d.id=uo.orgId and uo.id=?0", object);
            for (Org[] departArr : resultList) {
                departs.add(departArr[0]);
            }
        }
        List<Org> departList = userRepository.findAll(Org.class);
        return TagUtil.getComboBox(departList, departs, fields);
    }

    @Override
    public CriteriaQuery buildCq(User user, DataGridParam dataGridParam, String orgIds) {
        CriteriaQuery cq = new CriteriaQuery(User.class).buildParameters(user, null, dataGridParam);
        Short[] userstate = new Short[]{Globals.USER_NORMAL, Globals.USER_ADMIN, Globals.USER_FORBIDDEN};
        cq.in("status", userstate);

        List<String> orgIdList = IdUtils.extractIdListByComma(orgIds);
        // 获取 当前组织机构的用户信息
        if (!CollectionUtils.isEmpty(orgIdList)) {
            CriteriaQuery subCq = new CriteriaQuery(UserOrg.class);
            subCq.setProjection(Property.forName("tsUser.id"));
            subCq.in("tsDepart.id", orgIdList.toArray());
            subCq.add();

            cq.add(Property.forName("id").in(subCq.getDetachedCriteria()));
        }
        cq.add();
        return cq;
    }

    @Override
    public void del(String id) {
        User user = userRepository.find(User.class, id);
        List<RoleUser> roleUser = userRepository.findAllByProperty(RoleUser.class, USER_ID, id);
        Assert.isTrue(!user.getStatus().equals(Globals.USER_ADMIN), "超级管理员不可删除");
        if (!CollectionUtils.isEmpty(roleUser)) {
            // 删除用户时先删除用户和角色关系表
            delRoleUser(user);
            userRepository.executeSql("delete from t_s_user_org where user_id=?", user.getId()); // 删除 用户-机构 数据
            userRepository.delete(user);
        } else {
            userRepository.delete(user);
        }
    }

    @Override
    public void saveUser(User user, String roleId, String password, String orgIds) {
        if (StrUtils.isNotEmpty(user.getId())) {
            User users = userRepository.find(User.class, user.getId());
            users.setEmail(user.getEmail());
            users.setOfficePhone(user.getOfficePhone());
            users.setMobilePhone(user.getMobilePhone());
            userRepository.executeSql("delete from t_s_user_org where user_id=?", user.getId());
            saveUserOrgList(orgIds, user);
            users.setRealName(user.getRealName());
            users.setStatus(Globals.USER_NORMAL);
            userRepository.update(users);
            List<RoleUser> ru = userRepository.findAllByProperty(RoleUser.class, USER_ID, user.getId());
            userRepository.deleteEntities(ru);
            if (StrUtils.isNotEmpty(roleId)) {
                saveRoleUser(users, roleId);
            }
        } else {
            User users = userRepository.findUniqueByProperty(User.class, "username", user.getUsername());
            if (users == null) {
                user.setPassword(PasswordUtils.encrypt(user.getUsername(), password, PasswordUtils.getStaticSalt()));
                user.setStatus(Globals.USER_NORMAL);
                userRepository.save(user);
                saveUserOrgList(orgIds, user);
                if (StrUtils.isNotEmpty(roleId)) {
                    saveRoleUser(user, roleId);
                }
            }
        }
    }

    /**
     * 保存 用户-组织机构 关系信息
     *
     * @param orgIds
     * @param user   user
     */
    private void saveUserOrgList(String orgIds, User user) {
        List<UserOrg> userOrgList = new ArrayList<>();
        List<String> orgIdList = IdUtils.extractIdListByComma(orgIds);
        for (String orgId : orgIdList) {
            Org depart = new Org();
            depart.setId(orgId);
            UserOrg userOrg = new UserOrg();
            userOrg.setUser(user);
            userOrg.setOrg(depart);

            userOrgList.add(userOrg);
        }
        if (!userOrgList.isEmpty()) {
            userRepository.batchSave(userOrgList);
        }
    }

    private void saveRoleUser(User user, String roleidstr) {
        String[] roleids = roleidstr.split(",");
        for (int i = 0; i < roleids.length; i++) {
            RoleUser rUser = new RoleUser();
            Role role = userRepository.find(Role.class, roleids[i]);
            rUser.setRole(role);
            rUser.setUser(user);
            userRepository.save(rUser);

        }
    }

    private void delRoleUser(User user) {
        // 同步删除用户角色关联表
        List<RoleUser> roleUserList = userRepository.findAllByProperty(RoleUser.class, USER_ID, user.getId());
        for (RoleUser tRoleUser : roleUserList) {
            userRepository.delete(tRoleUser);
        }
    }

    @Override
    public void importFile(Map<String, MultipartFile> fileMap) {
        MultipartFile file;
        List<ExlUserDto> userList;
        List<User> userEntities = new ArrayList<>();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            try {
                //解析文件
                file = entity.getValue();
                userList = (List<ExlUserDto>) ExcelImportUtil
                        .importExcelByIs(file.getInputStream(),
                                ExlUserDto.class, new ImportParams());
                //验证文件
                Assert.isTrue(!CollectionUtils.isEmpty(userList), "<font color='red'>失败!</font> Excel中没有可以导入的数据");
                for (ExlUserDto exlUserVo : userList) {
                    AjaxJson j = new AjaxJson().volatileBean(exlUserVo);
                    Assert.isTrue(j.isSuccess(), "数据验证失败");

                    //判断帐号是否存在
                    User u = this.userRepository.findUniqueByProperty(User.class, "username", exlUserVo.getUsername());
                    Assert.isTrue(!StrUtils.isNotEmpty(u), FONT_COLOR_RED_FONT + exlUserVo.getUsername() + " 帐号已经存在");
                    //判断组织机构是否存在
                    List<Org> exlDeparts = new ArrayList<>();
                    String[] departNames = exlUserVo.getOrgName().split(",");
                    for (int i = 0; i < departNames.length; i++) {
                        List<Org> departs = userRepository.findAllByProperty(Org.class, "departname", departNames[i]);
                        Assert.isTrue(!CollectionUtils.isEmpty(departs), FONT_COLOR_RED_FONT + exlUserVo.getOrgName() + " 组织机构不存在");
                        exlDeparts.add(departs.get(0));
                    }


                    List<Role> exlRoles = new ArrayList<>();
                    String[] roleNames = exlUserVo.getRoleName().split(",");
                    for (int i = 0; i < roleNames.length; i++) {
                        //判断角色是否存在
                        List<Role> roles = userRepository.findAllByProperty(Role.class, "roleName", roleNames[i]);
                        Assert.isTrue(!CollectionUtils.isEmpty(roles), FONT_COLOR_RED_FONT + exlUserVo.getRoleName() + " 角色不存在");
                        exlRoles.add(roles.get(0));
                    }

                    User userEntity = new User();
                    BeanUtils.copyProperties(exlUserVo, userEntity);
                    userEntity.setOrgs(exlDeparts);
                    userEntity.setRoles(exlRoles);
                    userEntity.setStatus(AvailableEnum.AVAILABLE.getShortValue());
                    userEntities.add(userEntity);
                }

                for (User userEntity : userEntities) {
                    String pwd = userEntity.getPassword();
                    userEntity.setPassword(null);
                    String uid = (String) this.userRepository.save(userEntity);
                    userEntity = this.userRepository.find(User.class, uid);
                    userEntity.setPassword(PasswordUtils.encrypt(userEntity.getUsername(), pwd, PasswordUtils.getStaticSalt()));
                    userRepository.update(userEntity);

                    //保存组织机构
                    for (Org depart : userEntity.getOrgs()) {
                        UserOrg userOrg = new UserOrg();
                        userOrg.setUser(userEntity);
                        userOrg.setOrg(depart);
                        this.userRepository.save(userOrg);
                    }

                    //保存角色
                    for (Role role : userEntity.getRoles()) {
                        RoleUser roleUser = new RoleUser();
                        roleUser.setRole(role);
                        roleUser.setUser(userEntity);
                        this.userRepository.save(roleUser);
                    }
                }
            } catch (Exception e) {
                throw new BusinessException("<font color='red'>失败!</font> 检查文件数据、格式等是否正确！详细信息：", e);
            }
        }
    }

    @Override
    public List<ExlUserDto> findExportUserList(User user, String orgIds, DataGridParam dataGridParam) {
        dataGridParam.setPage(0);
        dataGridParam.setRows(1000000);
        CriteriaQuery cq = buildCq(user, dataGridParam, orgIds);
        return this.userRepository.getExlUserList(dataGridParam, user, cq);
    }

    @Override
    public DataGridData findDataGridData(UserDto userDto, String departid, DataGridParam dataGridParam) {
        CriteriaQuery cq = new CriteriaQuery(User.class).buildParameters(userDto, dataGridParam);
        if (!StrUtils.isEmpty(departid)) {
            DetachedCriteria dc = cq.getDetachedCriteria();
            DetachedCriteria dcDepart = dc.createCriteria("userOrgList");
            dcDepart.add(Restrictions.eq("parentOrg.id", departid));
        }
        Short[] userstate = new Short[]{Globals.USER_NORMAL, Globals.USER_ADMIN};
        cq.in("status", userstate);
        cq.add();
        return userRepository.findDataGridData(cq);
    }
}
