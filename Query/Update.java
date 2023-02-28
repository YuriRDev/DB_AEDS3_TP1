package Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Update {
    public String nome;
    public float funding;
    public String[] categories;

    public Update() {
        nome = null;
        funding = -1; // negative means no change
        categories = null;
    }

    public Update(String nome, float funding, String[] categories) {
        this.nome = nome;
        this.funding = funding;
        this.categories = categories;
    }

}
