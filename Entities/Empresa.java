package Entities;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import Query.Search;

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

    public Empresa(String nome, String[] categories, float funding) {
        this.id = id;
        this.nome = nome;
        this.categories = categories;
        this.funding = funding;
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
    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public void setFunding(float funding) {
        this.funding = funding;
    }

    /** DEBUG ONLY */
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

    /** Converts attributes to byteArray */
    public byte[] toByteArr() throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        DataOutputStream byteData = new DataOutputStream(byteOutput);

        byteData.writeInt(id);
        byteData.writeFloat(funding);
        byteData.writeLong(this.getCreatedAtAsLong());
        byteData.writeInt(nome.getBytes(Charset.forName("UTF-8")).length);
        byteData.writeUTF(nome);
        byteData.writeInt(categories.length);
        for (String category : categories) {
            byteData.writeInt(category.getBytes(Charset.forName("UTF-8")).length);
            byteData.writeUTF(category);
        }

        return byteOutput.toByteArray();
    }

    /** Converts byteArray to attribute */
    public void fromByteArr(byte[] byteArr) throws IOException {
        ByteArrayInputStream bytesInput = new ByteArrayInputStream(byteArr);
        DataInputStream byteData = new DataInputStream(bytesInput);

        /** Metadados */
        int byteLenght = byteData.readInt();
        boolean valid = byteData.readBoolean();

        /** Object info */
        this.id = byteData.readInt();
        this.funding = byteData.readFloat();
        this.created_at = new Date(byteData.readLong());

        int nameLength = byteData.readInt();
        this.nome = byteData.readUTF();

        int categoriesLength = byteData.readInt();
        this.categories = new String[categoriesLength];

        for (int i = 0; i < categoriesLength; i++) {
            // Read current lenght of this category
            byteData.readInt();
            this.categories[i] = byteData.readUTF();
        }
    }

    /** Merge data between updated and outdated Entity version */
    public void mergeData(Empresa newData) {
        if (newData.nome != null)
            setNome(newData.nome);

        if (newData.funding != -1)
            setFunding(newData.funding);

        if (newData.categories != null)
            setCategories(newData.categories);
    }

    /** 
     * Compare Byte sizes length with another empresa
     * 
     * @return int > 0 if this one is bigger
     * = 0 if it's the same size
     *  < 0 if this one is smaller
     */
    public int compareByteSizeWithOtherEmpresa(Empresa otherEmpresa) throws IOException {

        int thisEmpresaSize = this.toByteArr().length;
        int otherEmpresaSize = otherEmpresa.toByteArr().length;
        
        return thisEmpresaSize - otherEmpresaSize;
    }



    /**
     * Compare with the Seach query
     * 
     * @return true if it's a match searh
     * false if it's not a match 
     */
    public boolean matchWithSearchQuery(Search query){
        // Comparar o id

        if(query.id != -1){
            if(!compareIDBySign(query.idSearch, query.id)) return false;
        }

        if(query.name != null){
            if(!compareNameBySign(query.nameSearch, query.name)) return false;
        }

        if(query.categories != null){
            if(!compareCategoryBySign(query.categorySearch, query.categories)) return false;
        }

        if(query.funding != -1){
            if(!compareFundingBySign(query.fundingSearch, query.funding)) return false;
        }

        return true;
    }

    public boolean compareIDBySign(int sign, int compare_id) {
        // > = < 

        if(sign == 0){
            if(id > compare_id) return true;
        } else if (sign == 1){
            if(id == compare_id) return true;
        } else if (sign == 2 ){
            if(id < compare_id) return true;
        }

        return false;
    }

    public boolean compareFundingBySign(int sign, float compare_funding) {
        // > = < 

        if(sign == 0){
            if(this.funding > compare_funding) return true;
        } else if (sign == 1){
            if(funding == compare_funding) return true;
        } else if (sign == 2 ){
            if(funding < compare_funding) return true;
        }

        return false;
    }



    public boolean compareNameBySign(int sign, String compare_name){
        // < = > 
        if(sign == 0){
            if(this.nome.compareTo(compare_name) > 0) return true;
        } else if (sign == 1){
            if(nome.toLowerCase().equals(compare_name.toLowerCase())) return true;
        } else if (sign == 2){
            if(this.nome.compareTo(compare_name) < 0) return true;
        }

        return false;
    }


    public boolean compareCategoryBySign(int sign, String[] compareCategory){
        // Basicamente, ver se a quantidade de categorias eh maior ou menor ou sao as mesmas
        
        if(sign == 0){
            if(categories.length > compareCategory.length) return true;
        } else if (sign == 1){
            for(String compareString: compareCategory){
                boolean valid = false;

                for(String currentString: this.categories){
                    if(compareString.equals(currentString)){
                        valid = true;
                    }
                }

                if(valid == false) return false;
            }
            return true;
        } else if (sign == 2 ){
            if(categories.length < compareCategory.length) return true;
        }


        return false;
    }


}
