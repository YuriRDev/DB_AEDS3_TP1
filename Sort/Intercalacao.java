package Sort;

import java.io.*;

import Entities.Empresa;

public class Intercalacao {
    int blocos = 5;
    int caminhos = 5;
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
                    while (empresaArrTmp[i] == null)
                        empresaArrTmp[i] = deserializeEmpresa(dbFile);
                }

                quicksort(empresaArrTmp);

                /** Salvar na temp */
                RandomAccessFile currentTmpFile = tempFiles[fileCount % caminhos];

                for (int i = 0; i < blocos; i++) {
                    // length, boolean valido
                    byte[] empresaByteArr = empresaArrTmp[i].toByteArr();
                    int size = empresaByteArr.length + 1 + 4;
                    currentTmpFile.writeInt(size);
                    currentTmpFile.writeBoolean(true);
                    currentTmpFile.write(empresaByteArr);
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

    /** Deserialize */
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

    /* Sorting on primary memory, we'll be using quicksort */
    public void quicksort(Empresa arr[]) {
        int n = arr.length;
        quicksort(0, n - 1, arr);
    }

    /* Quicksort */
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

    /* Swap positions on array */
    private void swap(int i, int j, Empresa arr[]) {
        Empresa tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

}
