package com.civic.platform.config;

import com.civic.platform.api.UserResources;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class JerseyApplication extends ResourceConfig {
    public JerseyApplication() {
        register(UserResources.class);
        packages("com.civic.platform.api");
        property("jersey.config.server.response.setStatusOverSendError", true);
    }
}
