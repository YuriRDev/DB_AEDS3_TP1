package Sort;

import java.io.*;

import Entities.Empresa;

public class Intercalacao {
    int blocos = 3;
    int caminhos = 2;
    String path = "myDb.db";
    RandomAccessFile dbFile;
    RandomAccessFile[] tempFiles;

    public Intercalacao() throws IOException {
        dbFile = new RandomAccessFile(path, "r");
        tempFiles = new RandomAccessFile[caminhos * 2]; // ter o dobro de tmp

        for (int i = 0; i < caminhos * 2; i++) {
            String nome = "tmp" + i;
            tempFiles[i] = new RandomAccessFile(nome, "rw");
        }
    }

    /**
     * Load first tmp files
     */
    public void loadTempFile() throws IOException {
        dbFile.readInt();
        int fileCount = 0;
        try {
            while (!eof(dbFile)) {
                Empresa[] empresaArrTmp = new Empresa[blocos];

                /** Pegar empresa sequencialmente */
                for (int i = 0; i < blocos; i++) {
                    empresaArrTmp[i] = deserializeEmpresa(dbFile);

                    /** Caso null, tentar novamente ate nao ser mais null */
                    while (empresaArrTmp[i] == null && !eof(dbFile)) {

                        empresaArrTmp[i] = deserializeEmpresa(dbFile);
                    }
                }

                /** Verificar se o ultimo bloco existe, caso nao, limpe o array */
                if (empresaArrTmp[blocos - 1] == null)
                    empresaArrTmp = removeEmptyEmpresaFromArr(empresaArrTmp);

                /** Quicksort na memoria principal */
                quicksort(empresaArrTmp);

                /** Salvar na temp */
                RandomAccessFile currentTmpFile = tempFiles[fileCount % caminhos];
                for (int i = 0; i < empresaArrTmp.length; i++) {

                    // length, boolean valido
                    if (empresaArrTmp[i] == null)
                        continue;

                    byte[] empresaByteArr = empresaArrTmp[i].toByteArr();
                    int size = empresaByteArr.length + 1 + 4;
                    currentTmpFile.writeInt(size);
                    currentTmpFile.writeBoolean(true);
                    currentTmpFile.write(empresaByteArr);
                    System.out.println(empresaArrTmp[i].getId());
                }

                fileCount++;
            }

        } catch (EOFException e) {
            System.out.println("[debug] Terminou o preload");
        }

    }

    /**
     * Check if pointer read all the bytes
     */
    public boolean eof(RandomAccessFile file) throws IOException {
        return file.getFilePointer() == file.length();
    }

    /** Remove empty Empresas from array */
    public Empresa[] removeEmptyEmpresaFromArr(Empresa[] empressaArr) {
        int length = 0;
        for (Empresa a : empressaArr) {
            if (a != null)
                length++;
        }

        Empresa[] clearArr = new Empresa[length];
        for (int i = 0; i < length; i++) {
            clearArr[i] = empressaArr[i];
        }

        return clearArr;

    }

    /**
     * Convert byteArray to empresa
     */
    public Empresa deserializeEmpresa(RandomAccessFile file) throws IOException {
        Empresa curr = new Empresa();

        file.readInt();
        boolean valid = file.readBoolean();

        /* Getting data */
        curr.setId(file.readInt());
        curr.setFunding(file.readFloat());
        curr.setCreated_At(file.readLong());
        file.readInt(); // Name length
        curr.setNome(file.readUTF());
        int amount = file.readInt(); // Amount of categories
        String[] categories = new String[amount];
        for (int i = 0; i < amount; i++) {
            file.readInt(); // Skip String length
            categories[i] = file.readUTF();
        }
        curr.setCategories(categories);

        return valid ? curr : null;
    }

    /**
     * Append ByteArray of empresa on a file
     * 
     * @param file    file that we want to append
     * @param empresa Empresa that we want to write
     * @throws IOException if file has not been constructed yet
     */
    public void appendEmpresaOnByte(RandomAccessFile file, Empresa empresa) throws IOException {
        long currentPost = file.getFilePointer();

        file.seek(file.length());

        byte[] empresaByteArr = empresa.toByteArr();
        int size = empresaByteArr.length + 1 + 4;
        file.writeInt(size);
        file.writeBoolean(true);
        file.write(empresaByteArr);

        file.seek(currentPost);
    }

    /**
     * Quick sort method (constructor)
     */
    public void quicksort(Empresa arr[]) {
        int n = arr.length;
        quicksort(0, n - 1, arr);
    }

