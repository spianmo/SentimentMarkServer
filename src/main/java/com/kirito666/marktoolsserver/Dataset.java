package com.kirito666.marktoolsserver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Finger
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dataset {
    private String name;
    private Mood[] classification;
    private long count;

    @AllArgsConstructor
    @Data
    static class Mood{
        //{label: "生气", icon: "😡", color: "red lighten-4", labelEn: "anger"}
        String label;
        String icon;
        String color;
        String labelEn;
    }
}
