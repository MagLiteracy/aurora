package com.paypal.aurora.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Range {
    def start
    def end

    Range(start, end) {
        this.start = start
        this.end = end
    }

    Range() {

    }


    @Override
    public String toString() {
        return "Range{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
