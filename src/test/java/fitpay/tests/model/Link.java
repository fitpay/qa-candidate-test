package fitpay.tests.model;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
	"rel",
	"href",
	"title"
})
public class Link {

	@JsonIgnore
	private String rel;
	
	@JsonProperty("href")
	private URI href;

	@JsonProperty("title")
	private String title;
	
	public Link() {}
	
	public Link(String rel, URI href) {
		this.rel = rel;
		this.href = href;
	}
	
	public Link withRel(String rel) {
		this.rel = rel;
		return this;
	}
	
	public Link withHref(URI href) {
		this.href = href;
		return this;
	}
	
	public Link withTitle(String title) {
		this.title = title;
		return this;
	}
	
	
	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public URI getHref() {
		return href;
	}

	public void setHref(URI href) {
		this.href = href;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    @Override
    public String toString() {
        return "Link [rel=" + rel + ", href=" + href + ", title=" + title + "]";
    }
}
