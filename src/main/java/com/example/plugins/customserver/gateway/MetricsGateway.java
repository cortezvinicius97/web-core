package com.example.plugins.customserver.gateway;

import com.sun.net.httpserver.HttpServer;
import com.vcinsidedigital.webcore.server.Gateway;

import java.util.concurrent.atomic.AtomicLong;

public class MetricsGateway implements Gateway
{
    private final AtomicLong requestCount = new AtomicLong(0);
    private final long startTime = System.currentTimeMillis();

    @Override
    public String getName() {
        return "Metrics Gateway";
    }

    @Override
    public void initialize(HttpServer server) throws Exception {
        server.createContext("/metrics", exchange -> {
            long uptime = System.currentTimeMillis() - startTime;
            long requests = requestCount.get();

            String metrics = String.format(
                    "{\"uptime\": %d, \"requests\": %d, \"requestsPerSecond\": %.2f}",
                    uptime / 1000,
                    requests,
                    (requests * 1000.0) / uptime
            );

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, metrics.length());
            exchange.getResponseBody().write(metrics.getBytes());
            exchange.getResponseBody().close();

            requestCount.incrementAndGet();
        });
    }

    @Override
    public void onStart() {
        System.out.println("    └─ Metrics endpoint available at /metrics");
    }
}
