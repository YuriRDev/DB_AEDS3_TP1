import java.io.*;
import java.nio.charset.Charset;
import java.util.Random;

import Entities.Empresa;
import Query.Search;
import Query.Update;

public class Database {
    String path;

    /* == Constructors == */
    Database() throws FileNotFoundException {
        path = "./database";
    };

    Database(String path) throws FileNotFoundException {
        this.path = path;
    };

    /**
     * Write current Empresa Object on the DB File
     * 
     * @param filePointer current byte that will be read
     */
    public void writeEmpresaOnDb(Empresa empresa) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        byte[] byteArr;

        /** Sobreescreve a quantidade total */
        raf.writeInt(getCurrentSizeOfEntities() + 1);

        /* Set the pointer to the end of the file */
        raf.seek(raf.length());

        /** Write Empresa data */
        byteArr = empresa.toByteArr();
        raf.writeInt(byteArr.length + 1 + 4); // +1 boolean +4 byte Size
        raf.writeBoolean(true);
        raf.write(byteArr);

        raf.close();

    }

    /**
     * Search on the metadados of the DB file the current size of Entities created.
     */
    public int getCurrentSizeOfEntities() throws IOException {
        RandomAccessFile file = new RandomAccessFile(path, "rw");

        file.seek(0); // First seek to read if is empty
        if (file.read() == -1)
            return 0; // case if file is empty

        file.seek(0); // Second seek to read the integer
        int currentLength = file.readInt();
        file.seek(0); // Third seek to position the pointer on the first byte again.
        return currentLength;
    }

    /**
     * Read the first entity on the DB
     */
    public Empresa readFirst() throws IOException {
        return this.readFromSeek(4);
    }

    /**
     * Read an byteArray from a specific byte
     * 
     * @param seekValue must be > 4
     */
    public Empresa readFromSeek(long seekValue) throws IOException {
        if (seekValue <= 0)
            throw new Error("Seek value must be more than 0");

        RandomAccessFile file = new RandomAccessFile(path, "r");

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        DataOutputStream byteData = new DataOutputStream(byteOutput);

        file.seek(seekValue);
        checkIfByteArrayIsFromEmpresa(file);
        file.seek(seekValue);

        /** Metadados */
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

        for (int i = 0; i < categoriesLength; i++) {
            byteData.writeInt(file.readInt());
            byteData.writeUTF(file.readUTF());
        }

        Empresa novaEmpresa = new Empresa();
        novaEmpresa.fromByteArr(byteOutput.toByteArray());

        file.close();

        return novaEmpresa;
    }

    /**
     * Check if current ByteArray is from the empresa entity
     * 
     * <h3>Currently not working the throw...</h3>
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

    /**
     * Find an empresa object sequentially by it's id
     * 
     * @throws Error if could not find it
     */
    public Empresa findEmpresaByIdSequencially(int id) throws IOException {
        if (id <= 0)
            throw new Error("Id must be more than 0");

        RandomAccessFile file = new RandomAccessFile(path, "r");

        /** Check the metadados for the ID */
        if (id > file.readInt()) {
            file.close();
            throw new Error("Could not find the ID");
        }

        long filePointer = 0; // file pointer to be returned
        int currentSize = file.readInt();
        boolean isValid = file.readBoolean();
        int currentId = file.readInt();

        while (currentSize < file.length()) {

            if (id == currentId && isValid) {
                filePointer = file.getFilePointer();
                currentSize = (int) file.length(); // Break operation
            } else {
                file.seek(currentSize + 4);
                currentSize += file.readInt();
                isValid = file.readBoolean();
                currentId = file.readInt();
            }
        }

        if (id != currentId) {
            file.close();
            throw new Error("Could not find the ID");
        }

        Empresa returnedEmpresa = readFromSeek((filePointer - 4 - 4 - 1));

        file.close();
        return returnedEmpresa;
    }

    /**
     * Search an entity by it's id
     * And get the file pointer value to the first byte
     * 
     */
    public long findEmpresaFilePointerById(int id) throws IOException {
        if (id <= 0)
            throw new IOException("ID must be more than 0.");

        RandomAccessFile raf = new RandomAccessFile(path, "rw");

        /* Check for ID avaliability */
        if (id > raf.readInt())
            throw new IOException("Could not find the ID.");

        long filePointer = 0; // File pointer to be returned
        int currentSize = raf.readInt();
        boolean isValid = raf.readBoolean();
        int currentID = raf.readInt();

        while (currentSize < raf.length()) {

            if (currentID == id && isValid) {
                filePointer = raf.getFilePointer();
                currentSize = (int) raf.length(); // Break operation
            } else {
                raf.seek(currentSize + 4);
                currentSize += raf.readInt();
                isValid = raf.readBoolean();
                currentID = raf.readInt();
            }
        }

        if (id != currentID) {
            raf.close();
            throw new IOException("ID does not exist or was deleted.");
        }

        long returnPosition = filePointer - 4 - 1; // 4 from id, 1 for boolean
        raf.close();

        return returnPosition;
    }

    /**
     * Find and empresa Object sequentially by it's parrams
     * 
     * @param query The query param from the parser
     */
    public Empresa findEmpresaBySearchQuery(Search query) throws IOException {

        for (int i = 1; i < getCurrentSizeOfEntities(); i++) {
            Empresa tmp = findEmpresaByIdSequencially(i);
            if (tmp.matchWithSearchQuery(query))
                return tmp;

        }

        throw new Error("Could not find the Empresa by this query");
    }

    /**
     * Finds empresa file pointer sequentially on the DB and changes the boolean
     * valid to false
     */
    public void deleteEmpresaById(int id) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");

        long pos = findEmpresaFilePointerById(id);

        raf.seek(pos);
        raf.writeBoolean(false);

        raf.close();
    }

    /**
     * Update current empresa with other stats
     * Find empresa file Pointer sequentially on the DB
     */
    public void updateEmpresaById(int id, Empresa newData) throws IOException {
        Empresa empresaFound = findEmpresaByIdSequencially(id);

        empresaFound.mergeData(newData);

        if (empresaFound.compareByteSizeWithOtherEmpresa(newData) < 0) {
            deleteEmpresaById(id);
            writeEmpresaOnDb(empresaFound);
        } else {
            long filePointer = findEmpresaFilePointerById(id) + 5; // Point to FUNDING row
            reWriteEmpresaByFilePointer(filePointer, empresaFound);
        }
    }

    /*
     * Fully rewrites an filePointer entity record on file.
     */
    public void reWriteEmpresaByFilePointer(long pos, Empresa empresa) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");

        raf.seek(pos);
        raf.writeFloat(empresa.getFunding()); // Write funding

        long currentPos = raf.getFilePointer() + 8; // Skip created_at

        raf.seek(currentPos);
        raf.writeInt(empresa.getNome().getBytes(Charset.forName("UTF-8")).length); // write size of Nome field
        raf.writeUTF(empresa.getNome()); // Write name
        raf.writeInt(empresa.getCategories().length); // Write amount of categories
        for (String category : empresa.getCategories()) {
            raf.writeInt(category.getBytes(Charset.forName("UTF-8")).length);
            raf.writeUTF(category);
        }

        raf.close();
    }

}
