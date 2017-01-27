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

import java.util.Objects;

import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import bbejeck.model.StockDepth;
import bbejeck.model.StockTransaction;

/**
 * User: Vijay Vishwakarma
 * Date: 1/3/17
 * Time: 8:13 PM
 */

public class StockDepthProcessor extends AbstractProcessor<String, StockTransaction> {

    private KeyValueStore<String, StockDepth> depthStore;
    private ProcessorContext context;

    public void process(String key, StockTransaction stockTransaction) {
        String currentSymbol = stockTransaction.getSymbol();
        StockDepth depth = depthStore.get(currentSymbol);
        if (depth == null) {
            depth = StockDepth.fromTransaction(stockTransaction);
        } else {
            depth.update(stockTransaction);
        }
        depthStore.put(currentSymbol, depth);
        this.context.forward(currentSymbol, depth);
        this.context.commit();
    }
    
    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        depthStore = (KeyValueStore<String, StockDepth>) this.context.getStateStore("transaction-depth");
        Objects.requireNonNull(depthStore, "State store can't be null");
    }
}
