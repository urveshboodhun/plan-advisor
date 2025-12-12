package com.telco.planadvisor.api;

import lombok.Data;

@Data
public class ExtractedPlan {
    private String carrier;
    private String name;
    private String price;
    private String data;
    private String extras;
}



