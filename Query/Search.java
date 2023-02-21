package Query;

import java.sql.Date;

// SELECT n>0|* id>3 AND value=2 AND x=abc def
public class Search extends Query {
    int id;
    String name;
    String[] categories;
    float funding;
    Date created_at;

    public Search(String text) {
        String quantity = text.split(" ", 2)[0];
        String query = text.split(" ", 2)[1];

        getQuantitySelected(quantity);
        checkEveryQuery(query.split(" AND "));
    }

    /**
     * Split the query and treat every information from it
     */
    public void checkEveryQuery(String[] queryStr) {
        for (String query : queryStr) {
            checkQueryMathSign(query);
        }

    }

}
