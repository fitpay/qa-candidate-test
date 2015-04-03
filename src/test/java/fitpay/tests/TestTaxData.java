package fitpay.tests;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TestTaxData {
	
		String state;
		BigDecimal preTaxAmount;

		//generated values based on static tax rates
		BigDecimal expectedTax;
		BigDecimal expectedTotal;

		public TestTaxData(){
			
		}
		
		public String toURL(){
			//Format the URL here so if it changes it only has to be done once
			return "?amount=" + preTaxAmount + "&state=" + state;	
		}
		
		public String toString(){
			return "State: " + state + " PreTax: " + preTaxAmount + " Expected: " + expectedTax + " ExpectedTotal:" + expectedTotal;
		}
				
		public TestTaxData(String myState , BigDecimal preTax){
			this.state = myState;
			this.preTaxAmount = preTax;
			this.expectedTax = calculateExpectedTax(this);
			this.expectedTotal = calculateExpectedTotal(this);
		}
		
		public TestTaxData(String myState , String preTax){
			this.state = myState.toUpperCase();
			this.preTaxAmount = new BigDecimal(preTax);
			this.expectedTax = calculateExpectedTax(this);
			this.expectedTotal = calculateExpectedTotal(this);

		}
		private BigDecimal calculateExpectedTax(TestTaxData input){
			BigDecimal taxRate;
			//state is only supporting CA/CO - 
			//	could be an enum association for more values in a case statement or just straight value
			if (input.getState() == "CA") {
				taxRate = new BigDecimal(".075");
			} else if (input.getState() == "CO"){
				taxRate = new BigDecimal(".029");
			} else { //invalid input 
				return new BigDecimal("0.00");
			}
			return input.getAmount().multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
		}
		private BigDecimal calculateExpectedTotal(TestTaxData input){
			return input.getAmount().add(getExpectedTax()).setScale(2, RoundingMode.HALF_UP);
		}
		public void setState(String myState){
			this.state = myState;
		}
		
		public String getState(){
			return state;
		}
		public void setAmount(BigDecimal preTax){
			this.preTaxAmount= preTax;
		}
		public BigDecimal getAmount(){
			return preTaxAmount;
		}
		public BigDecimal getExpectedTax(){
			return expectedTax;
		}		
		public BigDecimal getExpectedTotal(){
			return expectedTotal;
		}
		
		
	}
