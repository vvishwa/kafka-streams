package bbejeck.model;

import java.util.Comparator;

public class StockTransactionComparator implements Comparator<StockTransaction>{
	
	public int compare(StockTransaction stock1, StockTransaction stock2) {
		
		if( stock1.getAmount() > stock2.getAmount() )  {
			return 1;
		}
		else if( stock1.getAmount() == stock2.getAmount() )  {
			return 0;
		}
		return -1;
	}

}