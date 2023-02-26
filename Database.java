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
    public void writeEmpresaOnDb(Empresa empresa) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        byte[] byteArr;


        /** Sobreescreve a quantidade total */
        raf.writeInt(getCurrentSizeOfEntities(raf));

        /* Set the pointer to the end of the file */
        raf.seek(raf.length());

        /** Write Empresa data */
        byteArr = empresa.toByteArr();
        raf.writeInt(byteArr.length + 1);
        raf.writeBoolean(true);
        raf.write(byteArr);

        raf.close();

    }

    /**
     * Search on the metadados of the DB file the current size of Entities created.
     */
    public int getCurrentSizeOfEntities(RandomAccessFile file) throws IOException {
        file.seek(0); // First seek to read if is empty
        if(file.read() == -1) return 1; // case if file is empty
        
        file.seek(0); // Second seek to read the integer
        int currentLength = file.readInt() + 1;
        file.seek(0); // Third seek to position the pointer on the first byte again.
        return currentLength;
    }


}
