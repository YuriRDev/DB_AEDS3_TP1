package Query;

import Entities.Empresa;

public class Create extends Query {
    String name;
    String[] categories;
    float funding;

    public Create(String text) {
        checkUsedAttributes(text);
    };

    /**
     * Check if the create attributes exists and so if the type is valid
     */
    public void checkUsedAttributes(String text) {
        String[] attributesPassed = text.split(" AND ");
        for (String attribute : attributesPassed) {
            checkAttributeEqualSign(attribute);
            checkIfAttributeExistsForCreation(attribute.split("=")[0]);
            checkAttributeType(attribute, "=");
            setAttributesValueToClass(attribute.split("=")[0], attribute.split("=")[1]);
        }
    }


    /**
     * Set the value of the attribute to the class
     * 
     * @param attribute nome|categories or funding
     * @param value correponds to it's type
     */
    public void setAttributesValueToClass(String attribute, String value) {
        switch (attribute) {
            case "nome":
                this.name = value;
                break;
            case "funding":
                this.funding = Float.parseFloat(value);
                break;
            case "categories":
                this.categories = value.split("&&");
                break;
        }
    }


    public Empresa returnEmpresaCreated(){
        Empresa tmp = new Empresa();
        tmp.setNome(this.name);
        tmp.setCategories(this.categories);
        tmp.setFunding(this.funding);

        return tmp;
    }
}
