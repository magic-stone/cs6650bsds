package KafkaConsumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public class ConsumerThreadHandler implements Runnable {

    private List<ConsumerRecord<Long, String>> buffer;
    ConsumerRecord<Long, String> record;

    public ConsumerThreadHandler(List<ConsumerRecord<Long, String>>buffer , ConsumerRecord<Long, String> record) {
        this.buffer = buffer;
        this.record = record;
    }

    public void run() {
       buffer.add(record);
    }
}
