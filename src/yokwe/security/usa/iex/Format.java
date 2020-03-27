package yokwe.security.usa.iex;

public enum Format {
	JSON ("json"),  // Five years
	CSV  ("csv");   // Two years
	
	public final String value;
	Format(String value) {
		this.value = value;
	}
}
