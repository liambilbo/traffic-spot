package com.dxc.bankia.event.pipes;
import com.dxc.bankia.event.functions.ApplyEnrichment;
import com.dxc.bankia.model.Driver;
import com.dxc.bankia.model.Event;
import com.dxc.bankia.model.Notification;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.*;


import java.util.Arrays;
import java.util.List;

/**
 * Created by rahul on 8/25/19.
 */
public class DataSetOps {


    public static void main(String[] args) {


        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Test");
        SparkSession session = SparkSession.builder().config(conf).getOrCreate();

        Encoder<Driver> driverEncoder = Encoders.bean(Driver.class);
        Encoder<Notification> vehicleEncoder = Encoders.bean(Notification.class);
        Encoder<Event> eventEncoder = Encoders.bean(Event.class);

        Event event=new Event();
        Driver driver=new Driver();

// Subscribe to 1 topic
        Dataset<Row> df = session
                .read()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("subscribe", "DEFAULT")
                .load();

        //df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)").show(false);
        /*Dataset<Event> ds=df.selectExpr("CAST(value AS STRING) as json")
                .select( df.from_json(column("json"), schema=smallBatchSchema).as("data"))
        ds.select("id").show(false);

        val dataDf = inputDf.selectExpr("CAST(value AS STRING) as json")
                .select( from_json($"json", schema=smallBatchSchema).as("data"))
                .select("data.*")*/

        Dataset<String> ds=df.selectExpr("CAST(value AS STRING) as json").as(Encoders.STRING());

        List<StructField> fields = Arrays.asList(
                DataTypes.createStructField("id", DataTypes.LongType, false)
                ,DataTypes.createStructField("type", DataTypes.StringType, false)
                ,DataTypes.createStructField("registrationNumber", DataTypes.StringType, true)
                ,DataTypes.createStructField("identificationNumber", DataTypes.StringType, true)
        );
        StructType schema = DataTypes.createStructType(fields);

        Dataset<Row> eventDS=session.read()
                .schema(schema).json(ds);

        eventDS.printSchema();
        //result.printSchema();

        //Object cc=result.collect();

        //System.out.println(cc);
        //session.sparkContext().stop();




/*
        JavaSparkContext jsc = new JavaSparkContext(session.sparkContext());

        //String path= "C:/spark-drools-master/batch/src/main/conf/employee";
        String path= "/media/willy/E353-C0ED/DXC/spark-drools-master/batch/src/main/conf/employee";

        //rdd(jsc, path);

        Dataset<Event> dataset = fromJson(session,path);
        dataset.show();

        Row max = dataset.agg(org.apache.spark.sql.functions.max(dataset.col("id"))).as("max").head();
        System.out.println(max);*/

    }

    private static Dataset<Event> fromJson(SparkSession session, String path) {
        Encoder<Event> employeeEncoder = Encoders.bean(Event.class);
        return session.read().json(path+".json").as(employeeEncoder);
    }
}
