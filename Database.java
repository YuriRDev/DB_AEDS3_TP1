import java.io.*;

import Entities.Empresa;

public class Database {
    FileOutputStream fileOs;

    /* == Constructors == */
    Database() throws FileNotFoundException {
        fileOs = new FileOutputStream("./database");
    };

    Database(String path) throws FileNotFoundException {
        fileOs = new FileOutputStream(path);
    };

    /** Save Empresa to the current DB path */
    public void create(Empresa empresa) throws IOException {
        DataOutputStream out = new DataOutputStream(fileOs);
        out.writeInt(empresa.getSizeOfObject() + 1); // Escreve o tamanho do registro
        out.writeBoolean(true); // Escreve condicao de validacao
        out.writeInt(empresa.getId()); // Escreve id 
        out.writeFloat(empresa.getFunding()); // Escreve
        out.writeLong(empresa.getCreatedAtAsLong());
        out.writeInt(empresa.getNome().length());
        out.writeChars(empresa.getNome());
        out.writeInt(empresa.getCategories().length);
        for(String category : empresa.getCategories()) {
            out.writeInt(category.length());
            out.writeChars(category);
        }
        out.close();
    }

}
