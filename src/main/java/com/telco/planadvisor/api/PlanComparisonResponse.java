package com.telco.planadvisor.api;

import java.util.List;

public class PlanComparisonResponse {

    public static class PlanSummary {
        private String carrier;
        private String name;
        private String price;
        private String data;
        private String notes;

        public String getCarrier() {
            return carrier;
        }

        public void setCarrier(String carrier) {
            this.carrier = carrier;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    private List<PlanSummary> parsedPlans;
    private String recommendedPlanName;
    private String recommendedCarrier;
    private String reasoning;

    public List<PlanSummary> getParsedPlans() {
        return parsedPlans;
    }

    public void setParsedPlans(List<PlanSummary> parsedPlans) {
        this.parsedPlans = parsedPlans;
    }

    public String getRecommendedPlanName() {
        return recommendedPlanName;
    }

    public void setRecommendedPlanName(String recommendedPlanName) {
        this.recommendedPlanName = recommendedPlanName;
    }

    public String getRecommendedCarrier() {
        return recommendedCarrier;
    }

    public void setRecommendedCarrier(String recommendedCarrier) {
        this.recommendedCarrier = recommendedCarrier;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
}