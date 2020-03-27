package yokwe.security.usa.iex;

public enum Type {
	PRODUCTION("https://cloud.iexapis.com"),
	SANDBOX   ("https://sandbox.iexapis.com");
	
	public final String url;
	
	Type(String newURL) {
		this.url   = newURL;
	}
	
	@Override
	public String toString() {
		return String.format("%s", this.name());
	}
}