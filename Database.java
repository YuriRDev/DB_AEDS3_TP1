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

    public void closeFile() throws IOException{
        out.close();
    }    

    /** Save Empresa to the current DB path */
    public void create(Empresa empresa) throws IOException {
        out.writeBoolean(true); // Escreve condicao de validacao
        out.writeInt(empresa.getId()); // Escreve id 
        out.writeFloat(empresa.getFunding()); // Escreve
        out.writeLong(empresa.getCreatedAtAsLong());
        byte[] fieldBytes = empresa.getNome().getBytes(Charset.forName("UTF-8"));
        out.writeShort(fieldBytes.length);
        out.write(fieldBytes); 
        out.writeInt(empresa.getCategories().length);
        for(String category : empresa.getCategories()) {
            out.writeInt(category.length());
            out.writeUTF(category);
        }
    }

    /** Save Empresa to the current DB path */
    public long write(Empresa empresa, long filePointer) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");

        long pos = filePointer;
        byte[] byteArr;

        if(pos == 0) {
            raf.writeInt(1);
            byteArr = empresa.toByteArr();
            raf.writeInt(byteArr.length + 1);
            raf.writeBoolean(true);
            raf.write(byteArr);
        } else {
            raf.seek(0);
            int num = raf.readInt();
            num++;
            raf.seek(0);
            raf.writeInt(num);
            raf.seek(pos);
            byteArr = empresa.toByteArr();
            raf.writeInt(byteArr.length + 1);
            raf.writeBoolean(true);
            raf.write(byteArr);
        }

        pos = raf.getFilePointer();
        raf.close();

        return pos;
     }
    
     /* Handle amount of records on file */

}
