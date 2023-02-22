package Entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Empresa implements Serializable {
    int id;
    String nome;
    String[] categories;
    float funding;
    Date created_at;

    public Empresa() {
        this.id = -1;
        this.nome = "Non Setted";
        this.funding = 0;
        this.created_at = new Date();
    }

    public Empresa(int id, String nome, String[] categories, float funding) {
        this.id = id;
        this.nome = nome;
        this.categories = categories;
        this.funding = funding;
        this.created_at = new Date();
    }

    public Empresa(int id, String nome, String[] categories, float funding, Date created_at) {
        this.id = id;
        this.nome = nome;
        this.categories = categories;
        this.funding = funding;
        this.created_at = created_at;
    }

    /** Setters */
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public void setFunding(float funding) {
        this.funding = funding;
    }

    public void print() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        String formatDate = dateFormat.format(date);
        System.out.println("[" + id + "] " + nome);
        for (String s : categories)
            System.out.print(s + " ");

        System.out.println();
        System.out.println("Created_at: " + formatDate);
        System.out.println("Funding: " + funding);

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }

}


