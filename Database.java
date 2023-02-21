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
        ByteArrayOutputStream out = new ByteArrayOutputStream();

    }

}
