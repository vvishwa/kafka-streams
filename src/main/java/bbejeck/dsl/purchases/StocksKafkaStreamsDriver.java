/*
 * Copyright 2016 Bill Bejeck
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

package bbejeck.dsl.purchases;

import bbejeck.model.StockTransaction;
import bbejeck.processor.stocks.StockTransactions;
import bbejeck.serializer.JsonDeserializer;
import bbejeck.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.TumblingWindows;
import org.apache.kafka.streams.kstream.internals.WindowedSerializer;
import org.apache.kafka.streams.processor.internals.WallclockTimestampExtractor;

import java.util.Properties;

/**
 * User: Bill Bejeck
 * Date: 3/9/16
 * Time: 9:21 PM
 */
public class StocksKafkaStreamsDriver {

    public static void main(String[] args) {

        StreamsConfig streamingConfig = new StreamsConfig(getProperties());

        JsonSerializer<StockTransactions> stockTransactionsSerializer = new JsonSerializer<>();
        JsonDeserializer<StockTransactions> stockTransactionsDeserializer = new JsonDeserializer<>(StockTransactions.class);
        JsonDeserializer<StockTransaction> stockTxnDeserializer = new JsonDeserializer<>(StockTransaction.class);
        JsonSerializer<StockTransaction> stockTxnJsonSerializer = new JsonSerializer<>();
        StringSerializer stringSerializer = new StringSerializer();
        StringDeserializer stringDeserializer = new StringDeserializer();
        WindowedSerializer<String> windowedSerializer = new WindowedSerializer<>(stringSerializer);

        KStreamBuilder kStreamBuilder = new KStreamBuilder();


        KStream<String,StockTransaction> transactionKStream =  kStreamBuilder.stream(stringDeserializer,stockTxnDeserializer,"stocks");

        transactionKStream.through("stocks-source", stringSerializer, stockTxnJsonSerializer,stringDeserializer, stockTxnDeserializer)
                .map((k,v)-> new KeyValue<>(v.getSymbol(),v))
                .aggregateByKey(StockTransactions::new,
                               (k, v, st) -> st.add(v),
                               TumblingWindows.of("stock-summaries").with(10000),
                               stringSerializer,stockTransactionsSerializer,stringDeserializer,stockTransactionsDeserializer)
                .to("transaction-summary", windowedSerializer,stockTransactionsSerializer);


        System.out.println("Starting StockStreams Example");
        KafkaStreams kafkaStreams = new KafkaStreams(kStreamBuilder,streamingConfig);
        kafkaStreams.start();
        System.out.println("Now started StockStreams Example");

    }

    private static Properties getProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "Stocks-Streams-Processor");
        props.put("group.id", "stock-streams");
        props.put(StreamsConfig.JOB_ID_CONFIG, "stocks_streams_id");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        props.put(StreamsConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(StreamsConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(StreamsConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(StreamsConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        return props;
    }
}
