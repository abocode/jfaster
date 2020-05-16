package com.abocode.jfaster.admin.system.repository;

import com.abocode.jfaster.core.common.model.json.DataGrid;
import com.abocode.jfaster.admin.system.dto.bean.ExlUserBean;
import com.abocode.jfaster.core.persistence.hibernate.qbc.CriteriaQuery;
import com.abocode.jfaster.core.repository.CommonRepository;
import com.abocode.jfaster.system.entity.Role;
import com.abocode.jfaster.system.entity.User;

import java.util.List;

public interface UserRepository extends CommonRepository {

    User checkUserExits(User user);

    String getUserRole(User user);

    void pwdInit(User user, String newPwd);

    int getUsersOfThisRole(String id);

    List<ExlUserBean> getExlUserList(DataGrid dataGrid, User user, CriteriaQuery cq);

    List<Role> findRoleById(String id);
}
