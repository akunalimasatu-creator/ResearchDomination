package com.example.cardgame;

public class Card
{
    private Suit suit;
    private int rank;

    // ini adalah constructor yang dipanggil ketika membuat decks (tumpukan kartu)
    public Card( Suit suit, int rank ) {
        this.suit = suit;
        this.rank = rank;
    }

    // tuk mengambil nilai sebuah daun (suit)
    public Suit getSuit() {
        return suit;
    }

    // tuk mengambil nilai. keluaran dalam tipe integer
    public int getRank() {
        return rank;
    }
}