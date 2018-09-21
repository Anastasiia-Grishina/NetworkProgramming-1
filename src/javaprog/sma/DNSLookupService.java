package javaprog.sma;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class DNSLookupService implements Deliverable {

	public static final int DNS_SERVICE_MESSAGE = 50;
	public static final int DNS_SERVICE_PORT = 1999;
	public Message send(Message m) {
		String ip = "Unable to resolve ip address";
		String hostName = m.getParam("url");
		try {
			ip = InetAddress.getByName(m.getParam("url")).toString();
		} catch (UnknownHostException e) {
			System.out.println("Unknown host exception");
			e.printStackTrace();
		}
		m.setParam("ip", ip);
		return m;
	}
	public static void main(String args[]) {
		DNSLookupService ds = new DNSLookupService();
		MessageServer ms;
		try {
			ms = new MessageServer(DNS_SERVICE_PORT);
		} catch(Exception e) {
			System.err.println("Could not start service " + e);
			return;
		}
		Thread msThread = new Thread(ms);
		ms.subscribe(DNS_SERVICE_MESSAGE, ds);
		msThread.start();
	}
}