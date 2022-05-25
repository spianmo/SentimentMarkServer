package com.kirito666.marktoolsserver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TextDataDto {

    public Integer pageNo = 1;

    public Integer pageSize = 10;

    public Long total;

    public Integer pages;

    public Map<Integer, TextData> dataMap;
}
