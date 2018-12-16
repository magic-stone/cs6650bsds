package KafkaConsumer;


import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import KafkaConsumer.DatabaseComponent.SharedDao;
public class ConsumerServer {

    public static void roll() {
        int numberOfThread = 5;

        ConsumerThread consumers = new ConsumerThread();
        consumers.execute(numberOfThread);

        try {
            Thread.sleep(100000);
        } catch (InterruptedException ie) {

        }
        consumers.shutdown();
    }

    public static void insertIntoDb(List<ConsumerRecord<Long, String>> buffer) {
        buffer.forEach(record -> {
            String message = record.value();
            String[] infos = message.split(",");
            if (infos.length == 4) {
                System.out.println("Message received: " + message + " with offset:" + record.offset());
                int userId = Integer.parseInt(infos[0]);
                int day = Integer.parseInt(infos[1]);
                int timeInterval = Integer.parseInt(infos[2]);
                int stepCount = Integer.parseInt(infos[3]);
                insert(userId, day, timeInterval, stepCount);
            }
        });
    }

    public static void insert(int userId, int day, int timeInterval, int stepCount) {
        try {
            SharedDao sharedDao = SharedDao.getInstance();
            sharedDao.create(userId,day,timeInterval,stepCount);
        } catch (Exception e) {
            System.out.println("Insert processing exception");
            e.printStackTrace();
        }
    }
}
