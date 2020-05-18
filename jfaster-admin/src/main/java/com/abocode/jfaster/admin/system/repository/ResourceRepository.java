package com.abocode.jfaster.admin.system.repository;

import com.abocode.jfaster.admin.system.dto.UploadFileDto;
import com.abocode.jfaster.core.common.model.json.ComboTree;
import com.abocode.jfaster.core.common.model.json.ImportFile;
import com.abocode.jfaster.core.common.model.json.TreeGrid;
import com.abocode.jfaster.core.repository.CommonRepository;
import com.abocode.jfaster.core.platform.view.interactions.easyui.ComboTreeModel;
import com.abocode.jfaster.core.platform.view.interactions.easyui.TreeGridModel;
import com.abocode.jfaster.system.entity.Org;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ResourceRepository extends CommonRepository {
    /**
     * 文件上传
     *
     * @param uploadFile
     */
    <T> T uploadFile(UploadFileDto uploadFile);

    /***
     * 预览及下载文件
     * @param uploadFile
     * @return
     */
    HttpServletResponse viewOrDownloadFile(UploadFileDto uploadFile);

    /**
     * 生成XML文件
     *
     * @param fileName XML全路径
     */
    HttpServletResponse createXml(ImportFile fileName);

    /**
     * 解析XML文件
     *
     * @param fileName XML全路径
     */
    void parserXml(String fileName);

    /**
     * 根据模型生成ComboTree JSON
     *
     * @param all       全部对象
     * @param comboTree 模型
     * @return List<ComboTree>
     */
    List<ComboTree> comTree(List<Org> all, ComboTree comboTree);

    /**
     * 根据模型生成JSON
     *
     * @param all       全部对象
     * @param in        已拥有的对象
     * @param recursive 是否递归加载所有子节点
     * @return List<ComboTree>
     */
    List<ComboTree> ComboTree(List all, ComboTreeModel comboTreeModel,
                              List in, boolean recursive);


    /**
     * 构建树形数据表
     *
     * @param all
     * @param treeGridModel
     * @return
     */
    List<TreeGrid> treegrid(List all, TreeGridModel treeGridModel);

    /**
     * 读取上传文件的内容
     *
     * @param uploadFile
     * @return
     */
    String getUploadFileContent(UploadFileDto uploadFile);

    void readAndParserXml(String ctxPath, UploadFileDto uploadFile);

    List<ComboTree> findComboTree(String orgId);
}
