package fitpay.tests.model;

import java.net.URI;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
	"_links"
})
public abstract class AbstractBase {

	@JsonProperty("_links")
	private HashMap<String, Link> links;
	
	public void addLink(String rel, URI href) {
		addLink(new Link(rel, href));
	}
	
	public void addLink(Link link) {
		if (links == null) {
			links = new HashMap<String, Link>();
		}
		
		links.put(link.getRel(), link);
	}

	public URI getLink(String rel) {
	    if (links != null && links.containsKey(rel)) {
	        return links.get(rel).getHref();
	    }
	    
	    return null;
	}
	
    @Override
    public String toString() {
        return "AbstractBase [links=" + links + "]";
    }
}
