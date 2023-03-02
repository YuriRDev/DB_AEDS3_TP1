package Query;

public class Delete extends Query {
    int id;

    public Delete(String query) {
        int parsedInt = parseInt(query);

        if(parsedInt <= 0) throw new Error("Error, ID must be > 0, received " + query);
        this.id = parsedInt;
    }   

    public int getId() {
        return this.id;
    }

}
