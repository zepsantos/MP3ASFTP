package Server;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;

public class DataTransfer {
    private Socket socket;

    public DataTransfer(Socket socket) {
        this.socket = socket;
    }

    public void DownloadFile(String fileName) throws IOException {
        DataInputStream dis = new DataInputStream(this.socket.getInputStream());
        BufferedInputStream input = new BufferedInputStream(dis);
        long size = dis.readLong();
        OutputStream outputFile = new FileOutputStream(fileName);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while(size > 0 && (bytesRead = input.read(buffer,0,(int) Math.min(buffer.length,size))) >= 0) {
            outputFile.write(buffer,0,bytesRead);
            size -= bytesRead;
            outputFile.flush();
        }
        outputFile.close();
        dis.close();
        input.close();

    }

    public void UploadFile(String fileName) throws IOException {
        File tmp = new File(fileName);
        FileInputStream fis = new FileInputStream(tmp);
        BufferedInputStream bis = new BufferedInputStream(fis);
        OutputStream os =  this.socket.getOutputStream();
        DataOutputStream out = new DataOutputStream(os);
        long size = tmp.length();
        out.writeLong(size);
        byte[] buffer = new byte[8192];
        int bytesRead = 0;
        while (size > 0 && (bytesRead = bis.read(buffer, 0,(int)  Math.min(buffer.length, size))) >= 0) {
            out.write(buffer,0,bytesRead);
            size-=bytesRead;
            out.flush();
        }
        fis.close();
        out.close();
        bis.close();
        os.close();
    }



}
