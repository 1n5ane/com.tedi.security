package com.tedi.security.configuration.properties

import com.tedi.security.configuration.SslConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class ServerProperties {

    @Autowired
    SslConfiguration sslConfiguration

    @Value('${server.port}')
    int port

    @Value('${server.address}')
    String address

    String getServerUrl() {
        def scheme = "https"
        if(!sslConfiguration.enabled)
            scheme = "http"
        return scheme + "://" + address + ":" + port
    }
}
