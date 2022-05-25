package com.kirito666.marktoolsserver.util;

import com.kirito666.marktoolsserver.common.BaseLog;
import com.kirito666.marktoolsserver.common.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Finger
 * @Description TODO Mongodb操作工具类 简单封装
 * @date 2020/7/24 16:21
 */
@Component
public class MongoDBUtil {
    public static MongoDBUtil mongodbUtil;

    @PostConstruct
    public void init() {
        mongodbUtil = this;
        mongodbUtil.mongoTemplate = this.mongoTemplate;
    }

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 保存对象List到指定集合中
     * <p>
     * 也可以在实体类上使用@Document(collection=“集合名称”)指定集合名称，未指定则默认实体类的类名为集合名称
     *
     * @param entiys
     */
    public static void saveAll(String collName, List<?> entiys) {
        for (Object entiy : entiys) {
            saveData(collName, entiy);
        }
    }


    /**
     * 保存单个对象到指定集合中
     *
     * @param collName 集合名称
     * @param entiy    实体名称
     */
    public static void saveOne(String collName, Class<?> entiy) {
        saveData(collName, entiy);
    }

    /**
     * 根据id倒序查询 集合中的数据
     *
     * @param entiy     数据实体
     * @param collName  集合名称
     * @param direction 倒序/正序 Direction.DESC/ASC
     * @param
     */
    public static Object findSortById(Class<?> entiy, String collName, Sort.Direction direction) {
        Query query = new Query().with(Sort.by(direction, "id")).skip(0).limit(10);
        return mongodbUtil.mongoTemplate.find(query, entiy, collName);
    }

