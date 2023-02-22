import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import Entities.Empresa;

public class CsvImport {
    BufferedReader reader;
    Database currentDatabase;

    /* Constructor */
    CsvImport(String path, Database createdDB) throws Exception {
        this.currentDatabase = createdDB;
        try {
            this.reader = new BufferedReader(new FileReader((path)));

            String line = reader.readLine();
            int i = 1;
            // Pular primeira linha
            line = reader.readLine();
            // Continuar lendo

            while (line != null) {
                getDataFromLine(line, i);
                line = reader.readLine();
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get the data from the current line and save it to the DB
     * 
     * @param line Line that is going to be parsed
     * @param id Integer that especifies the current ID to save on the DB
     */
    public void getDataFromLine(String line, int id) throws IOException {
        String[] parsedLine = (line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1));

        // created at HERE
        // updated at HERE

        String nome = parsedLine[1].replaceAll("\"", "");
        String categoriesUnparsed = (parsedLine[3]); // Separadados por |
        String[] categories = categoriesUnparsed.split("@@");

        float funding = Float.parseFloat(parsedLine[4].replaceAll("-", "0"));

        Empresa tmp = new Empresa(id, nome, categories, funding);
        this.currentDatabase.create(tmp);
    }
}
