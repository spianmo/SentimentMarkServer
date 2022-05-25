package com.kirito666.marktoolsserver;

import com.kirito666.marktoolsserver.common.PageModel;
import com.kirito666.marktoolsserver.util.MongoDBUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Finger
 * @Description TODO  测试MongoDB工具类
 * @date 2020/7/24 15:55
 */
@SpringBootTest
public class TestMongo {

    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    public void findSortPageCondition() {
        Map<String, Object> map = new HashMap<>();
        PageModel page = MongoDBUtil.findSortPageCondition(TextData.class, "nlpcc2014", map, 1, 6,
                Sort.Direction.DESC, "labelId");
        System.out.println(page);
    }

    @Test
    public void addTextData() {
        TextData textData = new TextData(1, "测试文本", false, "sadness");
        mongoTemplate.save(textData, "nlpcc2014");
    }

    @Test
    public void importTextData() {
        try {
            String encoding = "utf-8";
            File file = new File("D:\\PycharmProjects\\MultiClassify_LSTM_ForChinese\\data\\dataset\\summary\\weibo82813_8_classify.txt");
            if (file.exists() && file.isFile()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader br = new BufferedReader(read);
                String lineText;
                int count = 0;
                TextData textData = new TextData();
                while ((lineText = br.readLine()) != null) {
                    if (!lineText.startsWith("#")) {
                        String[] strings = lineText.split(":", 0);
                        textData.text = strings[1];
                        textData.emotion = strings[0];
                        textData.isMarked = false;
                        textData.labelId = ++count;
                        System.out.println(count);
                        mongoTemplate.save(textData, "nlpcc2014");
                    }
                }
                mongoTemplate.save(new Dataset("nlpcc2014", new Dataset.Mood[]{
                        new Dataset.Mood("生气", "😡", "red lighten-4", "anger"),
                        new Dataset.Mood("厌恶", "👿", "deep-purple lighten-4", "disgust"),
                        new Dataset.Mood("快乐", "😀", "amber lighten-4", "happiness"),
                        new Dataset.Mood("喜爱", "🥰", "deep-orange lighten-4", "like"),
                        new Dataset.Mood("悲伤", "😭", "blue lighten-4", "sadness"),
                        new Dataset.Mood("恐惧", "😨", "light-green lighten-4", "fear"),
                        new Dataset.Mood("惊讶", "😲", "indigo lighten-4", "surprise"),
                        new Dataset.Mood("None", "👾", "blue-grey lighten-4", "none")
                }, count), "dataset");
                br.close();
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void importDataset() {
        mongoTemplate.save(new Dataset("nlpcc2014", new Dataset.Mood[]{
                new Dataset.Mood("生气", "😡", "red lighten-4", "anger"),
                new Dataset.Mood("厌恶", "👿", "deep-purple lighten-4", "disgust"),
                new Dataset.Mood("快乐", "😀", "amber lighten-4", "happiness"),
                new Dataset.Mood("喜爱", "🥰", "deep-orange lighten-4", "like"),
                new Dataset.Mood("悲伤", "😭", "blue lighten-4", "sadness"),
                new Dataset.Mood("恐惧", "😨", "light-green lighten-4", "fear"),
                new Dataset.Mood("惊讶", "😲", "indigo lighten-4", "surprise"),
                new Dataset.Mood("中性", "👾", "blue-grey lighten-4", "none"),
                new Dataset.Mood("废弃", "\uD83D\uDDD1","brown lighten-4","trash")
        }, 83791), "dataset");
    }
}