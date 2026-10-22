package com.example.cardgame;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;

    //constructor
    public Deck() {
        cards = new ArrayList<Card>();

        createDeck();
    }

    // membuat 1 set kartu yang terdiri dari 13 kartu AI, 13 kartu Security, 13 kartu SE dan 13 kartu TIK
    private void createDeck() {
        cards.clear();

        for(Suit suit : Suit.values()) {
            for(int rank = 1; rank <= 13; rank++) {
                cards.add(
                    new Card( suit, rank )
                );
            }
        }
    }

    // mengocok kartu
    public void shuffle() {
        Collections.shuffle( cards );
    }

    // membagikan kartu
    public Card drawCard() {
        if(cards.isEmpty()) {
            return null;
        }

        return cards.remove(0);
    }

    // fungsi testing apakah tumpukan kartu (deck) sudah kosong
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    // fungsi untuk menghitung sisa kartu dalam tumpukan
    public int getRemainingCards() {
        return cards.size();
    }
}
