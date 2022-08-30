package com.mini.timecapsule.utils;

import org.springframework.stereotype.Component;

@Component
public abstract class EndResults {

    public static final EndResult UPDATED = EndResult.of(200010, "Updated");
    public static final EndResult DELETED = EndResult.of(200020, "Deleted");
    public static final EndResult SUCCESS = EndResult.of(200000, "Success");
    public static final EndResult CREATED = EndResult.of(201000, "Created");
    public static final EndResult BAD_REQUEST = EndResult.of(400000, "Bad Request");
    public static final EndResult REQUEST_FAILED = EndResult.of(400010, "Request Failed");
    public static final EndResult VERIFICATION_FAILED = EndResult.of(400020, "Verification Failed");
    public static final EndResult VALID_USER_NOT_FOUND = EndResult.of(400030, "Valid User Not Found");
    public static final EndResult REQUIRED_USER_NOT_FOUND = EndResult.of(400040, "Required User Not Found");
    public static final EndResult REQUIRED_DATA_NOT_FOUND = EndResult.of(400050, "Required Data Not Found");
    public static final EndResult PAYLOAD_CAST_ERROR = EndResult.of(500030, "Payload Cast Error");


}
