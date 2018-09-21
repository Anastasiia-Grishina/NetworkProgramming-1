package javaprog.sma;

public class DNSLookupClient {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: DNSLookup host port");
		}
		String host = args[0];
		int port;
		try {
			port = Integer.parseInt(args[1]);
		} catch(Exception e) {
			port = DNSLookupService.DNS_SERVICE_PORT;
		}
		MessageClient conn;
		try {
			conn = new MessageClient(host,port);
		} catch(Exception e) {
			System.err.println(e);
			return;
		}
		Message m = new Message();
		m.setType(DNSLookupService.DNS_SERVICE_MESSAGE);
		
		String hostName = args[2];
		System.out.println("Hostname: " + hostName);
		
		m.setParam("url", hostName);
		m = conn.call(m);
		System.out.println("IP address: " + m.getParam("ip"));
		conn.disconnect();
	}
}