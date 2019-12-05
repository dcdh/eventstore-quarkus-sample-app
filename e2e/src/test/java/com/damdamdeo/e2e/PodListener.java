package com.damdamdeo.e2e;

import io.fabric8.kubernetes.client.dsl.ExecListener;
import okhttp3.Response;

public final class PodListener implements ExecListener {

    @Override
    public void onOpen(final Response response) {
        System.out.println("shell open");
    }

    @Override
    public void onFailure(final Throwable t, Response response) {
        System.err.println("shell barfed");
    }

    @Override
    public void onClose(final int code, final String reason) {
        System.out.println("The shell will now close.");
    }

}