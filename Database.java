import java.io.*;
import java.nio.charset.Charset;

import Entities.Empresa;

public class Database {
    FileOutputStream fileOs;
    FileInputStream fileIS;
    DataOutputStream out;
    DataInputStream input;
    String path;

    /* == Constructors == */
    Database() throws FileNotFoundException {
        path = "./database";
        fileOs = new FileOutputStream("./database");
        out = new DataOutputStream(fileOs);
    };

    Database(String path) throws FileNotFoundException {
        this.path = path;
        fileOs = new FileOutputStream(path);
        out = new DataOutputStream(fileOs);
    };

    public void closeFile() throws IOException {
        out.close();
    }

    /**
     * Write current Empresa Object on the DB File
     * 
     * @param filePointer current byte that will be read
     */
    public long writeEmpresaOnDb(Empresa empresa, long filePointer) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");

        long pos = filePointer;
        byte[] byteArr;

        /** Sobreescreve a quantidade total */
        raf.writeInt(this.getCurrentSizeOfEntities(raf, filePointer));

        if (pos > 0)
            raf.seek(pos);

        /** Write Empresa data */
        byteArr = empresa.toByteArr();
        raf.writeInt(byteArr.length + 1);
        raf.writeBoolean(true);
        raf.write(byteArr);

        pos = raf.getFilePointer();
        raf.close();

        return pos;
    }

    /**
     * Search on the metadados of the DB file the current size of Entities created.
     */
    public int getCurrentSizeOfEntities(RandomAccessFile file, long filePointer) throws IOException {
        if (filePointer == 0)
            return 1;

        file.seek(0);
        int currentLength = file.readInt() + 1;

        return currentLength;
    }

}
