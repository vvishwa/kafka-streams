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

import bbejeck.model.Purchase;
import bbejeck.model.PurchasePattern;
import bbejeck.model.RewardAccumulator;
import bbejeck.serializer.JsonDeserializer;
import bbejeck.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.processor.internals.WallclockTimestampExtractor;

import java.util.Properties;

/**
 * User: Bill Bejeck
 * Date: 3/7/16
 * Time: 5:34 PM
 */
public class PurchaseKafkaStreamsDriver {

    public static void main(String[] args) {


        StreamsConfig streamsConfig = new StreamsConfig(getProperties());

        JsonDeserializer<Purchase> purchaseJsonDeserializer = new JsonDeserializer<>(Purchase.class);
        JsonSerializer<Purchase> purchaseJsonSerializer = new JsonSerializer<>();
        JsonSerializer<RewardAccumulator> rewardAccumulatorJsonSerializer = new JsonSerializer<>();
        JsonSerializer<PurchasePattern> purchasePatternJsonSerializer = new JsonSerializer<>();

        StringDeserializer stringDeserializer = new StringDeserializer();
        StringSerializer stringSerializer = new StringSerializer();

        KStreamBuilder kStreamBuilder = new KStreamBuilder();

        KeyValueMapper<String,Purchase,KeyValue<String,Purchase>> creditCardMasking = (key, prchs) -> {
            Purchase masked = Purchase.newBuilder(prchs).maskCreditCard().build();
            return new KeyValue<>(key,masked);
        };

        KeyValueMapper<String,Purchase,KeyValue<String,PurchasePattern>> createPattern = (key, prchs) ->{
            PurchasePattern pattern = PurchasePattern.newBuilder().date(prchs.getPurchaseDate())
                    .item(prchs.getItemPurchased()).build();
            return new KeyValue<>(key,pattern);
        };

        KeyValueMapper<String,Purchase,KeyValue<String,RewardAccumulator>> createReward = (key, prchs) -> {
                 RewardAccumulator accumulator = RewardAccumulator.newBuilder(prchs).build();
            return new KeyValue<>(key,accumulator);
        };

        KStream<String,Purchase> purchaseKStream = kStreamBuilder.stream(stringDeserializer,purchaseJsonDeserializer,"src-topic")
                .map(creditCardMasking);

        purchaseKStream.map(createPattern).to("patterns",stringSerializer,purchasePatternJsonSerializer);

        purchaseKStream.map(createReward).to("rewards",stringSerializer,rewardAccumulatorJsonSerializer);

        purchaseKStream.to("purchases",stringSerializer,purchaseJsonSerializer);

        System.out.println("Starting PurchaseStreams Example");
        KafkaStreams kafkaStreams = new KafkaStreams(kStreamBuilder,streamsConfig);
        kafkaStreams.start();
        System.out.println("Now started PurchaseStreams Example");

    }




    private static Properties getProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "Example-Kafka-Streams-Job");
        props.put("group.id", "streams-purchases");
        props.put(StreamsConfig.JOB_ID_CONFIG, "testing-streams-api");
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
