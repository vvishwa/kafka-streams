/*
 * Copyright 2016 Vijay Vishwakarma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bbejeck.processor.stocks;

import bbejeck.model.StockTransaction;
import bbejeck.model.StockDepth;
import bbejeck.serializer.JsonDeserializer;
import bbejeck.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.Stores;
import org.apache.log4j.BasicConfigurator;

import java.util.Properties;

/**
 * User: Vijay Vishwakarma
 * Date: 1/3/17
 * Time: 5:11 PM
 */
public class StockDepthStatefulProcessorDriver {

    public static void main(String[] args) {
    	
    	BasicConfigurator.configure();

        StreamsConfig streamingConfig = new StreamsConfig(getProperties());

        TopologyBuilder builder = new TopologyBuilder();

        JsonSerializer<StockDepth> stockTxnDepthSerializer = new JsonSerializer<>();
        JsonDeserializer<StockDepth> stockTxnDepthDeserializer = new JsonDeserializer<>(StockDepth.class);
        JsonDeserializer<StockTransaction> stockTxnDeserializer = new JsonDeserializer<>(StockTransaction.class);
        JsonSerializer<StockTransaction> stockTxnJsonSerializer = new JsonSerializer<>();
        StringSerializer stringSerializer = new StringSerializer();
        StringDeserializer stringDeserializer = new StringDeserializer();

        Serde<StockDepth> StockDepthSerde = Serdes.serdeFrom(stockTxnDepthSerializer,stockTxnDepthDeserializer);

        builder.addSource("stocks-source", stringDeserializer, stockTxnDeserializer, "stocks", "stocks2")
                       .addProcessor("depth", StockDepthProcessor::new, "stocks-source")
                       .addStateStore(Stores.create("transaction-depth").withStringKeys()
                               .withValues(StockDepthSerde).inMemory().maxEntries(100).build(),"depth")
                       .addSink("sink", "stocks-out", stringSerializer,stockTxnJsonSerializer,"stocks-source")
                       .addSink("sink-2", "transaction-depth", stringSerializer, stockTxnDepthSerializer, "depth");

        System.out.println("Starting StockDepthStatefulProcessor Example");
        KafkaStreams streaming = new KafkaStreams(builder, streamingConfig);
        streaming.start();
        System.out.println("StockDepthStatefulProcessor Example now started");

    }

    private static Properties getProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "Stock-Depth-Stateful-Processor");
        props.put("group.id", "depth-consumer-group");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stateful_depth_processor_id");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "jcionapp1d.jc.jefco.com:9092");
        props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "jcionapp1d.jc.jefco.com:2181");
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        return props;
    }
}
