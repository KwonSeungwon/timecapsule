package com.mini.timecapsule.utils;

import org.springframework.stereotype.Component;

@Component
public interface PayloadFactory {

    public Payload create(EndResult result);

    public void addReservedInfo(Payload payload);

    public Payload toPayload(Object object);
}
