package BsmchFlights.Utils;

public class QueryCondition {

    private String key;
    private String conditionRelation;
    private String[] parameters;

    public QueryCondition(String key, String conditionRelation, String[] parameters) {
        this.key = key;
        this.conditionRelation = conditionRelation;
        this.parameters = parameters;
    }

    public QueryCondition(String[] parameters) {
        this.parameters = parameters;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getConditionRelation() {
        return conditionRelation;
    }

    public void setConditionRelation(String conditionRelation) {
        this.conditionRelation = conditionRelation;
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }
}
