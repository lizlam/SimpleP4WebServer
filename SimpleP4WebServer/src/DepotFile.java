import java.io.InputStream;
import java.util.List;

import com.perforce.p4java.core.file.FileSpecBuilder;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.P4JavaException;
import com.perforce.p4java.option.server.GetFileContentsOptions;
import com.perforce.p4java.server.IOptionsServer;
import com.perforce.p4java.server.ServerFactory;

public class DepotFile {
	IOptionsServer server = null;
	private String URI = "p4java://server:1999";
	private List<IFileSpec> file;
	private String depotPath;
	private String user = "user";
	private String password = "password";
	
	public DepotFile(String path) {
		depotPath = path;
		file = FileSpecBuilder.makeFileSpecList(path);
		try {
			server = ServerFactory.getOptionsServer(URI, null);
			if (server != null) {
				server.connect();
				server.setUserName(user);
				server.login(password);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public boolean isExist() throws P4JavaException {
		if (server.getFileContents(file, null) != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public InputStream getP4FileContent() {
		InputStream s = null;
		GetFileContentsOptions options = new GetFileContentsOptions();
		options.setNoHeaderLine(true);
		try {
				s = server.getFileContents(file, options);
			} catch (P4JavaException e) {
				e.printStackTrace();
			}	
		return s;
	}
}
