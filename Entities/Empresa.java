package Entities;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.FlowView;

public class Empresa {
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

    /** Getters */
    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public float getFunding() {
        return this.funding;
    }

    public Date getCreatedAt() {
        return this.created_at;
    }
    
    public String[] getCategories() {
        return this.categories;
    }

    public long getCreatedAtAsLong() {
        return created_at.getTime();
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

    public void saveToDB() {
        // Save all the info to the DB folder
    }

    public int getSizeOfObject() {
        int sizeOfId = 4;
        int sizeOfFunding = 4;
        int sizeOfCreatedAt = 8;

        // Cada char ocupa 2 bytes + 4 bytes de inteiro para indicar o tamanho da string
        int sizeOfNome = (nome.length() * 2) + 4;

        // Size da quantidade de categorias
        int sizeOfCategoriesLengh = 4;

        int sizeOfCategories = 0;
        for (String categoria : categories) {
            // Cada char ocupa 2 bytes + 4 bytes de inteiro para indicar o tamanho da string
            sizeOfCategories += (categoria.length() * 2) + 4;
        }

        return sizeOfId + sizeOfFunding + sizeOfCreatedAt + sizeOfNome + sizeOfCategoriesLengh + sizeOfCategories;

    }

}
