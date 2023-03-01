package Query;

import java.sql.Date;

// SELECT n>0|* id>3 AND value=2 AND x=abc def
public class Search extends Query {
    public int id = -1;
    public String name = null;
    public String[] categories = null;
    public float funding = -1;
    public Date created_at;

    public int idSearch;
    public int nameSearch;
    public int categorySearch;
    public int fundingSearch;
    public int createdAtSearch;

    public int quantityToSelect;

    public Search(String text) {
        String quantity = text.split(" ", 2)[0];
        String query = text.split(" ", 2)[1];

        this.quantityToSelect = getQuantitySelected(quantity);
        checkEveryQuery(query.split(" AND "));
    }

    /**
     * Split the query and treat every information from it
     */
    public void checkEveryQuery(String[] queryStr) {
        for (String query : queryStr) {

            String splitSign = checkQueryMathSign(query);
            int signIntValue = getSearchSignValue(splitSign);
            
            String attributeName = checkIfAttributeExists(query, splitSign);
            String attributeValue = query.split(splitSign)[1];

            setAttributesValueToClass(attributeName, attributeValue, signIntValue);
        }

    }


    /**
     * Set the value of the attribute to the class
     * 
     * @param attribute nome|categories or funding
     * @param value correponds to it's type
     */
    public void setAttributesValueToClass(String attribute, String value, int splitSign) {


        switch (attribute) {
            case "id": 
                this.id = parseInt(value);
                this.idSearch = splitSign;
                break;
            case "nome":
                this.name = value;
                this.nameSearch = splitSign;
                break;
            case "funding":
                this.funding = parseFloat(value);
                this.fundingSearch = splitSign;
                break;
            case "categories":
                this.categories = value.split("&&");
                this.categorySearch = splitSign;
                break;
        }
    }



    /** 
     * Debug
     */
    public void debugPrint() {
        if(this.id != -1){
            System.out.println("Pesquisar o id " + this.id + " onde " + possiblesSigns[this.idSearch]);
        }

        if(this.name != null){
            System.out.println("Pesquisar o nome " + this.name + " onde " + possiblesSigns[this.nameSearch]);
        }

        if(this.categories != null){
            System.out.println("Pesquisar a categoria " + this.categories[0] + " onde " + possiblesSigns[this.categorySearch]);
        }

        if(this.funding != -1){
            System.out.println("Pesquisar o funding " + this.funding + " onde  seja " + possiblesSigns[this.fundingSearch]);
        }


    }

}
