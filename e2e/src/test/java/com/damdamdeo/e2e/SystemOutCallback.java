package com.damdamdeo.e2e;

import io.fabric8.kubernetes.client.Callback;

import java.util.Optional;

public final class SystemOutCallback implements Callback<byte[]> {

    private String data;

    @Override
    public void call(byte[] data) {
        Optional.of(new String(data))
                .map(d -> d.replace("\n", ""))
                .map(d -> d.replace("\r", ""))
                .ifPresent(d ->  {
                    System.out.println(d);
                    this.data = d;
                });
    }

    public String getData() {
        return data;
    }

}