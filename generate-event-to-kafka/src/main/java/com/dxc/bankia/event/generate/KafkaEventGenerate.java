/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dxc.bankia.event.generate;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Inspired from examples found at github
 * usage JavaDirectKafkaWordCount broker1-host:port,broker2-host:port topic1,topic2
 *
 * @author dfederico@dxc.com
 *
 *5f654766c552:9092 traffic-events
 *. /opt/spark/bin/spark-submit -class com.bankia.nrt.spark.KafkaWordCount usr/local/data/spark/sparkKafkaStreamConsumer-1.0-SNAPSHOT-jar-with-dependencies.jar d6eb7d975dc2:9092 digo
 */

public class KafkaEventGenerate {
    private static final Pattern SPACE = Pattern.compile(" ");

    private static Map<String, Object> buildKafkaParams(String brokers) {
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", brokers); //localhost:9092,anotherhost:9092
        kafkaParams.put("key.deserializer", LongDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "processorGroup");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);
        return kafkaParams;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: JavaDirectKafkaWordCount <brokers> <topics>\n" +
                    "  <brokers> is a list of one or more Kafka brokers\n" +
                    "  <topics> is a list of one or more kafka topics to consume from\n\n");
            System.exit(1);
        }

        //Programmatically set the driver log level (only for stand-alone?)
        //org.apache.log4j.Logger.getRootLogger().setLevel(Level.WARN);

        //Prepare Kafka Config
        Map<String, Object> kafkaParams = buildKafkaParams(args[0]);
        Set<String> topics = new HashSet<>(Arrays.asList(args[1].split(",")));


        //Spark Context and Stream context
        SparkConf conf = new SparkConf()
                .setAppName("com.bankia.nrt.spark.KafkaWordCount")
                .setMaster("spark://spark-master:7077")
                .set("spark.logConf", "true");

        //Raise LOG level - only spark
        JavaSparkContext sc = new JavaSparkContext(conf);
        sc.setLogLevel("WARN");

        JavaStreamingContext ssc = new JavaStreamingContext(sc, Durations.seconds(5));

        //Prepare input DStream
        JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(
                ssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaParams)
        );



        //Process DStream
        JavaPairDStream<String, Integer> wordCounts = stream.map(ConsumerRecord::value)
                .flatMap(line -> Arrays.asList(SPACE.split(line)).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey(Integer::sum);

        //Print Result
        wordCounts.print();

        ssc.start();
        ssc.awaitTermination();
    }
}
