package fitpay.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import fitpay.tests.model.Root;
import fitpay.tests.model.Tax;

public class SampleIT {

    private RestTemplate restTemplate;
    private final static String baseUrl = "http://qa.fit-pay.com";

    @Before
    public void setupRestTemplate() {
        restTemplate = new RestTemplate();
    }
    
    @Test
    public void ensureRootResourceIsAvailable() {
        Root root = restTemplate.getForObject(getUrl("/"), Root.class);
        assertNotNull("no root resource returned", root);
        assertNotNull("no tax hypermedia link returned", root.getLink("tax"));
        assertNotNull("no self hypermedia link returned", root.getLink("self"));
    }
    
    @Test
    public void happyPathTaxCalculation() {
        Tax tax = restTemplate.getForObject(getUrl("/tax") + "?amount=1.05&state=CA", Tax.class);
        
        assertNotNull("no tax result returned", tax);
        assertNotNull("no tax rate returned", tax.getTaxRate());
        assertEquals("calculated CA tax is incorrect", new BigDecimal(0.08).setScale(2, RoundingMode.HALF_UP), tax.getTax());
        assertEquals("calculated grand total is incorrect", new BigDecimal(1.13).setScale(2, RoundingMode.HALF_UP), tax.getGrandTotal());
    }
    
    private String getUrl(final String path) {
        return String.format("%s/%s", baseUrl, path);
    }
}
