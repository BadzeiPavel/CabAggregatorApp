package constants;

public class KafkaConstants {
    public static final String RIDE_COMPLETED_EVENT = "ride-completed-event";
    public static final String RIDE_COMPLETED_RECOVERY_EVENT = "ride-completed-recovery-event";

    public static final String DRIVER_SERVICE_GROUP_ID = "driver-service-group";

    public static final String CONSUMER_BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String AUTO_OFFSET_RESET = "earliest";

    public static final String PRODUCER_BOOTSTRAP_SERVERS = "localhost:29092";

    private KafkaConstants() {}
}