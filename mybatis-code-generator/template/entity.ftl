package ${packagePath}.po;

import java.util.Date;

/**
 * ${entityList[0].tableName} 表 Entity
 */
public class ${entityList[0].entityName} {

    <#list entityList as entity>
    // ${entity.remark}
    private ${entity.fieldType} ${entity.fieldName};

    </#list>

    <#list entityList as entity>

    /**
     * set ${entity.remark}
     * @param ${entity.fieldName} ${entity.remark}
     */
    public void set${entity.upperFieldName}(${entity.fieldType} ${entity.fieldName}) {
        this.${entity.fieldName} = ${entity.fieldName};
    }

    /**
     * get ${entity.remark}
     * @return ${entity.fieldType} ${entity.remark}
     */
    public ${entity.fieldType} get${entity.upperFieldName}() {
        return ${entity.fieldName};
    }

    </#list>

}
