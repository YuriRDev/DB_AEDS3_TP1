import java.io.*;
import java.util.Scanner;

import Entities.Empresa;
import Query.Create;
import Query.Query;
import Query.Search;

class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        // String[] a = {"abc", "cde"};
        // Empresa teste = new Empresa(0, "Teste", a, 0);

        // Database myDB = new Database("myDb.db");
        // myDB.create(teste);
        getUserInput();
    }

    public static void getUserInput() {
        String input = sc.nextLine();
        String verb = input.split(" ")[0];

        if (verb.equals("CREATE")) {
            Create createQuery = new Create(input.split("CREATE ")[1]);
            Empresa returnEmpresa = createQuery.returnEmpresaCreated();
            returnEmpresa.print();
        } else if (verb.equals("SELECT")) {
            Search searchQuery = new Search(input.split("SELECT ")[1]);
        } else {
            throw new Error(
                    "Error while parsing query. Received: " + verb + " expected CREATE, SELECT, UPDATE or REMOVE");
        }
    }
}