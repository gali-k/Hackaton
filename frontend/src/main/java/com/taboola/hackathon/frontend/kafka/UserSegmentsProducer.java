package com.taboola.hackathon.frontend.kafka;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class UserSegmentsProducer {
    public static String KAFKA_BROKERS = "kafka005.taboolasyndication.com:6667";
    public static String CLIENT_ID="hackaton";
    public static String USER_SEGMENTS_KAFKA_TOPIC="user_segments";

    private static Producer producer;


    public UserSegmentsProducer(){
        producer = createProducer();
    }

    private Producer<String, UserSegmentsData> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UserSegmentsSerializer.class.getName());
        return new KafkaProducer<>(props);
    }


    public static String getHashedUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        try {
            return Hash.sha256(userId);
        } catch (Exception e) {
            return null;
        }
    }

    public void sendToKafka(String deviceId){
        String hashedUserId = getHashedUserId(deviceId);
        UserSegmentsData data = new UserSegmentsData();
        data.setTaboolaUserId(deviceId);
        List<Long> ids_list = new ArrayList<>();
        ids_list.add(1603296L);
        ids_list.add(1603298L);
        data.setUddIdsList(ids_list);
        data.setDeletedUddIdsList(null);
        producer.send(new ProducerRecord<>(USER_SEGMENTS_KAFKA_TOPIC, hashedUserId, data));
    }

    public static void main(String[] args) {
        UserSegmentsProducer userSegmentsProducer = new UserSegmentsProducer();
        String deviceId = "12345667";//some device ID
        userSegmentsProducer.sendToKafka(deviceId);
        System.out.println("message sent");
    }



}
