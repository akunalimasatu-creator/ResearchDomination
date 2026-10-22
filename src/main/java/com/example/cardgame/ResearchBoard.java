package com.example.cardgame;

import java.util.ArrayList;

public class ResearchBoard {
    private ArrayList<Card> ai;
    private ArrayList<Card> security;
    private ArrayList<Card> se;
    private ArrayList<Card> tik;

    // constructor
    public ResearchBoard() {
        ai = new ArrayList<Card>();
        security = new ArrayList<Card>();
        se = new ArrayList<Card>();
        tik = new ArrayList<Card>();
    }

    // tambahkan sebuah kartu card dengan daun (suit) yang sesuai ke tumpukan research
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

    // tampilkan kartu-kartu dari research sesuai dengan daun (suit) yang diminta
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

    // hitung jumlah kartu dari research sesuai dengan daun (suit) yang diminta
    public int getCount(Suit suit) {
        ArrayList<Card> pile = getPile(suit);

        if(pile == null) {
            return 0;
        }

        return pile.size();
    }

    // tampilkan jumlah kartu dari seluruh research
    public int getTotalCards() {
        return ai.size()
            + security.size()
            + se.size()
            + tik.size();
    }

    // ambil satu kartu teratas dari daun (suit) uyang diminta --> ini dipakai pada operasi menghambat persaingan
    public Card removeTopCard(Suit suit) {
        ArrayList<Card> pile = getPile(suit);

        if(pile == null || pile.isEmpty()) {
            return null;
        }

        return pile.remove(pile.size() - 1);
    }

    // kosongkan tumpukan kartu dari daun (suit) yang diminta --> ini dipakai ketika pemain melakukan publikasi
    public ArrayList<Card> removePile(Suit suit) {
        ArrayList<Card> oldPile = getPile(suit);

        if(oldPile == null) {
            return null;
        }

        ArrayList<Card> publishedPile = new ArrayList<Card>(oldPile);

        oldPile.clear();

        return publishedPile;
    }

    // apakah tumpukan kartu dari daun (suit) yang diminta kosong?
    public boolean isEmpty(Suit suit) {
        return getCount(suit) == 0;
    }

    // memeriksa apakah suatu daun (suit) sudah memenuhi syarat tuk publikasi (syaratnya khan minimal ada 2 kartu di research)
    public boolean canPublish(Suit suit) {
        return getCount(suit) >= 2;
    }
}