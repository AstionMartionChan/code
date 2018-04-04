package ${entityList[0].packagePath}.result;

import java.util.Date;

/**
 * ${entityList[0].tableName}
 */
@ApiModel("${entityList[0].tableName}")
public class ${entityList[0].resultName} {

    <#list entityList as entity>
    // ${entity.remark}
    @ApiModelProperty("${entity.remark}")
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
