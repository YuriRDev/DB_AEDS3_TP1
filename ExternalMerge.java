import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import Entities.Empresa;

// AKA Intercalação Balanceada
public class ExternalMerge extends Database {
    int n; // Amount of records
    int m; // Amount of paths
    String path; // RandomAccessFile path
    RandomAccessFile file;
    RandomAccessFile temp[]; // Temporary files

    /* Constructors */
    public ExternalMerge(int n, int m, String path) throws IOException {
        this.n = n;
        this.m = m;
        this.path = path;
        file = new RandomAccessFile(path, "rw");
        temp = new RandomAccessFile[m*2];
        for(int i = 0; i < temp.length; i++) {
            temp[i] = new RandomAccessFile("tmpFile"+i, "rw");
        }
    }


    public void preLoad() throws IOException {
        file.seek(4); // Skip header to ease readability
        int currentFile = 0;
        while (!eof()) {
            if(currentFile == m) {currentFile = 0;}

            Empresa[] records = new Empresa[m*2]; // Create block of size 4, will be sorted on main memory

            for(int i = 0; i < records.length; i++){
                records[i] = deserializeEmpresa(); // Assigns entity to array position
            }
             
           sort(records); // Sort entities by ID

           for(int i = 0; i < records.length; i++) {temp[currentFile].write(records[i].toByteArr());} // Write in current temporary file
            
            currentFile++;
        }

        int recordAmount = this.n; // Initialize record amount as this.n

        /* Don't want to make intercalate() as a recursive function, maybe a 
         * loop at the end of preLoad()??? 
         * 
         * Any ideas??
         */

    }

    /* Intercalate between tempFiles
     * @param recordAmount = amount of records that will be merged (increases by *n at each call)
     */
    public void intercalate(int recordAmount) throws IOException {
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