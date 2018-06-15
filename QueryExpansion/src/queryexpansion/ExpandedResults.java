/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryexpansion;

import java.util.List;

/**
 *
 * @author srabonti 
 */
class ExpandedResults {
    private int resultCount;
    private List<SearchEntity> results;
    private long executionTime;
    private String initialQuery;
    private String expandedQuery;
    
    public int getResultCount() {
            return resultCount;
    }
    public void setResultCount(int resultCount) {
            this.resultCount = resultCount;
    }
    public List<SearchEntity> getResults() {
            return results;
    }
    public void setResults(List<SearchEntity> results) {
            this.results = results;
    }
    public long getExecutionTime() {
            return executionTime;
    }
    public void setExecutionTime(long executionTime) {
            this.executionTime = executionTime;
    }
    public String getInitialQuery() {
            return initialQuery;
    }
    public void setInitialQuery(String initialQuery) {
            this.initialQuery = initialQuery;
    }
    public String getExpandedQuery() {
            return expandedQuery;
    }
    public void setExpandedQuery(String expandedQuery) {
            this.expandedQuery = expandedQuery;
    }
}
