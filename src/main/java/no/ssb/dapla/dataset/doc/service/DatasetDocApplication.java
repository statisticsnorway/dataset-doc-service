package no.ssb.dapla.dataset.doc.service;

import ch.qos.logback.classic.util.ContextInitializer;
import io.helidon.config.Config;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jackson.JacksonSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerConfiguration;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.WebTracingConfig;
import io.helidon.webserver.accesslog.AccessLogSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.LogManager;

public class DatasetDocApplication {

    private static final Logger LOG;

    static {
        String logbackConfigurationFile = System.getenv("LOGBACK_CONFIGURATION_FILE");
        if (logbackConfigurationFile != null) {
            System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, logbackConfigurationFile);
        }
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        LOG = LoggerFactory.getLogger(DatasetDocApplication.class);
    }

    private final Map<Class<?>, Object> instanceByType = new ConcurrentHashMap<>();

    DatasetDocApplication(Config config) {
        put(Config.class, config);

        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())
                .build();
        MetricsSupport metrics = MetricsSupport.create();

        DatasetDocService datasetDocService = new DatasetDocService(config);

        ServerConfiguration.Builder serverConfig = ServerConfiguration.builder(config);
        config.get("server.port").asInt().ifPresent(serverConfig::port);
        config.get("server.host").asString().map(s -> {
            try {
                return InetAddress.getByName(s);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }).ifPresent(serverConfig::bindAddress);

        WebServer server = WebServer.builder(
                Routing.builder()
                        .register(AccessLogSupport.create(config.get("server.access-log")))
                        .register(WebTracingConfig.create(config.get("tracing")))
                        .register(health)
                        .register(metrics)
                        .register("/doc", datasetDocService)
                        .build()
        ).config(serverConfig).addMediaSupport(JacksonSupport.create()).build();
        put(WebServer.class, server);
    }

    /**
     * Application main entry point.
     *
     * @param args command line arguments.
     */
    public static void main(final String[] args) {
        DatasetDocApplication app = new DatasetDocApplication(Config.create());

        // Try to start the server. If successful, print some info and arrange to
        // print a message at shutdown. If unsuccessful, print the exception.
        app.get(WebServer.class).start()
                .thenAccept(ws -> {
                    System.out.println(
                            "WEB server is up! http://" + ws.configuration().bindAddress()+ ":" + ws.port() + "/concept");
                    ws.whenShutdown().thenRun(()
                            -> System.out.println("WEB server is DOWN. Good bye!"));
                })
                .exceptionally(t -> {
                    System.err.println("Startup failed: " + t.getMessage());
                    t.printStackTrace(System.err);
                    return null;
                });
    }

    public <T> T put(Class<T> clazz, T instance) {
        return (T) instanceByType.put(clazz, instance);
    }

    public <T> T get(Class<T> clazz) {
        return (T) instanceByType.get(clazz);
    }
}
