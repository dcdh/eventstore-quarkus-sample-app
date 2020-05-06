package com.damdamdeo.todo.publicfrontend.interfaces;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@ApplicationPath("/")
@OpenAPIDefinition(
        info = @Info(title = "Todo Public Frontend API", version = "1.0")
)
public class TodoPublicFrontendApplication extends Application {
}
