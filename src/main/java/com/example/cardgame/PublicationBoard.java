package com.example.cardgame;

import java.util.ArrayList;

public class PublicationBoard {
    private ArrayList<Card> ai;
    private ArrayList<Card> security;
    private ArrayList<Card> se;
    private ArrayList<Card> tik;

    // constructor
    public PublicationBoard() {
        ai = new ArrayList<Card>();
        security = new ArrayList<Card>();
        se = new ArrayList<Card>();
        tik = new ArrayList<Card>();
    }

    // menambahkan 1 kartu ke tumpukan publikasi
    public void addCard(Card card) {
        if(card == null) {
            return;
        }

        switch(card.getSuit()) {
            case AI:
                ai.add(card);
                break;

            case SEC:
                security.add(card);
                break;

            case SE:
                se.add(card);
                break;

            case TCS:
                tik.add(card);
                break;
        }
    }

    // menambahkan tumpukan kartu (dari research) ke tumpukan publikasi
    public void addPile(ArrayList<Card> pile) {
        if(pile == null) {
            return;
        }

        for(Card card : pile) {
            addCard(card);
        }
    }

    // ambil tumpukan publikasi dari suatu daun (suit) terpilih
    public ArrayList<Card> getPile(Suit suit) {
        switch(suit) {
            case AI:
                return ai;

            case SEC:
                return security;

            case SE:
                return se;

            case TCS:
                return tik;

            default:
                return null;
        }
    }

    // hitung jumlah kartu dari tumpukan publikasi dari suatu daun (suit) terpilih
    public int getCount(Suit suit) {
        ArrayList<Card> pile = getPile(suit);

        if(pile == null) {
            return 0;
        }

        return pile.size();
    }

    // hitung total publikasi
    public int getTotalCards()
    {
        return ai.size()
             + security.size()
             + se.size()
             + tik.size();
    }

    // menghitung bonus skor jika semua daun (suit) ada publikasinya
    public boolean hasAllFields()
    {
        return (ai.size() > 0)
            && (security.size() > 0)
            && (se.size() > 0)
            && (tik.size() > 0);
    }

    @Override
    public String toString() {
        return "AI=" + ai.size()
             + ", SECURITY=" + security.size()
             + ", SE=" + se.size()
             + ", TIK=" + tik.size();
    }
}