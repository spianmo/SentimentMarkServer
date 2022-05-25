package com.kirito666.marktoolsserver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TextData {
    public Integer labelId;
    public String text;
    public Boolean isMarked;
    public String emotion;
}
