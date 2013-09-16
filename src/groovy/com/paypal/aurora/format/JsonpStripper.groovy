package com.paypal.aurora.format

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Parses JSON out of a JSONP string, by removing the function call that wraps the JSON.
 */
class JsonpStripper {

    /**
     * Optional whitespace, some word characters for the JavaScript function name, more optional whitespace, an open
     * parenthesis, more optional whitespace, a bunch of random text in the middle that we will capture and hope it is
     * a JSON object, more optional whitespace, a close parenthesis, more optional whitespace, an optional semicolon,
     * and more optional whitespace.
     *
     * The DOTALL switch allows newline characters to be matched by a dot, so we can capture a multi-line string.
     */
    private static final Pattern jsonpPattern = Pattern.compile(/^\s*\w+\s*[(]\s*(.*)\s*[)]\s*?;?\s*?$/, Pattern.DOTALL)

    String text

    JsonpStripper(String text) {
        this.text = text
    }

    /**
     * @return String the JSON part of the JSONP string, or the whitespace-trimmed JSON string if the string does not
     *          match the JSONP pattern
     */
    String stripPadding() {
        Matcher matcher = jsonpPattern.matcher(text)
        if (matcher.matches()) {
            return matcher.group(1).trim()
        }
        return text?.trim()
    }
}
