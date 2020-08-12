*******************************************************
// MongoDB 2 Confluent 2 Elastic demo using Connectors
*******************************************************

// Prerequisite: have those solutions running
// 1) Confluent Platform 5.5 (on-prem)
// 2) MongoDB (on-prem or in Cloud): needs a replicaSet
// 3) Elastic (on-prem or in Cloud)

// Install MongoDB connector (available connectors at www.confluent.io/hub)
confluent-hub install mongodb/kafka-connect-mongodb:1.0.1

// Install Elastic connector (available connectors at www.confluent.io/hub)
confluent-hub install confluentinc/kafka-connect-elasticsearch:5.5.0

// Start Confluent (if not yet started)
confluent local start

// Start MongoDB (if not yet started)
mongod --replSet monreplica --dbpath /Users/olaplace/MongoDB/db

// Elastic is available from a bonsai.io instance

// Start mongo shell (in a new window)
mongo 

// Create "demo" DB
> use demo
> show tables

// In Confluent Control Center (C3): http://localhost:9021
// Create the MongoDB Connector in ksqlDB
CREATE SOURCE CONNECTOR `mongo-source-connector` WITH(
    "connector.class"='com.mongodb.kafka.connect.MongoSourceConnector',
    "connection.uri"='mongodb://localhost:27017',
    "database"='demo',
    "collection"='inventory',
    "tasks.max"='1',
    "topic.prefix"='',
    "topics"='inventory',
    "type.name"='_doc',
    "value.converter"='org.apache.kafka.connect.storage.StringConverter',
    "value.converter.schemas.enable"='false',
    "schema.ignore"='true',
    "key.ignore"='true');

// In Mongo shell: Insert documents in "inventory" Collection (demo DB)
db.inventory.insert ({ "SKU" : 1, "item_name":"Tickle Me Elmo", "quantity" : 10 })
db.inventory.insert ({ "SKU" : 2, "item_name":"Item name 2", "quantity" : 20 })
db.inventory.insert ({ "SKU" : 3, "item_name":"Item name 3", "quantity" : 30 })

// In Confluent Control Center (C3), in ksqlDB: http://localhost:9021
// ******** Filter appropriate MongoDB fields *********
// 1: Filter appropriate fields in Mongo Document 
CREATE STREAM MONGO_ELASTIC (documentKey STRUCT<_id STRUCT<"$oid" VARCHAR>>,
                             fullDocument STRUCT<SKU BIGINT, item_name VARCHAR, quantity BIGINT>) 
                             WITH 
                             (KAFKA_TOPIC='demo.inventory', VALUE_FORMAT='JSON'); 

// 2: Create a topic with those fields and rename "_id" field to "mongo_id" 
CREATE STREAM MONGO_ELASTIC_T  
   WITH (PARTITIONS=3, VALUE_FORMAT='JSON') 
   AS SELECT documentKey->_id->"$oid" AS mongo_id,
             fullDocument->SKU AS SKU, 
             fullDocument->item_name AS item_name,
             fullDocument->quantity AS quantity
   FROM MONGO_ELASTIC; 

// In Confluent Control Center (C3): http://localhost:9021
// Create the Elastic Connector in ksqlDB
// See also: https://www.confluent.io/blog/kafka-elasticsearch-connector-tutorial/
CREATE SINK CONNECTOR `elastic-sink-connector` WITH(
    "connector.class"='io.confluent.connect.elasticsearch.ElasticsearchSinkConnector',
    "connection.url"='https://iqdhdouvck:u9kokdgb8o@confluent-poc-3327101121.eu-west-1.bonsaisearch.net:443',
    "connection.username"='your_username',
    "connection.password"='your_password',
    "tasks.max"='1',
    "topics"='MONGO_ELASTIC_T',
    "type.name"='_doc',
    "value.converter"='org.apache.kafka.connect.json.JsonConverter',
    "value.converter.schemas.enable"='false',
    "schema.ignore"='true',
    "key.ignore"='true');


// In Mongo shell, continue to insert document(s) in "inventory" Collection (demo DB)
// The documents will automatically go from MongoDB to Confluent to Elastic
db.inventory.insert ({ "SKU" : 3000, "item_name":"Tickle Me Elmo 3000", "quantity" : 3000 })

// Search in Elastic for the new indexed document

