import java.io.*;

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

    public void closeFile() throws IOException{
        out.close();
    }    

    /** Save Empresa to the current DB path */
    public void create(Empresa empresa) throws IOException {
        out.writeInt(empresa.getSizeOfObject() + 1); // Escreve o tamanho do registro
        out.writeBoolean(true); // Escreve condicao de validacao
        out.writeInt(empresa.getId()); // Escreve id 
        out.writeFloat(empresa.getFunding()); // Escreve
        out.writeLong(empresa.getCreatedAtAsLong());
        out.writeInt(empresa.getNome().length());
        out.writeUTF(empresa.getNome());
        out.writeInt(empresa.getCategories().length);
        for(String category : empresa.getCategories()) {
            out.writeInt(category.length());
            out.writeUTF(category);
        }
    }

}
