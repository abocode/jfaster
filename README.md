#BMS项目说明

     1.该项目由个人发起忙，只用于技术交流，不用于做商业用途，如果有侵犯你的权益，请和个人联系：guanxf_m@126.com。
     2.项目的所有代码均开源共享，框架在jeecg的基础上发展而来，并且在真实环境中经过大量的检验，确保系统的安全及稳定。
       系统主要分为三大基本模块，bms-core模块、bms-platform及bms-project模块（请看模块说明部分）。
     3.项目框架为：spring MVC+Hibernate+Spring jdbc+  bootstrap+jquery easyui。
     4.运行环境：所有web服务器及jdk1.6+,浏览器为ie8+。

##项目优势
   
    1.系统采用开源协议BSD，源代码完全开源，无商业限制。
    2.框架为主流框架上手简单,文档齐全。
    3.完善的代码封装，简单功能完全不用写任何的js、css，根据数据库表，使用代码生成器分分钟完美呈现对表的CRUD功能。
    4.代码结构清晰，便于维护。
    5.页面校验自动完成，只需要修改简单的属性配置就可达到你想要的效果。
    6.无冗余代码，项目中可以直接使用，同时无需在页上做任何改动（在语言管理中有系统名称，版权所有等动态参数）。
    7.封装POI导入导出插件，读写Excel简单快捷。
    8.支持多语言，开发国际化项目非常方便。
    9.多种模版选择，可以在后台模版管理中进行选择，满足同一套框架，适用于不同的客户。
    10.经历过大并发生产环境积累，系统稳定。
    11.支持主流数据库。
    12.前端不懂java也可以进行模版开发，后端不懂前端也可进行功能开发，协作就是那么简单。
    13.真正节省时间的框架，快而稳是我们追求的目标。
   

##模块说明

### bms-core说明

     该模块封装了jdbc及Hibernate对数据库的操作，统一方法命名Hibernate采用findXXX()，纯jdbc操作则使用queryXXX()。

### bms-paltform说明

    该部分对页面显示元素、数据集及大量基础工具进行封装，在项目开发中可以直接进行使用。

### bms-project说明

    该部分为业务代码的实现，拥有jeecg的代码生成器及系统管理功能，是一套小而巧的框架，技术选型最优的选择。

### 其他说明

     1.bms-core，bms-paltform的最新jar在lib目录下,可以直接引入。
     2、文档在：项目根目录的doc中。
     3、bms-project.zip 为mysql的数据库脚本（其他数据库请使用hibernate自行初始化，修改hibernate的生成策略即可）。
     
     
## 系统功能介绍

     1.用户、菜单、角色、权限完全拥有。
     2.角色可配置权限，用户可配置权限,可以精确到具体的按钮。
     3.管理系统其他功能：你还想要啥？
     
##加入我们：

    1.长期招收有意于bms开发的技术人才，5年以上开发经验。联系方式：邮箱:guanxf_m@126.com,昵称:糊涂,交流qq群:140586555。
    2.欢迎各位开发者提出建议、push代码。

