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

    public Empresa readFirst() throws IOException {
        RandomAccessFile file = new RandomAccessFile(path, "r");

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        DataOutputStream byteData = new DataOutputStream(byteOutput);
        
        // Jump the first 4 bytes ( length of entities)
        file.seek(4);

        /** Metadados  */
        byteData.writeInt(file.readInt()); // Byte in length
        byteData.writeBoolean(file.readBoolean()); // IsValid
        
        /** Object In Bytes */
        byteData.writeInt(file.readInt()); // ID
        byteData.writeFloat(file.readFloat()); // Funding
        byteData.writeLong(file.readLong()); // Created_At

        byteData.writeInt(file.readInt()); // Name Length
        byteData.writeUTF(file.readUTF()); // Name

        int categoriesLength = file.readInt();
        byteData.writeInt(categoriesLength);

        for(int i =0; i < categoriesLength; i++){
            byteData.writeInt(file.readInt());
            byteData.writeUTF(file.readUTF());
        }

        Empresa novaEmpresa = new Empresa();
        novaEmpresa.fromByteArr(byteOutput.toByteArray());

        file.close();

        return novaEmpresa;
    }


    /**
     * Read an byteArray from a specific byte 
     * 
     * @param seekValue must be > 4
     */
    public Empresa readFromSeek(int seekValue) throws IOException {
        if(seekValue <= 4) throw new Error("Seek value must be more than 4");

        RandomAccessFile file = new RandomAccessFile(path, "r");

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        DataOutputStream byteData = new DataOutputStream(byteOutput);
        
        file.seek(seekValue);
        checkIfByteArrayIsFromEmpresa(file);
        file.seek(seekValue);

        /** Metadados  */
        byteData.writeInt(file.readInt()); // Byte in length
        byteData.writeBoolean(file.readBoolean()); // IsValid
        
        /** Object In Bytes */
        byteData.writeInt(file.readInt()); // ID
        byteData.writeFloat(file.readFloat()); // Funding
        byteData.writeLong(file.readLong()); // Created_At

        byteData.writeInt(file.readInt()); // Name Length
        byteData.writeUTF(file.readUTF()); // Name

        int categoriesLength = file.readInt();
        byteData.writeInt(categoriesLength);

        for(int i =0; i < categoriesLength; i++){
            byteData.writeInt(file.readInt());
            byteData.writeUTF(file.readUTF());
        }

        Empresa novaEmpresa = new Empresa();
        novaEmpresa.fromByteArr(byteOutput.toByteArray());

        file.close();

        return novaEmpresa;
    }


    /**
     *  Check if current ByteArray is from the empresa entity
     *  
     *  <h3>Currently not working the throw...</h3>
     */
    public void checkIfByteArrayIsFromEmpresa(RandomAccessFile file) throws IOException {
        try {
            file.readInt();
            file.readBoolean();
            file.readInt();
            file.readFloat();
            file.readLong();
        } catch (IOException e) {
            file.close();
            throw new Error("Error... This byte is probable not from the start of a entity");
        }
    }
}
