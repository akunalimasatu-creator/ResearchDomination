package com.example.cardgame;

import java.util.ArrayList;

public class DiscardPile {
    private ArrayList<Card> cards;

    // constructor
    public DiscardPile() {
        cards = new ArrayList<Card>();
    }

    // menambahkan kartu ke tumpukan teratas dari tumpukan buangan (discarded card)
    public void addCard(Card card) {
        if(card != null) {
            cards.add(card);
        }
    }

    // mengeluarkan jumlah kartu di tumpukan buangan --> ini berguna tuk debugging
    public int size() {
        return cards.size();
    }

    // check apakah tumpukan kartu buangan kosong --> ini juga tuk debugging
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    // menampilkan kartu ke-index dari kartu buangan
    public Card getCard(int index) {
        if(index < 0 || index >= cards.size()) {
            return null;
        }

        return cards.get(index);
    }

    // mengosongkan tumpukan kartu buangan
    public void clear() {
        cards.clear();
    }

    @Override
    public String toString() {
        return cards.toString();
    }
}