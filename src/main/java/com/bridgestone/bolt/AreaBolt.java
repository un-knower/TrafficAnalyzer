package com.bridgestone.bolt;

import com.bridgestone.controller.TopologyGraphController;
import com.bridgestone.redis.RedisRepository;
import com.bridgestone.storm.MeanCalculatorBolt;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.Utils;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by francesco on 26/07/17.
 */
public class AreaBolt extends BaseRichBolt {
    OutputCollector _collector;
    ObjectMapper mapper;
    TopologyGraphController controller;

    static double speedMean;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        mapper = new ObjectMapper();
        _collector = collector;
        controller = TopologyGraphController.getInstance();
    }

    @Override
    public void execute(Tuple tuple) {
        System.err.println("received" + tuple.getString(0) + "!!!" + "\n\n\n\n\n\n\n\n\n\n");
        String jsonData = tuple.getString(0);
        try {
            this.controller.updateGraphTopology(jsonData);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            _collector.ack(tuple);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields());
    }






    public static void main(String[] args) throws Exception {

        TopologyBuilder builder = new TopologyBuilder();
        String zkConnString = "localhost:2181";
        double x, y;
        x = 52.12;
        y = 41.34;
        String topic = Double.toString(x) + Double.toString(y);
        BrokerHosts hosts = new ZkHosts(zkConnString);

        SpoutConfig kafkaSpoutConfig = new SpoutConfig (hosts, topic, "/" + topic,
                UUID.randomUUID().toString());
        kafkaSpoutConfig.bufferSizeBytes = 1024 * 1024 * 4;
        kafkaSpoutConfig.fetchSizeBytes = 1024 * 1024 * 4;
        kafkaSpoutConfig.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
        kafkaSpoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());




        builder.setSpout("StreetInfo", new KafkaSpout(kafkaSpoutConfig),5);
        //parallelism hint: number of thread for node
        //builder.setBolt("exclaim1", new ExclamationBolt(), 3).shuffleGrouping("StreetInfo");
        //builder.setBolt("exclaim2", new ExclamationBolt(), 2).shuffleGrouping("exclaim1");
        builder.setBolt("mean", new AreaBolt(),8).shuffleGrouping("StreetInfo");

        Config conf = new Config();
        conf.setDebug(false);



        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", conf, builder.createTopology());
        System.err.print("Submitted topology " + topic + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        Utils.sleep(100000);
        cluster.killTopology("test");
        cluster.shutdown();

    }

}