package Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

import Entities.Empresa;

public class Update extends Query {
    public int empresaId;

    public String nome = null;
    public float funding = -1;
    public String[] categories = null;

    public Update() {
        nome = null;
        funding = -1; // negative means no change
        categories = null;
    }

    public Update(String query) {
        nome = null;
        funding = -1;
        categories = null;

        this.empresaId = parseInt(query.split(" ")[0]);
        String queryString = (query.split(" ", 2)[1]);
        checkAllAttributes(queryString.split(" AND "));
    }

    /**
     * Check the types and names of the queryArray
     * 
     * @param queryArr must include [attribute=valueOfType]
     */
    public void checkAllAttributes(String[] queryArr) {
        for (String query : queryArr) {
            String attribute = query.split("=")[0];
            String value = query.split("=")[1];

            checkAttributeEqualSign(query);
            checkIfAttributeExistsForCreation(attribute);
            setAttributesValueToClass(attribute, value);
        }
    }

    /**
     * Set the value of the attribute to the class
     * 
     * @param attribute nome|categories or funding
     * @param value     correponds to it's type
     */
    public void setAttributesValueToClass(String attribute, String value) {
        switch (attribute) {
            case "nome":
                this.nome = value;
                break;
            case "funding":
                this.funding = Float.parseFloat(value);
                break;
            case "categories":
                this.categories = value.split("&&");
                break;
        }
    }

    public int getEmpresaId(){
        return this.empresaId;
    }

    public void printDebug() {
        if (nome != null)
            System.out.println("Nome: " + nome);

        if (categories != null) {
            System.out.println("Categorias: ");
            for (String s : categories) {
                System.out.print(s + ", ");
            }
            System.out.println();
        }

        System.out.println("Funding: " + funding);
    }

    public Empresa returnEmpresaCreated() {
        Empresa tmp = new Empresa(this.nome, this.categories, this.funding);
        return tmp;
    }
}
