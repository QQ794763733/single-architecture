-- 设置模块ID
SET @ID=${id};
-- 插入模块权力
INSERT INTO authority VALUES(@ID,'${moduleName}','${className?upper_case}',0);
-- 插入模块增删改查权力
INSERT INTO authority VALUES(@ID+1,'增加${moduleName}','${className?upper_case}:ADD',@ID);
INSERT INTO authority VALUES(@ID+2,'删除${moduleName}','${className?upper_case}:DELETE',@ID);
INSERT INTO authority VALUES(@ID+3,'修改${moduleName}','${className?upper_case}:MODIFY',@ID);
INSERT INTO authority VALUES(@ID+4,'查询${moduleName}','${className?upper_case}:GET',@ID);