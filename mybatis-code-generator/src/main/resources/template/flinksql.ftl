<#assign maxLength=24 />
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
<#assign udfMaxLength=1 />
<#list entityList as entity>
    <#if (entity.columnName?length + entity.BLCSUdfName?length) gt udfMaxLength>
        <#assign udfMaxLength = (entity.columnName?length + entity.BLCSUdfName?length)/>
    </#if>
</#list>
<#function getUdfMaxSpace columnLength maxLength udfLength>
    <#assign spaceLength = maxLength - (columnLength + udfLength) + 1/>
    <#assign space = "" />
    <#list 1..spaceLength as l>
        <#assign space = space + " "/>
    </#list>
    <#return space>
</#function>
<#function matchTableName tableName>
    <#assign lastIndex = tableName?last_index_of("_") />
    <#if lastIndex == -1 >
        <#return "= '" + tableName + "'">
    <#else>
        <#return "like '" + tableName?substring(0, lastIndex) + "_%'" >
    </#if>
    <#return patternName>
</#function>
CREATE TABLE source_db_event(
    blcsSchema      VARCHAR,
    blcsTable       VARCHAR,
    blcsOptType     VARCHAR,
    blcsTopic       VARCHAR,
    blcsBefore      MAP,
    blcsAfter       MAP,
    blcsCreatedTime BIGINT
) WITH (
    format.type='blcs',
    connector.type='kafka',
    connector.version='0.10',
    connector.topic='...',
    bootstrap.servers='10.253.14.76:9092,10.139.55.93:9092,10.253.9.178:9092,10.139.106.202:9092,10.253.9.154:9092,10.253.160.91:9092,10.253.160.92:9092'
);


CREATE VIEW view_${entityList[0].tableName} AS
SELECT
<#list entityList as entity>
<#if entity_has_next>
    ${entity.BLCSUdfName}(blcsAfter, '${entity.columnName}')${getUdfMaxSpace(entity.columnName?length, udfMaxLength, entity.BLCSUdfName?length)}AS ${entity.columnName},
<#else>
    ${entity.BLCSUdfName}(blcsAfter, '${entity.columnName}')${getUdfMaxSpace(entity.columnName?length, udfMaxLength, entity.BLCSUdfName?length)}AS ${entity.columnName}
</#if>
</#list>
FROM
    source_db_event
WHERE
    blcsTable ${matchTableName(entityList[0].tableName)}
AND ...;


CREATE TABLE sink_${entityList[0].tableName}(
    `event_topic`              VARCHAR,
    `event_type`               VARCHAR,
    `event_analy_ingestion_ts` VARCHAR,
    `event_version`            VARCHAR,
<#list entityList as entity>
<#if entity_has_next>
    `${entity.columnName}`${getSpace(entity.columnName?length, maxLength)}${entity.columnType},
<#else>
    `${entity.columnName}`${getSpace(entity.columnName?length, maxLength)}${entity.columnType}
</#if>
</#list>
) WITH (
    format.type='json',
    updateMode='upsert',
    connector.type='kafka',
    connector.version='0.10',
    connector.topic='...',
    bootstrap.servers='10.253.14.76:9092,10.139.55.93:9092,10.253.9.178:9092,10.139.106.202:9092,10.253.9.154:9092,10.253.160.91:9092,10.253.160.92:9092'
);


INSERT INTO
    sink_${entityList[0].tableName}
SELECT
    `xxxx`,
    `xxxx`,
    FROM_UNIXTIME(NOW(), 'yyyy-MM-dd HH:mm:ss'),
    `xxxx`,
<#list entityList as entity>
<#if entity_has_next>
    `${entity.columnName}`,
<#else>
    `${entity.columnName}`
</#if>
</#list>
FROM
    view_${entityList[0].tableName};