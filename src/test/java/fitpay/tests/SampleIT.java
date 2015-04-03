package fitpay.tests;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import fitpay.tests.model.Root;
import fitpay.tests.model.Tax;


/* commenting here as check-in comments appear to be public

Includes:
Suggestions but not implementations for externalized parameters
System Property for setting baseURL in pom (defaults to qa if not set)
API verifications of status 400
In Code bug description of failed status 404 check/test
*/


public class SampleIT {

    public RestTemplate restTemplate;

    //ORIG:  private final static String baseUrl = "http://qa.fit-pay.com";
	//allow a -D parameter to be set
    private String baseURL = System.getProperty("baseURL");
	//no input - default to QA since default should NEVER be production
		//and dev usually has better setups to not run these ad-hoc all the time

    /*
    * Extending data loading possibilities:
    *	static array used for time
    * load a csv file of state, tax values
    * load a props file of state, tax values
    * using a java parameterized runWith parameters
    */

    List<TestTaxData> testData = new ArrayList<TestTaxData>();
    List<TestTaxData> negTestData = new ArrayList<TestTaxData>();
    List<TestTaxData> unsupportedStatesData = new ArrayList<TestTaxData>();

    /* Values that should be included
     * 	Verified: API requires positive values
     *  	Cannot do: Negative numbers, numbers that evaluate to zero
     *  max values =999.99
     */

    @Before
    public void setupRestTemplate() {
        restTemplate = new RestTemplate();
    	if (baseURL == "" || baseURL == null ) {
    		baseURL="http://qa.fit-pay.com";
    	}
    }

    @Before
    public void setup(){
    	//ideally from a file as explained above
        testData.add(new TestTaxData("CA","1.05"));
        testData.add(new TestTaxData("CO","1.05"));
        testData.add(new TestTaxData("CA","6.05"));
        testData.add(new TestTaxData("CO","6.05"));
        //min&max values
        testData.add(new TestTaxData("CA","0.01"));
        testData.add(new TestTaxData("CO","0.01"));
        testData.add(new TestTaxData("CA","999.99"));
        testData.add(new TestTaxData("CO","999.99"));

        testData.add(new TestTaxData("CA","10.01"));
        testData.add(new TestTaxData("CO","10.01"));
        testData.add(new TestTaxData("CA","199.99"));
        testData.add(new TestTaxData("CO","199.99"));

        //these should all return 400
        negTestData.add(new TestTaxData("CO","0"));
        negTestData.add(new TestTaxData("CA","0"));
        negTestData.add(new TestTaxData("CO","-.01"));
        negTestData.add(new TestTaxData("CA","-.01"));
        negTestData.add(new TestTaxData("CO","1000"));
        negTestData.add(new TestTaxData("CA","1000"));

        unsupportedStatesData.add(new TestTaxData("MO","1000"));
        //should be the same response for a state that will never exist
        unsupportedStatesData.add(new TestTaxData("XY","1000"));

    }

    //always keep the isAvailable test
    @Test
    public void ensureRootResourceIsAvailable() {
        Root root = restTemplate.getForObject(getUrl("/"), Root.class);
        assertNotNull("no root resource returned", root);
        assertNotNull("no tax hypermedia link returned", root.getLink("tax"));
        assertNotNull("no self hypermedia link returned", root.getLink("self"));
    }

    /*
    * Need a way to get the tax rate to ensure it is correct
    * 	With this current test every time the CA tax rate changes we have to update the test since that could invalidate the result
    * 	where do we check that the API has the right value? Is that place here? Is that our responsibility?
    * I have hardCoded the values in the TestTaxData class, but i don't think that is a long term solution.
    * 	I would feel better about the tests if they were not based on a hard-coded value that could change without our input
    */

    @Test
    public void happyPathTaxCalculation() {
    	Tax tax = restTemplate.getForObject(getUrl("/tax") + "?amount=1.05&state=CA", Tax.class);

        assertNotNull("no tax result returned", tax);
        assertNotNull("no tax rate returned", tax.getTaxRate());

        assertEquals("calculated CA tax is incorrect", new BigDecimal(0.08).setScale(2, RoundingMode.HALF_UP), tax.getTax());
        assertEquals("calculated grand total is incorrect", new BigDecimal(1.13).setScale(2, RoundingMode.HALF_UP), tax.getGrandTotal());
    }


    @Test
    public void verifyHappyPathTaxCalculation() {
    	//taxRates: CA=.075, CO=.029
    	for (TestTaxData e : testData){
    	    Tax tax = restTemplate.getForObject(getUrl("/tax") + e.toURL(), Tax.class);
    	    	//need to implement a logger, very helpful for all troubleshooting tasks
    	    	//System.out.println("HERE " + e.toString());
    	    assertNotNull("taxRate is null", tax.getTaxRate());
    	    assertNotNull("total is null", tax.getGrandTotal());
    	    	//System.out.println("TaxRate=" + tax.getTaxRate());

    	    //talk to dev, is it a bug that the scale is not always returned as 2 digits? is that ok?
        	//	the setScale(2) will throw a number exception which should fail if it returns more than 2 digits to the right of the scale
    	    assertEquals("total tax is incorrect", e.getExpectedTax(), tax.getTax().setScale(2) );
    	    assertEquals("Grand total is incorrect",e.getExpectedTotal(), tax.getGrandTotal().setScale(2));
    	}
    }


    @Test
    public void verifyHttpStatus_400(){
    	for (TestTaxData e : negTestData){
    		String result = new String();
    		//Initialize to good status
    		HttpStatus exc = HttpStatus.CREATED;
    		try {
    			result = restTemplate.getForObject(getUrl("/tax") + e.toURL(), String.class);
    		} catch (HttpStatusCodeException exception) {
                exc = exception.getStatusCode();
    		}
            //verify that a 400 error was returned for negative value/zero/out of bounds tests
            assertEquals("status was not 400", HttpStatus.BAD_REQUEST, exc);

    	}
    }

    /* ISSUE:
     * Summary: API Specification states that if a state is not yet supported, the response will
     * 		be status 404
     * Reproduction Steps: Send a state value that has not yet been implemented, ex. NY
     * 		along with a valid amount value to the /tax Url
     * Expected Results:  Status 404
     * Actual Results: Status 400 - Bad Request
     *
     */

    @Test
    public void verifyHttpStatus_404(){
    	for (TestTaxData e : unsupportedStatesData){
    		String result = new String();
    		//Initialize to good status
    		HttpStatus exc = HttpStatus.CREATED;
    		try {
    			result = restTemplate.getForObject(getUrl("/tax") + e.toURL(), String.class);
    		} catch (HttpStatusCodeException exception) {
                exc = exception.getStatusCode();
    		}
    		//verify that a 400 error was returned for negative value/zero/out of bounds tests
            assertEquals("status was not 404, NOT_FOUND", HttpStatus.NOT_FOUND, exc);
    	}
    }

    private String getUrl(final String path) {
        return String.format("%s/%s", baseURL, path);
    }
}
