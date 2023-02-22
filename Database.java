import java.io.*;

import Entities.Empresa;

public class Database {
    FileOutputStream fileOs;

    /* == Constructors == */
    Database() throws FileNotFoundException {
        fileOs = new FileOutputStream("./database");
    };

    Database(String path) throws FileNotFoundException {
        fileOs = new FileOutputStream("path");
    };

    /** Save Empresa to the current DB path */
    public void create(Empresa empresa) throws IOException {
        DataOutputStream out = new DataOutputStream(fileOs);
        out.writeInt(12); // Escreve o tamanho do vetor
        out.writeDouble(3.12); // Escreve cada valor
        out.close();
    }

}
