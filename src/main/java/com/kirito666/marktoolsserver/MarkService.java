package com.kirito666.marktoolsserver;

import com.kirito666.marktoolsserver.common.PageModel;
import com.kirito666.marktoolsserver.util.MongoDBUtil;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class MarkService {

    @Resource
    private MongoTemplate mongoTemplate;

    public TextDataDto getTextDataPage(String datasetName, int pageId, int pageSize, boolean onlyNoMark) {
        Map<String, Object> map = new HashMap<>();
        if (onlyNoMark) {
            map.put("isMarked", false);
        }
        PageModel page = MongoDBUtil.findSortPageCondition(TextData.class, datasetName, map, pageId, pageSize,
                Sort.Direction.ASC, "labelId");
        List<TextData> textDataList = (List<TextData>) page.getList();
        return new TextDataDto(page.getPageNo(), page.getPageSize(), page.getTotal(), page.getPages(), new LinkedHashMap<Integer, TextData>() {
            {
                int count = 0;
                for (TextData textData : textDataList) {
                    put(count++, textData);
                }
            }
        });

    }

    public String markText(String datasetName, int labelId, String emotion) {
        Update update = new Update();
        update.set("emotion", emotion);
        update.set("isMarked", true);
        UpdateResult result = mongoTemplate.updateMulti(new Query(Criteria.where("labelId").is(labelId)), update, datasetName);
        return result.wasAcknowledged() ? "文本标注成功" : "文本标注失败";
    }

    public List<Dataset> getAllDataset() {
        return (List<Dataset>) MongoDBUtil.findAll(Dataset.class, "dataset");
    }

    public TextDataDto getRandomTextDataPage(String datasetName, int pageId, int pageSize, boolean onlyNoMark) {
        Map<String, Object> map = new HashMap<>();
        if (onlyNoMark) {
            map.put("isMarked", false);
        }
        PageModel page = MongoDBUtil.findPageCondition(TextData.class, datasetName, map, pageId, pageSize);
        List<TextData> textDataList = (List<TextData>) page.getList();
        return new TextDataDto(page.getPageNo(), page.getPageSize(), page.getTotal(), page.getPages(), new LinkedHashMap<Integer, TextData>() {
            {
                int count = 0;
                for (TextData textData : textDataList) {
                    put(count++, textData);
                }
            }
        });
    }
}
