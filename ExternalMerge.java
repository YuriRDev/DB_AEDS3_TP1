import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.Math;

import Entities.Empresa;

// AKA Intercalação Balanceada
public class ExternalMerge extends Database {
    int recordAmount; // Current amount of records in our file.
    int blockSize; // Amount of block of registers.
    int pathAmount = 2; // Amount of paths, I left it static.
    String path; // RandomAccessFile path.
    RandomAccessFile file;
    RandomAccessFile temp[]; // Temporary files.

    /* Constructors */
    public ExternalMerge(int blockSize,String path) throws IOException {
        this.blockSize = blockSize;
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

            Empresa[] records = new Empresa[blockSize]; // Create block of size 4, will be sorted on main memory

            for(int i = 0; i < records.length; i++){
                records[i] = deserializeEmpresa(file); // Assigns entity to array position
            }
             
           sort(records); // Sort entities by ID

           for(int i = 0; i < records.length; i++) {temp[currentFile].write(records[i].toByteArr());} // Write in current temporary file
            
            currentFile++;
        }
    }

    /* Intercalate between tempFiles
     * @param blockSize = amount of records that will be merged (increases by *n at each call)
     */
    private void intercalate() throws IOException {
        
        /*  We calculate how many times we'll pass through the loop by writing a simple expression
        *   we do log at base (@param pathAmount) of N(@param total record amount)/b(@param sizeOf sorted records in memory)
        */
        int passings = (int)(Math.ceil((Math.log10(recordAmount/blockSize))/Math.log10(pathAmount)));
        boolean phase = true; // On false, write on second file pair. On true, on first file pair.
        int segmentSize = blockSize;

        for(int i = 0; i < passings; i++) {  // Run the amount
            if(!phase) { // 1 e 2 p/ 3 e 4
                int count3 = 0, count4 = 0, counter = 0;
                int curr = 0;
                Empresa[] a1 = new Empresa[segmentSize];
                Empresa[] a2 = new Empresa[segmentSize];
                while(temp[0].getFilePointer() < temp[0].length() && temp[1].getFilePointer() < temp[1].length()) {
                        if(curr >=2) {curr = 0;} // reset curr

                        for(int j=0; j < segmentSize*2; j++) {
                            a1[counter] = deserializeEmpresa(temp[0]);
                            a2[counter++] = deserializeEmpresa(temp[1]);
                        }
                        //Merge and write in order to new File
                        for(int k = 0; k < segmentSize*4; k++) {
                            if(curr == 0) {
                                if(a1[count3].getId() < a2[count4].getId()) {
                                    temp[2].write(a1[count3++].toByteArr());
                                }else{temp[2].write(a2[count4++].toByteArr());}
                            }else {
                                if(a1[count3].getId() < a2[count4].getId()) {
                                    temp[3].write(a1[count3++].toByteArr());
                                }else{temp[3].write(a2[count4++].toByteArr());}
                            }
                        }
                        count3 = 0;
                        count4 = 0;                    
                }



            }
            else if(phase) {

            }


        }

    }


    /* Search and returns entity */
    private Empresa deserializeEmpresa(RandomAccessFile file)  throws IOException {
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