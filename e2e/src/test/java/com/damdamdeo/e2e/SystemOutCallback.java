package com.damdamdeo.e2e;

import io.fabric8.kubernetes.client.Callback;

import java.util.Optional;

public final class SystemOutCallback implements Callback<byte[]> {

    private String data;

    @Override
    public void call(byte[] data) {
        this.data = new String(data);
    }

    public String getData() {
        final Optional<String> optionalData = Optional.ofNullable(data)
                .map(d -> d.replace("\n", ""))
                .map(d -> d.replace("\r", ""));
        optionalData.ifPresent(System.out::println);
        return optionalData.orElse(null);
    }

}