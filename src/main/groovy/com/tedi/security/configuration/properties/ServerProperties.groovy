package com.tedi.security.configuration.properties
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class ServerProperties {

    @Value('${server.port}')
    int port

    @Value('${server.address}')
    String address

    String getServerUrl() {
        return "http://" + address + ":" + port
    }
}
