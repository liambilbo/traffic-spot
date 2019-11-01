package com.dxc.bankia.event.pipes;

import com.dxc.bankia.event.functions.ApplyEnrichment;
import com.dxc.bankia.event.objects.EventExecuted;
import com.dxc.bankia.event.serializer.EventDeserializer;
import com.dxc.bankia.model.Event;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * /opt/spark/bin/spark-submit --class com.dxc.bankia.event.pipes.EventEnrichmentPipe usr/local/data/spark/SparkConsumer.jar kafka-broker:9092 DEFAULT
 *
 */
public class EventEnrichmentPipe {



    private static final Logger LOGGER = LoggerFactory.getLogger(EventEnrichmentPipe.class);

    private static Map<String, Object> buildKafkaParams(String brokers) {
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers); //localhost:9092,anotherhost:9092
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "processorGroup");
        //kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventDeserializer.class);

        return kafkaParams;
    }

    public static void main(String[] args)  throws Exception {

        if (args.length < 2) {
            System.err.println("Usage: EventEnrichmentPipe <brokers> <topics>\n"
                    + "  <brokers> is a list of one or more Kafka brokers\n"
                    + "  <topics> is a list of one or more kafka topics to consume from\n\n");
            System.exit(1);
        }

        Map<String, Object> kafkaParams = buildKafkaParams(args[0]);
        Set<String> topics = new HashSet<>(Arrays.asList(args[1].split(",")));


        SparkSession sparkSession = SparkSession.builder().master("spark://spark-master:7077").appName("com.dxc.bankia.event.pipies.EventEnrichmentPipe").config("spark.logConf","true").getOrCreate();
        //SparkSession sparkSession = SparkSession.builder().master("local[2]").appName("com.dxc.bankia.event.pipies.EventEnrichmentPipe").config("spark.logConf","true").getOrCreate();

        JavaSparkContext sc = new JavaSparkContext(sparkSession.sparkContext());
        sc.setLogLevel("INFO");

        JavaStreamingContext jssc= new JavaStreamingContext(new JavaSparkContext(sparkSession.sparkContext()),
                Durations.seconds(30));

        sparkSession.sparkContext().setLogLevel("INFO");

        JavaInputDStream<ConsumerRecord<String, Event>> stream = KafkaUtils.createDirectStream(
                jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaParams)
        );

        //Process DStream
        JavaDStream<Event> events = stream.map(ConsumerRecord::value);

        JavaDStream<EventExecuted> executed = events.map(Event::new).mapPartitions(new ApplyEnrichment("com.dxc.bankia"
                , "traffic-enrichment-rules-kjar" , "1.0.0-SNAPSHOT"));

        executed.print();


        //executed.filter(EventExecuted::isWithErrors).map(e -> e.getErrors()).print();


        //executed.filter(EventExecuted::isFiltered).map(e -> e.getFilters()).print();

        jssc.start();
        jssc.awaitTermination();
    }

}
