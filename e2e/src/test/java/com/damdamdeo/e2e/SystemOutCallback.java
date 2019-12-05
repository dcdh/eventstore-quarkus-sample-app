package com.damdamdeo.e2e;

import io.fabric8.kubernetes.client.Callback;

public final class SystemOutCallback implements Callback<byte[]> {

    private String data;

    @Override
    public void call(byte[] data) {
        this.data = new String(data)
                .replace("\n", "")
                .replace("\r", "");
    }

    public String getData() {
        return data;
    }

}