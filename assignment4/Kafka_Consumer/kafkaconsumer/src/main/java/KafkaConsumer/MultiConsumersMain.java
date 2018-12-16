package KafkaConsumer;


public class MultiConsumersMain {
    public static void main(String[] args) {

        // Start group of Consumers
        ConsumerGroup consumerGroup =
                new ConsumerGroup();

        consumerGroup.execute();

        try {
            Thread.sleep(100000);
        } catch (InterruptedException ie) {

        }
    }
}
