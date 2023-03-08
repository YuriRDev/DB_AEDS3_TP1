import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.Math;

import Entities.Empresa;

// AKA Intercalação Balanceada
public class ExternalMerge extends Database {
    int recordAmount; // Current amount of records in our file.
    int blockSize; // Amount of block of registers.
    int pathAmount; // Amount of paths.
    String path; // RandomAccessFile path.
    RandomAccessFile file;
    RandomAccessFile temp[]; // Temporary files.

    /* Constructors */
    public ExternalMerge(int blockSize, int pathAmount, String path) throws IOException {
        this.blockSize = blockSize;
        this.pathAmount = pathAmount;
        this.path = path;
        file = new RandomAccessFile(path, "rw");
        this.recordAmount = file.readInt(); 
        temp = new RandomAccessFile[pathAmount*2];
        for(int i = 0; i < temp.length; i++) {
            temp[i] = new RandomAccessFile("tmpFile"+i, "rw");
        }
    }


    public void preLoad() throws IOException {
        file.seek(4); // Skip header to ease readability
        int currentFile = 0;
        while (!eof()) {
            if(currentFile == pathAmount) {currentFile = 0;}

            Empresa[] records = new Empresa[pathAmount*2]; // Create block of size 4, will be sorted on main memory

            for(int i = 0; i < records.length; i++){
                records[i] = deserializeEmpresa(); // Assigns entity to array position
            }
             
           sort(records); // Sort entities by ID

           for(int i = 0; i < records.length; i++) {temp[currentFile].write(records[i].toByteArr());} // Write in current temporary file
            
            currentFile++;
        }
    }

    /* Intercalate between tempFiles
     * @param blockSize = amount of records that will be merged (increases by *n at each call)
     */
    public void intercalate() throws IOException {

        /*  We calculate how many times we'll pass through the loop by writing a simple expression
        *   we do log at base (@param pathAmount) of N(@param total record amount)/b(@param sizeOf sorted records in memory)
        */
        int passings = (int)(1 + ((Math.log(recordAmount/recordAmount))/Math.log(pathAmount)));

        
        /*  Escrever de arquivos 1 e 2 para 3 e 4, e vice-versa, 
        *   limpando os arquivos que receberao os dados ordenados a cada passada.
        *   
        *   Existe .clear() pra RandomAccessFile?? Como limpar os arquivos??
        */ 

        


    }


    /* Search and returns entity */
    private Empresa deserializeEmpresa()  throws IOException {
        Empresa curr = new Empresa();

        /* Skip data... */
        file.readInt(); // Size of record
        file.readBoolean(); // isValid

        /* Getting data */
        curr.setId(file.readInt());
        curr.setFunding(file.readFloat());
        curr.setCreated_At(file.readLong());
        file.readInt(); // Name length
        curr.setNome(file.readUTF());
        int amount = file.readInt(); // Amount of categories
        String[] categories = new String[amount];
        for(int i = 0; i < amount; i++) {
            file.readInt(); // Skip String length
            categories[i] = file.readUTF();
        }

        return curr;
    }


    /* Returns true when filePointer reaches end of file */
    private boolean eof() throws IOException {
        return file.getFilePointer() == file.length();
    }


    /* Sorting on primary memory, we'll be using quicksort */
    public void sort(Empresa arr[]) {
        int n = arr.length;
        quicksort(0,n-1,arr);
    }

    /* Quicksort */
    private void quicksort(int left, int right, Empresa arr[]) {
        int i = left, j = right;
        // Create Pivot
        Empresa pivot = arr[(left+right)/2]; // Check value of pivot, not position.

        while(i <= j) {
            while(arr[i].getId() < pivot.getId()) {i++;}

            while(arr[j].getId() > pivot.getId()) {j--;}

            if(i <= j) {
                swap(i,j,arr);
                i++;
                j--;
            }
        }

        if(left < j) {quicksort(left, j, arr);}
        if(i < right){quicksort(i, right, arr);}
    }


    /* Swap positions on array */
    private void swap(int i, int j, Empresa arr[]) {
        Empresa tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }



}