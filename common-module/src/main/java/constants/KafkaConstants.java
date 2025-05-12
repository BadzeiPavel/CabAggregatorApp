package constants;

// TODO: change localhost on service names in FEIGN-CLIENTS

public class KafkaConstants {

    public static final String RIDE_COMPLETED_EVENT = "ride-completed-event";
    public static final String RIDE_COMPLETED_RECOVERY_EVENT = "ride-completed-recovery-event";
    public static final String RIDE_PAYMENT_ON_COMPLETE_EVENT = "ride-payment-on-complete-event";
    public static final String RIDE_PAYMENT_ON_COMPLETE_RECOVERY_EVENT = "ride-payment-on-complete-recovery-event";

    public static final String DRIVER_SERVICE_GROUP_ID = "driver-service-group";
    public static final String PAYMENT_SERVICE_GROUP_ID = "payment-service-group";

    public static final String CONSUMER_BOOTSTRAP_SERVERS = "kafka:29092";
    public static final String AUTO_OFFSET_RESET = "earliest";

    public static final String PRODUCER_BOOTSTRAP_SERVERS = "kafka:29092";

    private KafkaConstants() {}
}