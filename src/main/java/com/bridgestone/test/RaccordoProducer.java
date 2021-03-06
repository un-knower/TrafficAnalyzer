package com.bridgestone.test;

import com.bridgestone.kafka.KafkaTopicCreator;
import com.bridgestone.properties.ApplicationProperties;
import com.bridgestone.utils.random.Rngs;
import com.bridgestone.utils.random.Rvgs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

import static jodd.util.ThreadUtil.sleep;

/**
 * Created by balmung on 01/09/17.
 */
public class RaccordoProducer {

    public static void main(String[] args) throws Exception {


        //Assign topicName to string variable
        double x, y;
        double firstx = 12.3;
        double firsty = 41.75;
        /*x = 52.12;
        y = 41.34;*/
        x = firstx; y = firsty;
        ApplicationProperties.loadProperties();
        String address = ApplicationProperties.getKafkaAddress();//"35.158.214.67";
        String topicName = Double.toString(x) + Double.toString(y); //topic name: coordinates of the master section
        KafkaTopicCreator.createTopic(address, topicName);

        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        //props.put("bootstrap.servers", "10.200.176.240:9092");

        //Set acknowledgements for producer requests.
       // props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
       // props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, address + ":9092");//"54.93.238.46:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 1);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        int count = 0;
        for(;;) {
            x = firstx; y = firsty;
            topicName = Double.toString(x) + Double.toString(y);
            //double time = System.currentTimeMillis();
            for (int j = 0; j < 80; j++) {
                if (j == 20) {
                    y = y + 0.1;
                    topicName = Double.toString(x) + Double.toString(y);
                    KafkaTopicCreator.createTopic(address, topicName);
                }
                if (j == 40) {
                    x = x + 0.1;
                    topicName = Double.toString(x) + Double.toString(y);
                    KafkaTopicCreator.createTopic(address, topicName);
                }
                if (j == 60) {
                    y = y - 0.1;
                    topicName = Double.toString(x) + Double.toString(y);
                    KafkaTopicCreator.createTopic(address, topicName);
                }
                String data = "[";

                data = data + jsonFormat(7680 + j%10, 51, 52.12 + 50 + 1, 41.34 + 50 + 1, (50 + count) % 600);
                data = data + "," + jsonFormat(0, 51, 132.12 + 50 + 1, 43.34 + 50 + 1, (50 + count) %100);
                data = data + "," + jsonFormat(0, 51, 0, 1, (50 + count)%800);
                data = data + "," + jsonFormat(0, 1, 1, 1, (5640 + count)%600);
                /*data = data + "," + jsonFormat(0, 1, 0, 2, 363228);
                data = data + "," + jsonFormat(1, 1, 1, 2, 93438);
                data = data + "," + jsonFormat(0, 2, 1, 2, 9234);
                data = data + "," + jsonFormat(1, 2, 2, 2, 3348);
                data = data + "," + jsonFormat(2, 2, 2, 3, 73230);
                data = data + "," + jsonFormat(1, 2, 1, 3, 90430);
                data = data + "," + jsonFormat(1, 3, 2, 3, 9793);
                data = data + "," + jsonFormat(1, 3, 1, 2, 1144);
                data = data + "," + jsonFormat(0, 3, 1, 3, 8234);*/




           /* data = data + "," + jsonFormat(2, 4, 2, 3, 0);
            data = data + "," + jsonFormat(2, 2, 2, 1, 0);
            data = data + "," + jsonFormat(2, 1, 2, 0, 0);
            data = data + "," + jsonFormat(0, 1, 152.12 + 50 + 1, 41.34 + 50 + 1, 0);
            data = data + "," + jsonFormat(0, 1, 1, 1, 0);
            data = data + "," + jsonFormat(0, 1, 0, 2, 0);
            data = data + "," + jsonFormat(0, 4, 0, 5, 0);

            data = data + jsonFormat(2, 5, 32.12 + 50 + 1, 3.34 + 50 + 1, 0);
            data = data + "," + jsonFormat(2, 3, 2, 2, 0);


            data = data + "," + jsonFormat(0, 4, 1, 4, 0);
            data = data + "," + jsonFormat(1, 4, 2, 4, 0);
            data = data + "," + jsonFormat(0, 2, 0, 3, 0);*/
                //data = data + "," + jsonFormat(0, 3, 0, 4, 0);

            /*data = data + "," + jsonFormat(2, 2, 1, 2, 0);
            data = data + "," + jsonFormat(1, 2, 0, 2, 0);*/




            /*data = data + "," + jsonFormat(1, 1, 1, 2, 1);
            data = data + "," + jsonFormat(1, 1, 2, 1, 2);
            data = data + "," + jsonFormat(2, 1, 2, 2, 3);
            data = data + "," + jsonFormat(1, 2, 2, 2, 4);

            data = data + "," + jsonFormat(2, 2, 2, 3, 5);
            data = data + "," + jsonFormat(2, 2, 3, 2, 6);
            data = data + "," + jsonFormat(2, 3, 3, 3, 7);
            data = data + "," + jsonFormat(3, 2, 3, 3, 8);

            data = data + "," + jsonFormat(2, 1, 3, 1, 9);
            data = data + "," + jsonFormat(3, 1, 3, 2, 9);


            data = data + "," + jsonFormat(2, 3, 2, 4, 9);
            data = data + "," + jsonFormat(2, 4, 3, 4, 10);
            data = data + "," + jsonFormat(3, 3, 3, 4, 11);
*/

                data = data + "]";
                ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(topicName, data);
                producer.send(producerRecord);
                System.err.println(j + "   " + data);
                count ++;
            }
           // System.out.println("TEMPO:" + (System.currentTimeMillis() - time) + "\tpkt: 1000" + "\n\n\n\n\n");
            if(count == 1000){
                count = 0;
                sleep(200);
            }
        }
    }

    private static String jsonFormat(double x1, double y1, double x2, double y2, int speed){
        return "{\"x1\":" + Double.toString(x1) + ",\"y1\":" + Double.toString(y1)
                + ",\"x2\":" + Double.toString(x2)+ ",\"y2\":" + Double.toString(y2) +
                ",\"speed\":" + Integer.toString(speed) + "}";
    }
}
