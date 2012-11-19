package net.soartex.admin.helpers;

import java.io.*;
import java.net.*;

public class FTPupload {
    protected FTPclientConn cconn;
    public final String localfile;
    public final String targetfile;

    public FTPupload(String _host, String _user, String _password,
         String _localfile, String _targetfile) {
        cconn = new FTPclientConn(_host, _user, _password);
        localfile = _localfile;
        targetfile = _targetfile;

        doit();
    }
    public FTPupload(String _host, String _user, String _password, String _file) {
        cconn = new FTPclientConn(_host, _user, _password);
        localfile = _file;
        targetfile = _file;

        doit();
    }

    protected void doit() {
        try {
            OutputStream os = cconn.openUploadStream(targetfile);
            FileInputStream is = new FileInputStream(localfile);
            byte[] buf = new byte[600];
            int c;
            System.out.print("[");
            while (true) {
                System.out.print(".");
                c = is.read(buf);
                if (c <= 0)  {
                	System.out.println("]");
                	break;
                }
                os.write(buf, 0, c);
            }
            os.close();
            is.close();
            cconn.close(); // section 3.2.5 of RFC1738
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}

class FTPclientConn {
    public final String host;
    public final String user;
    protected final String password;
    protected URLConnection urlc;

    public FTPclientConn(String _host, String _user, String _password) {
        host = _host;  user= _user;  password= _password;
        urlc = null;
    }

    protected URL makeURL(String targetfile) throws MalformedURLException  {
        if (user == null)
            return new URL("ftp://"+ host+ "/"+ targetfile+ ";type=i");
        else
            return new URL("ftp://"+ user+ ":"+ password+ "@"+ host+ "/"+ targetfile+ ";type=i");
    }

    protected InputStream openDownloadStream(String targetfile) throws Exception {
        URL url = makeURL(targetfile);
        urlc = url.openConnection();
        InputStream is = urlc.getInputStream();

        return is;
    }

    protected OutputStream openUploadStream(String targetfile) throws Exception {
        URL url= makeURL(targetfile);
        urlc = url.openConnection();
        OutputStream os = urlc.getOutputStream();

        return os;
    }

    protected void close() {
        urlc= null;
    }
}
