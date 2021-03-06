// created by Benjamin Lamprecht 2020

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

class Deck {
    private Card trump;
    private String trumpColor;
    private final ArrayList<Card> deck;

    public Deck() {
        deck = collectDeck(new ArrayList<Card>());
    }

    protected void dealDeck (){
        Collections.shuffle(deck);
        trump = trump(deck);
        trumpColor = trump.getColor();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void blockDeck() {
        ArrayList<Card> tempDeck = new ArrayList<>(deck);
        for (Card c : tempDeck) {
            deck.remove(c);
        }
    }

    private Card trump(ArrayList<Card> deck) {
        Card card = deck.get(0);
        deck.remove(card);
        return card;
    }

    public ArrayList<Card> collectDeck(ArrayList<Card> deck) {
        String DB_DECK = "DoppelDeutscheKarten.db";
        String CONNECTION_STRING = "jdbc:sqlite::resource:" + DB_DECK;
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(CONNECTION_STRING);

            try (Statement statement = conn.createStatement()) {

                ResultSet results = statement.executeQuery("SELECT * FROM Deck ORDER BY ROWID ASC");
                while (results.next()) {
                    Card card = new Card(results.getString("NAME"), results.getString("COLOR"), 0);
                    String str = results.getString("VALUE");
                    switch (str){
                        case "ass":
                            card.setValue(11);
                            break;
                        case  "zehn":
                            card.setValue(10);
                            break;
                        case  "könig":
                            card.setValue(4);
                            break;
                        case  "dame":
                            card.setValue(3);
                            break;
                        case  "bube":
                            card.setValue(2);
                            break;
                        default:
                            card.setValue(0);
                            break;
                    }
                    if (card.getValue() > 0) {
                        deck.add(card);
                    }
                }

                statement.close();
                conn.close();

            } catch (SQLException e) {
                System.out.println("List not generated: " + e.getMessage());
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Couldn't connect to db: " + e.getMessage());
        }

        return deck;
    }

    public Card getTrump() {
        return trump;
    }

    public String getTrumpColor() {
        return trumpColor;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setTrump(Card trump) {
        this.trump = trump;
    }
}
