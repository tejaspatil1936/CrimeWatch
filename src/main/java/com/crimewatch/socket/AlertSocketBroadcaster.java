package com.crimewatch.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

@Component
public class AlertSocketBroadcaster {

    private static final Logger log = LoggerFactory.getLogger(AlertSocketBroadcaster.class);

    @Autowired private SocketServer socketServer;
    private final ObjectMapper mapper = new ObjectMapper();

    public void broadcastJson(Object payload) {
        try {
            String json = mapper.writeValueAsString(payload);
            for (PrintWriter client : socketServer.getClients()) {
                try {
                    client.println(json);
                } catch (Exception e) {
                    log.warn("Failed to write to a client; removing", e);
                    socketServer.getClients().remove(client);
                }
            }
            log.debug("Broadcasted to {} client(s)", socketServer.getClients().size());
        } catch (Exception e) {
            log.error("Broadcast serialisation failed", e);
        }
    }
}
