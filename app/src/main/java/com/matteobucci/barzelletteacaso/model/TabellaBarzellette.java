package com.matteobucci.barzelletteacaso.model;

/**
 * Created by matteo on 04/02/16.
 */
public class TabellaBarzellette {

    private String nome;
    private int numeroBarzellette;
    private int id;
    private String descrizione = "";
    private String nomeVisualizzato = "";

    public String getNomeVisualizzato() {
        return nomeVisualizzato;
    }

    public void setNomeVisualizzato(String nomeVisualizzato) {
        this.nomeVisualizzato = nomeVisualizzato;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }



    public TabellaBarzellette(String nome) {
        this.nome = nome;
    }

    public TabellaBarzellette(String nome, int id, int numeroBarzellette) {
        this.nome = nome;
        this.id = id;
        this.numeroBarzellette = numeroBarzellette;
    }

    public TabellaBarzellette(String nome, int id, int numeroBarzellette, String descrizione, String nomeVisualizzato) {
        this.nome = nome;
        this.id = id;
        this.numeroBarzellette = numeroBarzellette;
        this.descrizione = descrizione;
        this.nomeVisualizzato = nomeVisualizzato;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumeroBarzellette() {
        return numeroBarzellette;
    }

    public void setNumeroBarzellette(int numeroBarzellette) {
        this.numeroBarzellette = numeroBarzellette;
    }

    public String getNome(){return this.nome;}

}
