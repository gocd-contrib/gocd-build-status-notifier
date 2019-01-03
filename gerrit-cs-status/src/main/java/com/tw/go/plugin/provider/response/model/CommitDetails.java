package com.tw.go.plugin.provider.response.model;

public class CommitDetails {
    private String id;
    private String project;
    private String branch;
    private String change_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getChangeId() {
        return change_id;
    }

    public void setChangeId(String change_id) {
        this.change_id = change_id;
    }
}
