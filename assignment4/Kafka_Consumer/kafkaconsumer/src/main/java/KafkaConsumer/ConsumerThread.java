package KafkaConsumer;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import static KafkaConsumer.ConsumerServer.insertIntoDb;

public class ConsumerThread implements Runnable{
    private final Consumer<Long, String> consumer;
    // Threadpool of consumers
    private ExecutorService executor;

    public ConsumerThread() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, IKafkaConstants.KAFKA_BROKERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, IKafkaConstants.GROUP_ID_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, IKafkaConstants.MAX_POLL_RECORDS);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, IKafkaConstants.OFFSET_RESET_EARLIER);

        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList(IKafkaConstants.TOPIC_NAME));
    }


    /**
     * Creates a { ThreadPoolExecutor} with a given number of threads to consume the messages
     * from the broker.
     *
     * @param numberOfThreads The number of threads will be used to consume the message
     */
    public void execute(int numberOfThreads) {

        // Initialize a ThreadPool with size = 5 and use the BlockingQueue with size =1000 to
        // hold submitted tasks.
        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

        final int minBatchSize = 500;
        List<ConsumerRecord<Long, String>> buffer = new ArrayList<>();
        int size = 0;
        while (true) {
            ConsumerRecords<Long, String> records = consumer.poll(100);
            for (final ConsumerRecord record : records) {
                executor.submit(new ConsumerThreadHandler(buffer,record));
            }
            if (buffer.size() >= minBatchSize || buffer.size() == size) {
                insertIntoDb(buffer);
                consumer.commitSync();
                buffer.clear();
                size = 0;
            } else {
                size = buffer.size();
            }
        }
    }

    @Override
    public void run() {
        ConsumerServer.roll();
    }

    public void shutdown() {
        if (consumer != null) {
            consumer.close();
        }
        if (executor != null) {
            executor.shutdown();
        }
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out
                        .println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
    }
}
