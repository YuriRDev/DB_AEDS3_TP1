import java.io.*;
import java.util.Date;
import java.util.Scanner;

import Entities.Empresa;
import Query.Create;
import Query.Delete;
import Query.Update;
import Query.Search;

class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        Database myDB = new Database("myDb.db");
        // CsvImport myCSV = new CsvImport("./dataset.csv", myDB);
        printCommands();
        getUserInput(myDB);

        getUserInput(myDB);

        // Empresa tmp = myDB.findEmpresaByIdSequencially(12);
        // tmp.print();
        // String[] updateCategories = {"abc", "def"};
        // Empresa updatedEmpresa = new Empresa("Nomepequeno", updateCategories, 1);

        // myDB.updateEmpresaById(12, updatedEmpresa);
    }

    public static void getUserInput(Database database) throws IOException {
        String input = sc.nextLine();
        String verb = input.split(" ")[0];

        long queryStarted = System.currentTimeMillis();
        switch (verb) {
            case "CREATE": {
                Create createQuery = new Create(input.split("CREATE ")[1]);

                Empresa returnEmpresa = createQuery.returnEmpresaCreated();
                returnEmpresa.setId(database.getCurrentSizeOfEntities() + 1);

                database.writeEmpresaOnDb(returnEmpresa);
                System.out.println("[Id] " + returnEmpresa.getId());
                break;
            }
            case "SELECT": {
                Search searchQuery = new Search(input.split("SELECT ")[1]);

                Empresa empresaFund = database.findEmpresaBySearchQuery(searchQuery);
                empresaFund.print();
                break;
            }
            case "UPDATE": {
                // UPDATE id nome=batata AND funding=102
                Update updateQuery = new Update(input.split("UPDATE ")[1]);

                Empresa empresaCreated = updateQuery.returnEmpresaCreated();
                database.updateEmpresaById(updateQuery.getEmpresaId(), empresaCreated);

                break;
            }
            case "DELETE":{
                Delete deleteQuery = new Delete(input.split("DELETE ")[1]);
                database.deleteEmpresaById(deleteQuery.getId());
                break;
            }
            default: {
                throw new Error(
                        "Error while parsing query. Received: " + verb + " expected CREATE, SELECT, UPDATE or REMOVE");
            }
        }

        long queryFinished = System.currentTimeMillis();
        long timeElapsed = queryFinished - queryStarted;
        System.out.println("Time elapsed: " + timeElapsed + "ms");
    }

    /**
     * Print on the terminal the possibles query that can be used
     */
    public static void printCommands() {
        printAsciiLogo();
        System.out.println("[] Querys");
        System.out.println("    help --create|-c");
        System.out.println("    help --select|-s");
        System.out.println("    help --update|-u");
        System.out.println("    help --delete|-d");

        System.out.println();
        System.out.println("[] Overall commands");
        System.out.println("    listall");
    }

    /**
     * Print ASCII art from the DB logo
     */
    public static void printAsciiLogo() {
        System.out.println("\n\n\n");
        System.out.println("▒█▀▀▄ █▀▀█ █▀▀▀ █▀▀ ▒█▀▀▄ ▒█▀▀█ ");
        System.out.println("▒█░▒█ █░░█ █░▀█ █▀▀ ▒█░▒█ ▒█▀▀▄ ");
        System.out.println("▒█▄▄▀ ▀▀▀▀ ▀▀▀▀ ▀▀▀ ▒█▄▄▀ ▒█▄▄█");
        System.out.println("\n- DogeDB 1.0 -\n");
    }
}