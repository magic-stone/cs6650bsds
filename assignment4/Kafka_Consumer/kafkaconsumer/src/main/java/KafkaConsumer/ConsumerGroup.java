package KafkaConsumer;

import java.util.ArrayList;
import java.util.List;

public class ConsumerGroup {
    private final int numberOfConsumers = 3;
    private List<ConsumerThread> consumers;

    public ConsumerGroup() {
        consumers = new ArrayList<>();
        for (int i = 0; i < this.numberOfConsumers; i++) {
            ConsumerThread thread =
                    new ConsumerThread();
            consumers.add(thread);
        }
    }

    public void execute() {
        for (ConsumerThread thread : consumers) {
            Thread t = new Thread(thread);
            t.start();
        }
    }
}
