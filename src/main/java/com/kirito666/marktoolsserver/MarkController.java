package com.kirito666.marktoolsserver;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MarkController {

    @Resource
    MarkService markService;

    @GetMapping("/dataset")
    public List<Dataset> getAllDataset(){
        return markService.getAllDataset();
    }

    @GetMapping("/text")
    public TextDataDto getTextDataPage(@RequestParam String datasetName, @RequestParam int pageId,
                                       @RequestParam int size,
                                       @RequestParam(defaultValue = "false") boolean onlyNoMark) {
        return markService.getTextDataPage(datasetName, pageId, size, onlyNoMark);
    }

    @GetMapping("/text/random")
    public TextDataDto getRandomTextDataPage(@RequestParam String datasetName, @RequestParam int pageId,
                                       @RequestParam int size,
                                       @RequestParam(defaultValue = "false") boolean onlyNoMark) {
        return markService.getRandomTextDataPage(datasetName, pageId, size, onlyNoMark);
    }

    @PostMapping("/mark")
    public String markText(@RequestParam String datasetName, @RequestParam int labelId, @RequestParam String emotion) {
        return markService.markText(datasetName, labelId, emotion);
    }
}
