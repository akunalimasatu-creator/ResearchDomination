package com.example.cardgame;

import java.util.ArrayList;
import java.util.List;

public class Literature {
    private static final int MAX_SIZE = 6;
    private ArrayList<Card> cards;

    // constructor
    public Literature() {
        cards = new ArrayList<Card>();
    }

    // menghitung jumlah kartu di literature
    public int size() {
        return cards.size();
    }

    // test apakah tumpukan literature kosong
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Mengembalikan daftar seluruh kartu yang ada di literatur.
     * Method ini digunakan oleh GUI (CenterBoardView) untuk merender kartu ke layar.
     */
    public List<Card> getCards() {
        return this.cards;
    }

    // tampilkan 1 kartu ke-index dari tumpukan literature
    public Card getCard(int index) {
        if(index < 0 || index >= cards.size()) {
            return null;
        }

        return cards.get(index);
    }

    // tambahkan 1 kartu ke tumpukan literature
    public void addCard(Card card) {
        cards.add(card);
    }

    // buang 1 kartu ke-index dari tumpukan literature
    public Card removeCard(int index) {
        if(index < 0 || index >= cards.size()) {
            return null;
        }

        return cards.remove(index);
    }

    // buang satu kartu dengan referensi kartu (dipakai pada saat user klik suatu kartu)
    public Card removeCardByClick(Card card) {
        if (cards == null || card == null) {
            return null;
        }
        int index = cards.indexOf(card);
        return removeCard(index);
    }

    // buang 1 kartu pertama dari tumpukan literature. Sebetulnya ini bisa diwakili dengan removeCard(0), hasilnya sama saja
    public Card removeOldestCard() {
        if(cards.isEmpty()) {
            return null;
        }

        return cards.remove(0);
    }

    // in case penambahan kartu ke tumpukan literature menghasilkan jumlah kartu lebih dari 6, maka kartu pertama harus dibuang
    public Card trimToMaxSize() {
        if(cards.size() > MAX_SIZE) {
            return removeOldestCard();
        }

        return null;
    }

    @Override
    public String toString()
    {
        return cards.toString();
    }
}