package com.crimewatch.util;

import java.util.UUID;

public final class IdGenerator {
    private IdGenerator() {}
    private static String shortUuid() { return UUID.randomUUID().toString().replace("-", "").substring(0, 10); }
    public static String reportId()      { return "rpt_" + shortUuid(); }
    public static String zoneId()        { return "zone_" + shortUuid(); }
    public static String userId()        { return "usr_" + shortUuid(); }
    public static String assignmentId()  { return "asn_" + shortUuid(); }
    public static String escalationId()  { return "esc_" + shortUuid(); }
    public static String auditId()       { return "log_" + shortUuid(); }
    public static String alertId()       { return "alt_" + shortUuid(); }
}
