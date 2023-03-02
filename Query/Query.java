package Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Query {
    public static String[] attributes = { "id", "nome", "categories", "funding", "created_at", "updated_at" };
    public static String[] creatingAttributes = { "nome", "categories", "funding" };
    public static String[] possiblesSigns = { ">", "=", "<" };

    /**
     * Check if the attribute is in it's valid type
     * 
     * @param attribute must include the name and it's value
     */
    public void checkAttributeType(String attribute, String splitSign) {
        String currentAttribute = attribute.split(splitSign)[0];
        String attributeValue = attribute.split(splitSign)[1];
        switch (currentAttribute) {
            case "id":
                parseInt(attributeValue);
                break;
            case "funding":
                parseFloat(attributeValue);
                break;
            case "created_at":
                parseDate(attributeValue);
                break;
        }
    }

    /**
     * Check if has used any possibleSign
     * 
     * @param query must include >, = or <
     */
    public String checkQueryMathSign(String query) {
        /* Parse */
        for (String sign : possiblesSigns)
            if (query.contains(sign)) {
                return sign;
            }

        throw new Error(
                "Error while parsing condition (" + query + "). Invalid sign, expected >, >=, =, <=, <.");
    }

    /**
     * Search for the equal sign on the creation
     * 
     * @param text <i>attribute</i> <b>=</b> value
     */
    public void checkAttributeEqualSign(String text) {
        if (!text.contains("=")) {
            throw new Error("Error while creating entity. (" + text + ") must include the equal sign while creating");
        }
    }

    /**
     * Return the numeric value of * or n>0
     * 
     * @param text n>0 or *
     */
    public int getQuantitySelected(String text) {
        if (text.equals("*"))
            return -1;

        try {
            int numericValue = Integer.parseInt(text);
            if (numericValue <= 0)
                throw new Error("Could not parse the quantity for selection. Received " + text
                        + " expected: number greather than 0");
            return numericValue;
        } catch (NumberFormatException n) {
            throw new Error(
                    "Could not parse the quantity for selection. Received: " + text + " expected: * or a number > 0");
        }
    }

    /**
     * Check if attribute exist
     * 
     * @param attribute one of: "id", "nome", "categories", "funding", "created_at", "updated_at"
     */

    public String checkIfAttributeExists(String text, String splitSign) {
        String attribute = text.split(splitSign)[0];

        for (String s : attributes) {
            if(attribute.equals(s)) {
                return s;
            }
        }

        throw new Error("Attribute not found. Received: " + attribute);
    }

    /**
     * Check if attribute exists for the creating/update query
     * 
     * @param attribute nome|categories or funding
     */
    public String checkIfAttributeExistsForCreation(String attribute) {
        for (String att : creatingAttributes) {
            if (att.equals(attribute))
                return attribute;
        }
        throw new Error("Error while reading (" + attribute + ") does not exist. Must be nome,categories or funding");
    }

    /**
     * Return the numeric value of the search Sign
     * 
     * @param searchSign must be >|=|<
     */
    public int getSearchSignValue(String searchSign){
        for(int i =0; i < possiblesSigns.length; i++){
            if(searchSign.equals(possiblesSigns[i])) return i;
        }

        throw new Error("Could not identify the search sign!, received: " + searchSign);

    }

    public int parseInt(String queryValue) {
        try {
            int returnValue = Integer.parseInt(queryValue);
            return returnValue;
        } catch (NumberFormatException e) {
            throw new Error("Error while parsing number, attribute  must be an integer");
        }
    }

    public float parseFloat(String queryValue) {
        try {
            float returnValue = Float.parseFloat(queryValue);
            return returnValue;
        } catch (NumberFormatException e) {
            throw new Error("Error while parsing float, attribute  must be a float");
        }
    }

    public void parseDate(String queryValue) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy", Locale.CANADA);
        } catch (NumberFormatException e) {
            throw new Error(
                    "Error while parsing Date, attribute  must be a Date type dd-mm-yyyy");
        }
    }


    /**
     * Prints the structure of the CREATE query
     */
    public void printHelpCreate() {

    }
}
