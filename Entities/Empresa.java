package Entities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.WriteAbortedException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
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

    // // Convert attributes to byte array
    public byte[] toByteArr() throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        DataOutputStream byteData = new DataOutputStream(byteOutput);

        byteData.writeInt(id);
        byteData.writeFloat(funding);
        byteData.writeLong(this.getCreatedAtAsLong());
        byteData.writeInt(nome.getBytes(Charset.forName("UTF-8")).length);
        byteData.writeUTF(nome);
        byteData.writeInt(categories.length);
        for(String category : categories) {
            byteData.writeInt(category.getBytes(Charset.forName("UTF-8")).length);
            byteData.writeUTF(category);
        }

        return byteOutput.toByteArray();    
    }

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

        for(int i =0; i< categoriesLength; i++){
            // Read current lenght of this category
            byteData.readInt();
            this.categories[i] = byteData.readUTF();
        }
    }
}
