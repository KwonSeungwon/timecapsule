package com.mini.timecapsule.utils;

public abstract class EndResults {

    public static final EndResult UPDATED = EndResult.of(200010, "Updated");
    public static final EndResult DELETED = EndResult.of(200020, "Deleted");
    public static final EndResult SUCCESS = EndResult.of(200000, "Success");
    public static final EndResult CREATED = EndResult.of(201000, "Created");

}
