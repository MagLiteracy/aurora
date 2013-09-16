package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Job {
    String comments
    String completionDate
    String creationDate
    String jobId
    String requestMethod
    String requestBody
    String requestURI
    String status
    String tenantName
    String taskType

    Job() {
    }

    Job(def data) {
        this.comments = data.comments
        this.completionDate = data.completionDate
        this.creationDate = data.creationDate
        this.jobId = data.jobId
        this.requestMethod = data.requestMethod
        this.requestBody = data.requestBody
        this.requestURI = data.requestURI
        this.status = data.status
        this.tenantName = data.tenantName
        this.taskType = data.taskType
    }


    @Override
    public String toString() {
        return "Job{" +
                "comments='" + comments + '\'' +
                ", completionDate='" + completionDate + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", jobId='" + jobId + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", requestURI='" + requestURI + '\'' +
                ", status='" + status + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", taskType='" + taskType + '\'' +
                '}';
    }
}
