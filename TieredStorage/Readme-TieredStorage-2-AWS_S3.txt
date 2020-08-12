************************ T I E R E D  S T O R A G E ************************

// Update server.properties config file with those parameters
# Tiered Storage to S3 (AWS Keys are in Env variables or here)
confluent.tier.feature=true
confluent.tier.enable=true
confluent.tier.backend=S3
confluent.tier.s3.bucket=confluent-ola-s3
confluent.tier.s3.region=eu-west-3
confluent.tier.s3.aws.access.key.id=Your-S3-Id
confluent.tier.s3.aws.secret.access.key=Your-S3-Key
# After 5 min, a log segment is deleted locally (will stay in S3 during log.retention.ms)
confluent.tier.local.hotset.ms=300000
# Check every 2 min for topic deletion
confluent.tier.topic.delete.check.interval=120000
confluent.tier.metadata.replication.factor=1
# 36000000 = 600 min = 10H = retention time in S3 (-1 = infinite)
log.retention.ms=36000000
# log segment size: 1 Mo
log.segment.bytes=1048576


# The maximum size of a log segment file. When this size is reached a new log segment will be created.
# Default log.segment.bytes=1073741824
log.segment.bytes=524288

# The interval at which log segments are checked to see if they can be deleted according
# to the retention policies
log.retention.check.interval.ms=300000


// Create a Topic with Tiered Storage
// ==> Retention = 1H
// ==> Hotset (after it goes to S3) = 5 Min
kafka-topics --bootstrap-server localhost:9092 --create --topic twitter-tweets --partitions 3 --replication-factor 1 --config confluent.tier.enable=true --config confluent.tier.local.hotset.ms=60000 --config retention.ms=3600000

kafka-topics --bootstrap-server localhost:9092 --create --topic twitter-tweets --partitions 3 --replication-factor 1

// Produce bulk messages to the Topic to test if it's tiered
kafka-producer-perf-test --topic twitter-tweets --num-records 5000000 --record-size 5000 --throughput -1 --producer-props acks=all bootstrap.servers=localhost:9092 batch.size=8196


// Consume messages from earliest offset (no longer locally but in S3)
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic twitter-tweets --partition 0 --offset 0 > tweets.out

