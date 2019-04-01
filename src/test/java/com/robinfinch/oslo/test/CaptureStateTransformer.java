package com.robinfinch.oslo.test;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.*;

public class CaptureStateTransformer extends ResponseDefinitionTransformer {

    @Override
    public String getName() {
        return "CaptureStateTransformer";
    }

    @Override
    public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, FileSource files, Parameters parameters) {

        String state = request.queryParameter("state").firstValue();

        String redirectLocation = responseDefinition.getHeaders().getHeader("Location").firstValue()
                .replace("${state}", state);

        return ResponseDefinition.redirectTo(redirectLocation);
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }
}