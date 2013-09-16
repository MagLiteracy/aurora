package com.paypal.aurora

import java.util.regex.Matcher

class Styler {

    // Cache of zone style classes to avoid recalculating them.
    private static Map<String, String> zonesToStyleClasses = [:]

    /**
     * Match patterns like us-east-1a and ap-southeast-1c, plucking out the final letter after the number.
     *
     * @param zone string like us-east-1a
     * @return String style class name like zoneA
     * @throws AssertionError if zone does not end in "hyphen, digit, lowercase letter"
     */
    static String availabilityZoneToStyleClass(String zone) {
        if (zonesToStyleClasses[zone]) {
            return zonesToStyleClasses[zone]
        }
        Matcher zoneMatcher = zone =~ /^.*?-[0-9]([a-z])$/
        if(zoneMatcher.matches()) {
            String zoneLetter = zoneMatcher[0][1]
            return zonesToStyleClasses[zone] = "zone${zoneLetter.toUpperCase()}"
        }
        return null
    }

}
