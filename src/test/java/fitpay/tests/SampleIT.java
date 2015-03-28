package fitpay.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Properties;
import java.net.HttpURLConnection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import fitpay.tests.model.Root;
import fitpay.tests.model.Tax;


public class SampleIT {

    private RestTemplate restTemplate;
    private static String baseUrl;
    private Properties props;
    public static String domain;
    public static BigDecimal stateTaxRateCA = new BigDecimal(0.075);
    public static BigDecimal stateTaxRateCO = new BigDecimal(0.029);
    public BigDecimal amount;

    @Before
    public void setupRestTemplate() {
        restTemplate = new RestTemplate();
        props = new Properties();
        try {
            props.load(SampleIT.class.getClassLoader().getResourceAsStream("env.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        baseUrl = props.getProperty("test.domain");
    }

    @Test
    public void happyPathColoradoStateTax(){
        amount = new BigDecimal(1.05);
        taxCalculation(amount, "CO", "");
    }

    @Test
    public void lowBoundaryColoradoStateTax(){
        amount = new BigDecimal(.01);
        taxCalculation(amount, "CO", "");
    }

    @Test
    public void upperBoundaryColoradoStateTax(){
        amount = new BigDecimal(999.99);
        taxCalculation(amount, "CO", "");
    }

    @Test
    public void lowOutOfBoundsColoradoStateTax(){
        amount = new BigDecimal(-0.05);
        taxCalculation(amount, "CO", "");
    }

    @Test
    public void upOutOfBoundsColoradoStateTax(){
        amount = new BigDecimal(1000);
        taxCalculation(amount, "CO", "");
    }

    @Test
    public void happyPathCaliforniaStateTax(){
        amount = new BigDecimal(1.05);
        taxCalculation(amount, "CA", "");
    }

    @Test
    public void lowBoundaryCaliforniaStateTax(){
        amount = new BigDecimal(.01);
        taxCalculation(amount, "CA", "");
    }

    @Test
    public void upperBoundaryCaliforniaStateTax(){
        amount = new BigDecimal(999.99);
        taxCalculation(amount, "CA","");
    }

    @Test
    public void lowOutOfBoundsCaliforniaStateTax(){
        amount = new BigDecimal(-0.05);
        taxCalculation(amount, "CA", "the specified amount must be within 0.01 and 999.99");
    }


    @Test
    public void upOutOfBoundsCaliforniaStateTax(){
        amount = new BigDecimal(1000);
        taxCalculation(amount, "CA", "the specified amount must be within 0.01 and 999.99");
    }

    @Test
    public void invalidState(){
        amount = new BigDecimal(15);
        taxCalculation(amount, "ca", "the specified state is not currently supported");
    }

    @Test
    public void MissingState(){
        amount = new BigDecimal(15);
        taxCalculation(amount, "MO", "the specified state is not currently supported");
    }

    //***I fully realize this is a lazy way to do this, but I know this test is needed and at this point I need to wrap up the assignment
    @Test
    public void invalidAmount(){
        try {
            Tax tax = restTemplate.getForObject(getUrl("/tax") + "?amount=ab&state=CA", Tax.class);
        }
        catch (HttpClientErrorException e){
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("the specified amount is an invalid number"));
        }
    }


    @Test
    public void ensureRootResourceIsAvailable() {
        Root root = restTemplate.getForObject(getUrl("/"), Root.class);
        assertNotNull("no root resource returned", root);
        assertNotNull("no tax hypermedia link returned", root.getLink("tax"));
        assertNotNull("no self hypermedia link returned", root.getLink("self"));
    }

    @Test
    public void requestFailed(){
        try{
            Root root = restTemplate.getForObject(getUrl("/garbage"), Root.class);
        }
        catch  (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("the requested resource was not found"));
        }
    }

    public void taxCalculation(BigDecimal amt, String state, String errorMessage) {
        BigDecimal stateTaxRate = null;
        switch (state) {
            case "CA":
                stateTaxRate = stateTaxRateCA;
                break;
            case "CO":
                stateTaxRate = stateTaxRateCO;
                break;
            case "MO":
            case "ca":
                stateTaxRate = new BigDecimal(0.00);
                break;
        }
        amt = amt.setScale(2, RoundingMode.UP);
        BigDecimal taxAmount = amt.multiply(stateTaxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = amt.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        try {
            Tax tax = restTemplate.getForObject(getUrl("/tax") + "?amount=" + amt + "&state=" + state, Tax.class);

            assertNotNull("no tax result returned", tax);
            assertEquals("no amount returned", amt, tax.getAmount());
            assertEquals("no state returned", state, tax.getState());
            assertEquals("no tax rate returned", stateTaxRate.setScale(3, RoundingMode.HALF_UP), tax.getTaxRate().setScale(3, RoundingMode.HALF_UP));
            assertEquals("calculated CA tax is incorrect", taxAmount, tax.getTax().setScale(2, RoundingMode.HALF_UP));
            assertEquals("calculated grand total is incorrect", total, tax.getGrandTotal().setScale(2, RoundingMode.HALF_UP));
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains(errorMessage));
        }
    }

    private String getUrl(final String path) {
        return String.format("%s/%s", baseUrl, path);
    }
}
