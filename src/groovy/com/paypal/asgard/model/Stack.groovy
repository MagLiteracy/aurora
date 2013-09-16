package com.paypal.asgard.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Stack {
    String id
    String status
    String stack_status_reason
    String description
    String created
    String name
    String updated
    String timeout
    Boolean disable_rollback
    Map<String, String> parameters

    Stack() {
    }

    Stack(def stack) {
        this.id = stack.id
        this.status = stack.stack_status
        this.stack_status_reason = stack.stack_status_reason
        this.description = stack.description
        this.created = stack.creation_time
        this.name = stack.stack_name
        this.updated = stack.updated_time
        this.timeout = stack.timeout_mins
        this.disable_rollback = ("true" == stack.disable_rollback)
        this.parameters = stack.parameters
    }

    @Override
    public String toString() {
        return "Stack{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", stack_status_reason='" + stack_status_reason + '\'' +
                ", description='" + description + '\'' +
                ", created='" + created + '\'' +
                ", name='" + name + '\'' +
                ", updated='" + updated + '\'' +
                ", timeout='" + timeout + '\'' +
                ", disable_rollback=" + disable_rollback +
                ", parameters=" + parameters +
                '}';
    }
}