    /**
     * 查询返回指定字段
     *
     * @param fields   需要返回的指定字段  eg: fields.add("runTime");
     * @param clazz    数据实体类class
     * @param collName 集合名称
     * @param map      Map<查询条件key,查询条件value>  eg: map.put("describe", "查询用户信息");
     * @param returnId 返回字段的时候id默认为返回，不返回id则field设置
     * @return
     */
    public static Object findDesignField(List<String> fields, Map<String, Object> map, Class<?> clazz, String collName, boolean returnId) {
        Criteria criteria = null;
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件不能为null");
        }
        for (String key : map.keySet()) {
            criteria = Criteria.where(key).is(map.get(key));
        }
        Query query = new Query(criteria);
        for (String field : fields) {
            query.fields().include(field);
        }
        if (!returnId) {
            query.fields().exclude("id");
        }
        return mongodbUtil.mongoTemplate.find(query, clazz, collName);
    }

    /**
     * 查询指定集合中的所有数据
     *
     * @param entiy    数据实体类
     * @param collName 集合名称
     */
    public static Object findAll(Class<?> entiy, String collName) {
        return mongodbUtil.mongoTemplate.findAll(entiy, collName);
    }

    /**
     * 模糊查询 根据 key 可以到 collName 中进行模糊查询 并排序
     *
     * @param param     匹配的参数名称
     * @param key       模糊搜索关键字
     * @param collName  集合名称
     * @param sortField 排序字段
     * @param direction Direction.desc /asc 倒序/正序
     * @return java.lang.Object
     **/
    public static Object findLikeByParam(String param, String key, String collName, String sortField, Sort.Direction direction) {
        Pattern pattern = Pattern.compile("^.*" + key + ".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query(Criteria.where(param).regex(pattern)).with(Sort.by(direction, sortField));
        return mongodbUtil.mongoTemplate.find(query, BaseLog.class, collName);
    }

    /**
     * 向指定集合设置索引
     *
     * @param collName  集合名称
     * @param indexName 索引名称
     * @param map       map.put("添加索引的字段",Direction.ASC/DESC)
     */
    public static void createIndex(String collName, String indexName, Map<String, Sort.Direction> map) {
        for (String key : map.keySet()) {
            Assert.notNull(key, "添加索引的字段不能为null");
        }
        Index index = new Index().named(indexName);
        for (String key : map.keySet()) {
            index.on(key, map.get(key));
        }
        mongodbUtil.mongoTemplate.indexOps(collName).ensureIndex(index);
    }

    /**
     * 获取指定集合中的索引信息
     *
     * @param collName 集合名称
     * @return
     */
    public static Object getIndexInfo(String collName) {
        return mongodbUtil.mongoTemplate.indexOps(collName).getIndexInfo();
    }


    /**
     * 根据索引名称删除索引
     *
     * @param indexName 索引名称
     * @param collName  集合名称
     */
    public static void removeIndexByName(String collName, String indexName) {
        mongodbUtil.mongoTemplate.indexOps(collName).dropIndex(indexName);
    }


    /**
     * 删除指定集合中得所有索引
     *
     * @param collName 集合名称
     */
    public static void removeIndexByName(String collName) {
        mongodbUtil.mongoTemplate.indexOps(collName).dropAllIndexes();
    }


    /**
     * 根据指定key 和value到指定collName集合中删除数据
     *
     * @param key
     * @param value
     * @param collName
     */
    public static void removeAllByParam(String key, String value, String collName) {
        Criteria criteria = Criteria.where(key).is(value);
        Query query = Query.query(criteria);
        mongodbUtil.mongoTemplate.remove(query, collName);
    }


    /**
     * 根据指定条件查询 并排序
     *
     * @param obj      数据对象
     * @param map      Map<"查询条件key"，查询条件值> map
     * @param collName 集合名称
     * @return
     */
    public static List<? extends Object> findSortByParam(Class<?> obj, String collName, Map<String, Object> map, String sortField, Sort.Direction direction) {
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件key不能为null");
        }
        Criteria criteria = null;
        criteria = getCriteria(criteria, map);
        if (criteria == null) {
            return new ArrayList<>();
        }
        Query query = Query.query(criteria).with(Sort.by(direction, sortField));
        return mongodbUtil.mongoTemplate.find(query, obj, collName);
    }


    /**
     * 范围查询
     * <p>
     * 查询大于等于begin  小于等于end范围内条件匹配得数据并排序
     *
     * @param obj           数据对象
     * @param collName      集合名称
     * @param map           Map<"查询条件key"，查询条件值> map
     * @param sortField     排序字段
     * @param direction     排序方式  Direction.asc   / Direction.desc
     * @param rangeCriteria 示例： lt小于  lte 小于等于  gt大于  gte大于等于 eq等于 ne不等于
     *                      <p>
     *                      Criteria rangeCriteria=Criteria.where("createDate").gte(begin).lte(end));
     *                      <p>
     *                      createDate:数据库中的时间字段，gegin:起始时间  end:结束时间
     * @return
     */
    public static List<? extends Object> findRangeByParam(Class<?> obj, String collName, Map<String, Object> map,
                                                          String sortField, Sort.Direction direction, Criteria rangeCriteria) {
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件key不能为null");
        }
        Criteria criteria = null;
        criteria = getCriteria(criteria, map);
        if (criteria == null) {
            return new ArrayList<>();
        }
        Query query = new Query().addCriteria(rangeCriteria).addCriteria(criteria).with(Sort.by(direction, sortField));
        return mongodbUtil.mongoTemplate.find(query, obj, collName);
    }


    /**
     * 根据指定key value到指定集合中查询匹配得数量
     *
     * @param collName
     * @param key
     * @param value
     * @return
     */
    public static long count(String collName, String key, String value) {
        Query query = Query.query(Criteria.where(key).is(value));
        return mongodbUtil.mongoTemplate.count(query, collName);
    }


    /**
     * 在指定范围内查询匹配条件的数量
     *
     * @param clazz         数据实体类
     * @param collName      集合名称
     * @param map           查询条件map
     * @param rangeCriteria 范围条件  Criteria rangeCriteria= Criteria.where("数据库字段").gt/gte（起始范围）.lt/lte（结束范围）
     * @return
     */
    public static Long countRangeCondition(Class<?> clazz, String collName, Criteria rangeCriteria, Map<String, Object> map) {
        Criteria criteria = null;
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件key不能为null");
        }
        for (String key : map.keySet()) {
            criteria = Criteria.where(key).is(map.get(key));
        }
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        query.addCriteria(rangeCriteria);
        return mongodbUtil.mongoTemplate.count(query, clazz, collName);
    }


    /**
     * 指定集合 根据条件查询出符合的第一条数据
     *
     * @param clazz    数据对象
     * @param map      条件map  Map<条件key,条件value> map
     * @param collName 集合名
     * @return
     */
    public static Object findSortFirst(Class<?> clazz, Map<String, Object> map, String collName, String field, Sort.Direction direction) {
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件key不能为null");
        }
        Criteria criteria = null;
        criteria = getCriteria(criteria, map);
        Query query = Query.query(criteria).with(Sort.by(direction, field));
        return mongodbUtil.mongoTemplate.findOne(query, clazz, collName);
    }


    /**
     * 指定集合 修改数据，且修改所找到的所有数据
     *
     * @param accordingKey   修改条件 key
     * @param accordingValue 修改条件 value
     * @param map            Map<修改内容 key数组,修改内容 value数组>
     * @param collName       集合名
     * @param type           修改操作类型  1:修改第一条数据  0：修改所有匹配得数据
     */
    public static void updateMulti(String accordingKey, Object accordingValue, Map<String, Object> map,
                                   String collName, Integer type) {
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件key不能为null");
        }
        Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Query query = Query.query(criteria);
        Update update = new Update();

        for (String key : map.keySet()) {
            update.set(key, map.get(key));
        }
        if (type == 1) {
            mongodbUtil.mongoTemplate.updateFirst(query, update, collName);
        } else {
            mongodbUtil.mongoTemplate.updateMulti(query, update, collName);
        }
    }


    /**
     * 对某字段做sum求和
     *
     * @param clazz         数据实体类
     * @param map           Map<查询条件key,查询条件value> map
     * @param collName      集合名称
     * @param sumField      求和字段
     * @param rangeCriteria 范围条件
     * @return Criteria rangeCriteria = Criteria.where(字段).gt(起始范围).lt(结束范围)
     */
    public static Object findSum(Class<?> clazz, Map<String, Object> map, String collName, String sumField, Criteria rangeCriteria) {
        for (String key : map.keySet()) {
            Assert.notNull(key, "查询条件key不能为null");
        }
        Criteria criteria = null;
        MatchOperation match = null;
        for (String key : map.keySet()) {
            criteria = Criteria.where(key).is(map.get(key));
        }
        if (criteria != null) {
            match = Aggregation.match(criteria);
        }
        GroupOperation count = Aggregation.group().sum(sumField).as(sumField);
        return mongodbUtil.mongoTemplate.aggregate(Aggregation.newAggregation(match, count), collName, clazz).getMappedResults();
    }


    /**
     * 分页查询
     *
     * @param clazz     数据实体类
     * @param collName  集合名称
     * @param map       Map<"查询条件key"，查询条件值> map 若 keys/values 为null,则查询集合中所有数据
     * @param pageNo    当前页
     * @param pageSize  当前页数据条数
     * @param direction Direction.Desc/ASC 排序方式
     * @param sortField 排序字段
     * @return
     */
    public static PageModel findSortPageCondition(Class<?> clazz, String collName, Map<String, Object> map,
                                                  int pageNo, int pageSize, Sort.Direction direction, String sortField) {

        Criteria criteria = getCriteria(new Criteria(), map);

        long count;

        if (criteria == null) {
            count = mongodbUtil.mongoTemplate.count(new Query(), clazz, collName);
        } else {
            count = mongodbUtil.mongoTemplate.count(new Query(criteria), clazz, collName);
        }
        int pages = (int) Math.ceil((double) count / (double) pageSize);
        if (pageNo <= 0 || pageNo > pages) {
            pageNo = 1;
        }
        int skip = pageSize * (pageNo - 1);
        Query query = new Query().skip(skip).limit(pageSize);
        query.with(Sort.by(direction, sortField));
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        List<?> list = mongodbUtil.mongoTemplate.find(query, clazz, collName);
        PageModel pageModel = new PageModel();
        pageModel.setPageNo(pageNo);
        pageModel.setPageSize(pageSize);
        pageModel.setTotal(count);
        pageModel.setPages(pages);
        pageModel.setList(list);
        return pageModel;
    }

    public static PageModel findPageCondition(Class<?> clazz, String collName, Map<String, Object> map,
                                                  int pageNo, int pageSize) {

        Criteria criteria = getCriteria(new Criteria(), map);

        long count;

        if (criteria == null) {
            count = mongodbUtil.mongoTemplate.count(new Query(), clazz, collName);
        } else {
            count = mongodbUtil.mongoTemplate.count(new Query(criteria), clazz, collName);
        }
        int pages = (int) Math.ceil((double) count / (double) pageSize);
        if (pageNo <= 0 || pageNo > pages) {
            pageNo = 1;
        }
        int skip = pageSize * (pageNo - 1);
        Query query = new Query().skip(skip).limit(pageSize);
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        List<?> list = mongodbUtil.mongoTemplate.find(query, clazz, collName);
        PageModel pageModel = new PageModel();
        pageModel.setPageNo(pageNo);
        pageModel.setPageSize(pageSize);
        pageModel.setTotal(count);
        pageModel.setPages(pages);
        pageModel.setList(list);
        return pageModel;
    }

    private static void saveData(String collName, Object entiy) {
        if (StringUtils.isEmpty(collName)) {
            mongodbUtil.mongoTemplate.save(entiy);
        } else {
            mongodbUtil.mongoTemplate.save(entiy, collName);
        }
    }


    private static Criteria getCriteria(Criteria criteria, Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        int i = 0;
        for (String key : map.keySet()) {
            if (i == 0) {
                criteria = Criteria.where(key).is(map.get(key));
                i++;
            } else {
                criteria.and(key).is(map.get(key));
            }
        }
        return criteria;
    }

}
