package ${entityList[0].packagePath}.dao;

import ${entityList[0].packagePath}.po.${entityList[0].entityName};
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* ${entityList[0].tableName} 表
*/
@Repository("${entityList[0].repositoryDaoName}")
public interface ${entityList[0].daoName} {

    /**
     * 根据Id获取Entity
     * @param id  自增Id（主键）
     * @return ${entityList[0].entityName}  POJO类
     */
    ${entityList[0].entityName} selectById(Long id);

    /**
     * 根据条件获取Entity List
     * @param entity  POJO类
     * @return List<${entityList[0].entityName}>  POJO类 集合
     */
    List<${entityList[0].entityName}> select(${entityList[0].entityName} entity);

    /**
     * 插入
     * @param entity  POJO类
     */
    void insert(${entityList[0].entityName} entity);

    /**
     * 更新
     * @param entity  POJO类
     */
    void update(${entityList[0].entityName} entity);

}