package com.github.olaplace.kafka.tutorial1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import io.confluent.kafka.serializers.KafkaJsonSerializerConfig;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializerConfig;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;

import java.util.Properties;

public class ProducerJsonDemo {

    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallback.class);
        String bootstrapServers = "127.0.0.1:9092";

        //1: Create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // To produce a JSON record
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSerializer.class.getName());
        // To produce a JSON / Schema record
        //properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSchemaSerializer.class.getName());

        // ONLY when using Schema Registry with JSON Schemas
        properties.setProperty(KafkaJsonSchemaSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://127.0.0.1:8081");
        properties.setProperty(KafkaJsonSchemaSerializerConfig.AUTO_REGISTER_SCHEMAS, "false");


        // Set topic name and key
        String topic = "test-schema";
        String key = "mykey";
        // Create a Json Item with 2 fields
        //Item item = new Item(2000, "Tickle Me Elmo");
        // Create a Json Item with 3 fields
        Item item = new Item(300, "Tickle Me Elmo", 333);
        // Create a Json Item with 4 fields
        //Item item = new Item(150, "Tickle Me Elmo", 150, "description of item");


        //1: Create a ProducerRecord
        ProducerRecord<String, Item> record;
        record = new ProducerRecord<String, Item>(topic, key, item);

        //2: Create the Producer
        KafkaProducer<String, Item> producer = new KafkaProducer<String, Item>(properties);

        //3: Send Data asynchronously
        producer.send(record, new Callback() {
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                // Executes every time a record is successfully sent or an exception is thrown
                if (e == null) {
                    // The record was successfully sent
                    logger.info("Received new metadata: \n" +
                            "Topic: " + recordMetadata.topic() + "\n" +
                            "Partition: " + recordMetadata.partition() + "\n" +
                            "Offset: " + recordMetadata.offset() + "\n" +
                            "Timestamp: " + recordMetadata.timestamp());
                } else {
                    logger.error("Error while producing a JSON message: ", e);
                }
            }
        }); //Asynchronous

        // Flush Data
        producer.flush();
        // Flush Data and close producer
        producer.close();
    }

    public static class Item {
        @JsonProperty
        public Integer sku;

        @JsonProperty
        public String item_name;

        @JsonProperty
        public Integer quantity;
/*
        @JsonProperty
        public String description;
*/
        public Item() {}
/*
        public Item(Integer sku, String itemName) {
            this.sku=  sku;
            this.item_name = itemName;
        }
*/

        public Item(Integer sku, String itemName, Integer quantity) {
            this.sku=  sku;
            this.item_name = itemName;
            this.quantity = quantity;
        }
/*
        public Item(Integer sku, String itemName, Integer quantity, String description) {
            this.sku=  sku;
            this.item_name = itemName;
            this.quantity = quantity;
            this.description = description;
        }
*/

    }
}
