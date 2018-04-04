<#assign maxLength=1 />
<#list entityList as entity>
    <#if (entity.columnName?length) gt maxLength>
        <#assign maxLength= (entity.columnName?length)/>
    </#if>
</#list>
<#function getSpace columnLength maxLength>
    <#assign spaceLength = maxLength - columnLength + 1/>
    <#assign space = "" />
    <#list 1..spaceLength as l>
        <#assign space = space + " "/>
    </#list>
    <#return space>
</#function>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//ibatis.apache.org//DTD Mapper 3.0//EN" "http://ibatis.apache.org/dtd/ibatis-3-mapper.dtd">
<mapper namespace="${packagePath}.dao.${entityList[0].daoName}">


    <!-- Start Auto Common -->

    <select id="selectById" parameterType="long"  resultType="${entityList[0].packagePath}.po.${entityList[0].entityName}">
        SELECT
        <#list entityList as entity>
        <#if entity_has_next>
            ${entity.columnName}${getSpace(entity.columnName?length, maxLength)}AS ${entity.fieldName},
        <#else>
            ${entity.columnName}${getSpace(entity.columnName?length, maxLength)}AS ${entity.fieldName}
        </#if>
        </#list>
        FROM
            ${entityList[0].tableName}
        WHERE
            id = ${r'#{id}'}
    </select>

    <select id="select" parameterType="${entityList[0].packagePath}.po.${entityList[0].entityName}"  resultType="${entityList[0].packagePath}.po.${entityList[0].entityName}">
        SELECT
        <#list entityList as entity>
        <#if entity_has_next>
            ${entity.columnName}${getSpace(entity.columnName?length, maxLength)}AS ${entity.fieldName},
        <#else>
            ${entity.columnName}${getSpace(entity.columnName?length, maxLength)}AS ${entity.fieldName}
        </#if>
        </#list>
        FROM
            ${entityList[0].tableName}
        <where>
        <#list entityList as entity>
        <#if entity.fieldType == "String">
        <if test="${entity.fieldName} != null and ${entity.fieldName} != ''">
            AND ${entity.columnName} = ${r'#'}{${entity.fieldName}}
        </if>
        <#else>
        <if test="${entity.fieldName} != null ">
            AND ${entity.columnName} = ${r'#'}{${entity.fieldName}}
        </if>
        </#if>

        </#list>
        </where>
    </select>


    <insert id="insert" parameterType="${entityList[0].packagePath}.po.${entityList[0].entityName}" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO ${entityList[0].tableName} (
            <#list entityList as entity>
            <#if entity_index gt 0>
            <#if entity_has_next>
            ${entity.columnName},
            <#else>
            ${entity.columnName}
            </#if>
            </#if>
            </#list>
        )
        VALUES (
            <#list entityList as entity>
            <#if entity_index gt 0>
            <#if entity_has_next>
            ${r'#'}{${entity.fieldName}},
            <#else>
            ${r'#'}{${entity.fieldName}}
            </#if>
            </#if>
            </#list>
        )
    </insert>

    <update id="update" parameterType="${entityList[0].packagePath}.po.${entityList[0].entityName}">
        UPDATE ${entityList[0].tableName}
        <set>
        <#list entityList as entity>
        <#if entity_index gt 0>
        <#if entity.fieldType == "String">
        <if test="${entity.fieldName} != null and ${entity.fieldName} != ''">
            ${entity.columnName} = ${r'#'}{${entity.fieldName}},
        </if>
        <#else>
        <if test="${entity.fieldName} != null ">
            ${entity.columnName} = ${r'#'}{${entity.fieldName}},
        </if>
        </#if>
        </#if>

        </#list>
        </set>
        WHERE
            id = ${r'#'}{id}
    </update>

    <!-- End Auto Common  -->

</mapper>


