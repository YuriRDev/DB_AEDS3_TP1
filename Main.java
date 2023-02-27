import java.io.*;
import java.util.Scanner;

import Entities.Empresa;
import Query.Create;
import Query.Update;
import Query.Search;

class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        Database myDB = new Database("myDb.db");
        // System.out.println(myDB.getCurrentSizeOfEntities());

        // CsvImport myCSV = new CsvImport("./dataset.csv", myDB);
        // getUserInput(myDB);

        // Empresa tmp = myDB.findEmpresaByIdSequencially(559);
        // tmp.print();


        String[] updateCategories = {"abc", "def"};
        Empresa updatedEmpresa = new Empresa("Novo nome", updateCategories, 123);

        myDB.updateEmpresaById(12, updatedEmpresa);
    }

    public static void getUserInput(Database database) throws IOException {
        String input = sc.nextLine();
        String verb = input.split(" ")[0];

        if (verb.equals("CREATE")) {
            Create createQuery = new Create(input.split("CREATE ")[1]);

            Empresa returnEmpresa = createQuery.returnEmpresaCreated();
            System.out.println(database.getCurrentSizeOfEntities() + 1);
            returnEmpresa.setId(database.getCurrentSizeOfEntities() + 1);

            database.writeEmpresaOnDb(returnEmpresa);
        } else if (verb.equals("SELECT")) {
            Search searchQuery = new Search(input.split("SELECT ")[1]);
        } else {
            throw new Error(
                    "Error while parsing query. Received: " + verb + " expected CREATE, SELECT, UPDATE or REMOVE");
        }
    }
}