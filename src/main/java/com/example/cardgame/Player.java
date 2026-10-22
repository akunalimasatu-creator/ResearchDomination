package com.example.cardgame;

import java.util.ArrayList;

public class Player {
    private int playerId;
    private int score;
    private ArrayList<Card> hand;
    private ResearchBoard researchBoard;
    private PublicationBoard publicationBoard;


    // constructor
    public Player(int playerId) {
        this.playerId = playerId;
        this.score = 0;
        this.hand = new ArrayList<Card>();

        researchBoard = new ResearchBoard();
        publicationBoard = new PublicationBoard();
    }

    // ambil ID Player
    public int getPlayerId() {
        return playerId;
    }

    // ambil score
    public int getScore() {
        return score;
    }

    // ambil informasi jumlah kartu di tangan
    public int getHandSize() {
        return hand.size();
    }

    // ambil kartu-kartu di tangan
    public ArrayList<Card> getHand() {
        return hand;
    }

    // tambahkan score
    public void addScore(int points) {
        score += points;
    }

    // assign score
    public void setScore(int score) {
        this.score = score;
    }


    // testing apakah kartu di tangan sudah 6 kartu atau lebih
    // kalau belum, tambahkan 1 kartu ke tangan
    public boolean addCard(Card card) {
        if(hand.size() >= 6) {
            return false;
        }

        hand.add(card);

        return true;
    }

    // hilangkan 1 kartu dari tangan
    public Card removeCard(int index) {
        if(index < 0 || index >= hand.size()) {
            return null;
        }

        return hand.remove(index);
    }

    // hilankan 1 kartu dengen referensi kartu itu sendiri
    public Card removeCard(Card card) {
        if (hand.contains(card)) {
            hand.remove(card);
            return card; // Berhasil menghapus dan mengembalikan kartu tersebut
        }
        return null; // Kartu tidak ditemukan di tangan
    }

    // ambil kartu yang ke- index
    public Card getCard(int index) {
        if(index < 0 || index >= hand.size()) {
            return null;
        }

        return hand.get(index);
    }

    // clear kartu di tangan
    public void clearHand() {
        hand.clear();
    }

    // testing apakah kartu di tangan 6 atau lebih
    public boolean isHandFull() {
        return hand.size() >= 6;
    }

    // testing apakah di tangan tidak ada kartu lagi
    public boolean isHandEmpty() {
        return hand.isEmpty();
    }

    // menampilkan kartu kartu di tangan --> tuk debugging dan testing
    public void showHand() {
        System.out.println( "\nPlayer #" + (playerId + 1) + " Hand" );

        for(int i = 0; i < hand.size(); i++) {
            System.out.println( "[" + i + "] " + hand.get(i) );
        }
    }

    // menampilkan informasi seorang pemain
    public void showPlayerInfo() {
        System.out.println( "\nPlayer #" + (playerId + 1) );

        System.out.println( "Score : " + score );

        System.out.println( "Hand  : " + hand.size() + " cards" );
    }

    // mengambil tumpukan penelitian
    public ResearchBoard getResearchBoard() {
        return researchBoard;
    }

    // menagambil tumpukan publikasi
    public PublicationBoard getPublicationBoard() {
        return publicationBoard;
    }

    public boolean hasValidResearchStackForPublication() {
        boolean valid = false;
        ResearchBoard researchboard = getResearchBoard();
        for (Suit suit : Suit.values()) {
            if( researchboard.canPublish(suit) ) {
                valid = true;
            }
        }
        return valid;
    }

    public ArrayList<Suit> getValidResearch() {
        ArrayList<Suit> validResearch = new ArrayList<Suit>();
        ResearchBoard researchboard = getResearchBoard();
        for (Suit suit : Suit.values()) {
            if( researchboard.canPublish(suit) ) {
                validResearch.add(suit);
            }
        }
        return validResearch;
    }

    public ArrayList<Suit> researchExists() {
        ArrayList<Suit> validResearch = new ArrayList<Suit>();
        ResearchBoard researchboard = getResearchBoard();
        for (Suit suit : Suit.values()) {
            if( !researchboard.isEmpty(suit) ) {
                validResearch.add(suit);
            }
        }
        return validResearch;
    }

    @Override
    public String toString()
    {
        return "Player #" + (playerId + 1) + " Score=" + score + " Hand=" + hand.size();
    }
}