******************************** D E M O ***********************************

*********  SCHEMA VALIDATION  **********
// Create the test-schema topic
kafka-topics --bootstrap-server localhost:9092 --create --partitions 3 --replication-factor 1 --topic test-schema
// OPTION: Create a topic with Schema Validation (requires a Schema to be attached to the Topic)
// ... or do it through Control Center
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic test-schema --config confluent.value.schema.validation=false


// Send a JSON message from IntelliJ (KafkaJsonSerializer serializer)
==> OK

// Change the property confluent.value.schema.validation = true (in CCC)

// Re-send a JSON message from IntelliJ
==> KO

// Now test to send a JSON / Schema message from IntelliJ (KafkaJsonSchemaSerializer serializer)
// ... with AUTO_REGISTER_SCHEMAS = false
==> KO

// Now test to send a JSON / Schema message from IntelliJ (KafkaJsonSchemaSerializer serializer)
// ... with AUTO_REGISTER_SCHEMAS = true
==> OK
==> Show the schema and its content


*********  SCHEMA REGISTRY  **********
// Test to send another message with 3 properties (KafkaJsonSchemaSerializer serializer)
// ... with AUTO_REGISTER_SCHEMAS = false
==> OK (Schema there and valid)

// Test to send another message with 4 properties (KafkaJsonSchemaSerializer serializer)
// ... with AUTO_REGISTER_SCHEMAS = false
==> KO (Schema not valid)

// Test to send another message with 4 properties (KafkaJsonSchemaSerializer serializer)
// ... with AUTO_REGISTER_SCHEMAS = true
==> OK (new Schema version valid)

// Test to send another message with 2 properties (KafkaJsonSchemaSerializer serializer)
// ... with AUTO_REGISTER_SCHEMAS = true
==> KO (Schema not compatible with old versions due to compatibility)



*********  SCHEMA MANAGEMENT  (for information) ********
// Get Schemas with Curl / REST API (or with Insomnia)
curl --silent -X GET http://localhost:8081/subjects

curl --silent -X GET http://localhost:8081/subjects/test_quota-value/versions/latest | jq .
