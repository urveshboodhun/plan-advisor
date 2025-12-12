package com.telco.planadvisor.api;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PlanComparisonRequest {

    @NotEmpty
    private List<String> planTexts; // raw text pasted from carrier sites

    @NotNull
    private Integer budget; // monthly budget in CAD

    @NotNull
    private Integer dataGb; // desired data in GB

    private boolean needsRoaming;

    public List<String> getPlanTexts() {
        return planTexts;
    }

    public void setPlanTexts(List<String> planTexts) {
        this.planTexts = planTexts;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public Integer getDataGb() {
        return dataGb;
    }

    public void setDataGb(Integer dataGb) {
        this.dataGb = dataGb;
    }

    public boolean isNeedsRoaming() {
        return needsRoaming;
    }

    public void setNeedsRoaming(boolean needsRoaming) {
        this.needsRoaming = needsRoaming;
    }
}
