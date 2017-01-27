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

package bbejeck.model;

/**
 * User: Vijay Vishwakarma
 * Date: 1/3/17
 * Time: 3:32 PM
 */
public class StockDepth {

    public double amount;
    public String tickerSymbol;
    public int sharesPurchased;
    public int sharesSold;
    private long lastUpdatedTime;

    public void update(StockTransaction transaction){
          this.amount += transaction.getAmount();
          if(transaction.getType().equalsIgnoreCase("purchase")){
              this.sharesPurchased += transaction.getShares();
          } else{
              this.sharesSold += transaction.getShares();
          }
        this.lastUpdatedTime = System.currentTimeMillis();
    }

    public boolean updatedWithinLastMillis(long currentTime, long limit){
         return currentTime - this.lastUpdatedTime <= limit;
    }

    public static StockDepth fromTransaction(StockTransaction transaction){
    	StockDepth depth = new StockDepth();
             depth.tickerSymbol = transaction.getSymbol();
             depth.update(transaction);
             return depth;
    }
}
