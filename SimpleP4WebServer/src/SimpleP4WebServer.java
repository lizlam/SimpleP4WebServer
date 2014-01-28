import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * @author Liz Lam
 * 
 * Programming exercise to learn more about web servers
 *
 * Idea: Submit simple web sites into Perforce server.  
 *       Use SimpleP4WebServer will serve up pages directly from the depot. 
 *
 */
public class SimpleP4WebServer {
	
	private static final int PORT = 8080;
	private String depotRoot = "//depot/";
	private String indexPage = "index.html";
	private static ServerSocket dServerSocket;
	DepotFile depotFile = null;
	
	/**
	 * Default constructor
	 * @throws Exception
	 */
	public SimpleP4WebServer() throws Exception {
		dServerSocket = new ServerSocket(PORT);
	}
	
	public void run()  throws Exception {
		while (true) {
			Socket s = dServerSocket.accept();
			processRequest(s);
		}
	}
	
	/**
	 * Set depot path to where index page resides
	 * Example: //depot/website
	 * @param path
	 */
	public void setDepotRoot(String path) {
		if (path.charAt(path.length()-1) != '/') {
			depotRoot = path + "/";
		} else {
			depotRoot = path;
		}
	}
	
	/**
	 * Set index page, default is index.html
	 * @param index
	 */
	public void setIndexPage(String index) {
		indexPage = index;
	}
	
	public void processRequest(Socket s) throws IOException {
		BufferedReader br = 
				new BufferedReader(
						new InputStreamReader(s.getInputStream()));

		OutputStreamWriter osw =
				new OutputStreamWriter(s.getOutputStream());
		
		String request = br.readLine();
		
		String command = null;
		String path = null;
		
		StringTokenizer st =
				new StringTokenizer(request, " ");
		
		command = st.nextToken();
		path = st.nextToken();
		
		if (command.equals("GET")) {
			try {
				serveFile(osw, path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			osw.write("HTTP/1.0 501 Not Implemented\n\n");
		}
		osw.close();
	}
	
	public void serveFile(OutputStreamWriter osw, String path) throws Exception {
		StringBuffer sb = new StringBuffer();
		if (path.charAt(0) == '/') {
			path = path.substring(1);
		}
		if (path.equals("")) {
			path = indexPage;
		}
		depotFile = new DepotFile(depotRoot + path);
		if (!depotFile.isExist()) {
			osw.write("HTTP/1.0 404 Not Found\n\n <h1>404 File not found</h1>");
		} else {
			osw.write("HTTP/1.0 200 OK\n\n");
			InputStreamReader isr = new InputStreamReader(depotFile.getP4FileContent());
			BufferedReader br2  = new BufferedReader(isr);
			String read = br2.readLine();
			while (read != null) {
				System.out.println(read);
				sb.append(read);
				read = br2.readLine();
			}
			osw.write(sb.toString());
		}
	}
	
	public static void main(String args[]) throws Exception {
		SimpleP4WebServer sws = new SimpleP4WebServer();
		sws.setDepotRoot("//depot/website/");
		sws.setIndexPage("example.html");
		sws.run();
	}
 
}