    /**
     * Quicksort sort method
     * 
     * @param right arr lenght
     * @param arr   Empresa array
     */
    private void quicksort(int left, int right, Empresa arr[]) {
        int i = left, j = right;
        // Create Pivot
        Empresa pivot = arr[(left + right) / 2]; // Check value of pivot, not position.

        while (i <= j) {
            while (arr[i].getId() < pivot.getId()) {
                i++;
            }

            while (arr[j].getId() > pivot.getId()) {
                j--;
            }

            if (i <= j) {
                swap(i, j, arr);
                i++;
                j--;
            }
        }

        if (left < j) {
            quicksort(left, j, arr);
        }
        if (i < right) {
            quicksort(i, right, arr);
        }
    }

    /**
     * Swap position on the array
     * 
     * @param i   pos1
     * @param j   pos2
     * @param arr new array with swapped position
     */
    private void swap(int i, int j, Empresa arr[]) {
        Empresa tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    /**
     * DEBUG!!!!
     */
    public void firstIntercalate() throws IOException {
        // Reset pointers
        pointAllFPToStart();

        int firstCount = 1;
        int seccondCount = 1;

        Empresa empresaUm = deserializeEmpresa(tempFiles[0]);
        Empresa empresaDois = deserializeEmpresa(tempFiles[1]);

        do {
            while (firstCount < blocos || seccondCount < blocos) {

                if (firstCount >= blocos) {

                    // Bom... Vamos pegar o valor da empresa Dois e escrever
                    System.out.println(empresaDois.getId());

                    seccondCount++;
                    Empresa oldTmp = empresaDois;
                    try {
                        empresaDois = deserializeEmpresa(tempFiles[1]);
                    } catch (EOFException e) {
                        System.out.println(oldTmp.getId());
                        System.out.println("Opa, deu EOF na empresa DOIS...");
                        seccondCount += 10;
                        continue;

                    }
                }

                if (seccondCount >= blocos) {
                    // Bom... Vamos pegar o valor da empresa Dois e escrever
                    System.out.println(empresaDois.getId());

                    firstCount++;
                    Empresa oldTmp = empresaUm;
                    try {
                        empresaUm = deserializeEmpresa(tempFiles[0]);
                    } catch (EOFException e) {
                        System.out.println(oldTmp.getId());
                        System.out.println("Opa, deu EOF na empresa UM...");
                        firstCount += 10;
                        continue;

                    }
                }
            
                /** Comparar pra ver qual é maior ou menor */
                if(empresaUm.getId() < empresaDois.getId()){
                    System.out.println(empresaUm.getId());

                    Empresa oldTmp = empresaUm;
                    firstCount++;
                    try {
                        empresaUm = deserializeEmpresa(tempFiles[0]);
                    } catch (EOFException e){
                        System.out.println(oldTmp.getId());
                        System.out.println("Deu EOF na empresa UM.... comparando");
                        firstCount+=10;
                        continue;
                    }
                }
            
                /** Comparar pra ver qual é maior ou menor */
                if(empresaUm.getId() > empresaDois.getId()){
                    System.out.println(empresaDois.getId());

                    Empresa oldTmp = empresaDois;
                    seccondCount++;
                    try {
                        empresaDois = deserializeEmpresa(tempFiles[1]);
                    } catch (EOFException e){
                        System.out.println(oldTmp.getId());
                        System.out.println("Deu EOF na empresa DOIS.... comparando");
                        seccondCount+=10;

                    }
                }
            
                
            }

            firstCount = 0;
            seccondCount = 0;

        } while (!eof(tempFiles[0]) || !eof(tempFiles[1]));

        System.out.println("Acabou eof");
    }

    public void printIntercaleTmp() throws IOException {
        // Reset pointers
        pointAllFPToStart();

        int i = 0;
        do {

            Empresa tmp;
            tmp = deserializeEmpresa(tempFiles[0]);
            System.out.println("[0] - " + tmp.getId() + " - " + tmp.getNome());
            i++;
        } while (!eof(tempFiles[0]));

        do {
            Empresa tmp;
            tmp = deserializeEmpresa(tempFiles[1]);
            System.out.println("[1] - " + tmp.getId() + " - " + tmp.getNome());
            i++;
        } while (!eof(tempFiles[1]));

        System.out.println("Acabou eof");
    }

    /**
     * Reset all file pointers of the class
     * Make the file pointers point to the begging
     * 
     * @throws IOException if file has not called a constructor
     */
    public void pointAllFPToStart() throws IOException {
        for (int i = 0; i < tempFiles.length; i++) {
            tempFiles[i].seek(0);
        }
    }
}
