// created by Benjamin Lamprecht 2020

import java.util.ArrayList;
import java.util.Collections;

class Controller {

    //Input for tricks
    ArrayList<PlayerCard> playerCards = new ArrayList<>();
    Deck deck = new Deck();
    //collecting players
    private final ArrayList<Player> players = new ArrayList<>();
    private boolean gameRuns = true;
    protected ArrayList<Player> teamOne = new ArrayList<>();
    protected ArrayList<Player> teamTwo = new ArrayList<>();

    Controller() {
        deck.dealDeck();
        //creation of Player1
        Player p1 = new HumanPlayer(this);
        players.add(p1);
        createOtherPlayer();
        dealCards();

        System.out.println(Fonts.BLUE_BOLD + "Trump card is: " + deck.getTrump().getName() + Fonts.RESET);
        System.out.println();
    }

    protected boolean isNoCardsInHand(){
        for (Player p : players) {
            if (p.getCardsInHand().size() == 0) {
                return true;
            }
        }
        return false;
    }

    Deck getDeck() {
        return deck;
    }

    boolean isGameRuns() {
        return gameRuns;
    }

    ArrayList<PlayerCard> getPorts() {
        return playerCards;
    }

    ArrayList<Player> getPlayers() {
        return players;
    }

    private void gameRuns() {
        this.gameRuns = false;
    }

    void addScore(Player player) {
        int newScore = player.getScore();
        for (PlayerCard p : playerCards) {
            newScore = newScore + p.getCard().getValue();
        }
        player.setScore(newScore);
    }

    void printCardsOnTable() {
        System.out.println(Fonts.YELLOW_BOLD + "On the Table: " + Fonts.RESET);
        for (PlayerCard p : playerCards) {
            System.out.println(Fonts.YELLOW_BOLD + p.getCard().getName() + Fonts.RESET);
        }
    }

    Player turnSwitcher(Player player) {
        System.out.println(Fonts.BLUE_BOLD + "Turn ended\n\n" + Fonts.RESET);

        int i = 1;
        if (playerCards.size() > 0) {
            for (Player p : players) {
                if (p.equals(playerCards.get(playerCards.size() - 1).getPlayer())) {
                    if (i < players.size()) {
                        return players.get(i);
                    } else {
                        return players.get(0);
                    }
                }
                i++;
            }
        }
        return player;
    }

    private void NPCsCountCards() {
        for (Player player : players) {
            for (PlayerCard playerCard : playerCards) {
            player.countCards(playerCard);
            }
        }
    }

    protected Player tricks() {
        NPCsCountCards();
        //System.out.println(Fonts.RED_BOLD + "started tricks" + Fonts.RESET);
        PlayerCard temp;
        PlayerCard winCard = playerCards.get(0);
        for (PlayerCard playerCard : playerCards) {
            temp = conditionsToTrickCard(winCard, playerCard);
            winCard = temp;
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        //System.out.println(winCard.getCard().getName());
        System.out.println(Fonts.RED_BOLD + winCard.getPlayer().getName() + " won cards: " + Fonts.RESET);
        //printCardsOnTable();
        return winCard.getPlayer();
    }

    protected void endingGame(Player p) {
        gameRuns();
        Player winPlayer = p;
        for (Player temp : players) {
            winPlayer = searchWinner(temp, winPlayer);
        }
        announceWinner(winPlayer);
        System.exit(0);
    }

    private PlayerCard conditionsToTrickCard(PlayerCard master, PlayerCard slave) {
        boolean checkIfSlaveIsHigher = slave.getCard().getValue() > master.getCard().getValue();
        boolean checkIfSlaveMatchesColor = slave.getCard().getColor().equals(master.getCard().getColor());
        boolean checkIfSlaveIsTrump = slave.getCard().getColor().equals(deck.getTrumpColor());
        boolean checkIfMasterIsTrump = master.getCard().getColor().equals(deck.getTrumpColor());

        if ((checkIfSlaveIsHigher && checkIfSlaveMatchesColor) || (checkIfSlaveIsTrump && !checkIfMasterIsTrump)) {
            master = slave;
        }
        return master;
    }

    private Player searchWinner(Player p, Player winPlayer) {

        if (p.getScore() > winPlayer.getScore()) {
            return p;
        }
        return winPlayer;
    }

    private void announceWinner(Player winPlayer) {
        if (winPlayer.getScore() < 33) {
            System.out.println(Fonts.BLUE_BOLD + "Player " + winPlayer.getName() + " called too early and lost the game" +
                    '\n' + "Your Score: " + winPlayer.getScore());
        } else {
            System.out.println(Fonts.PURPLE_BOLD + "\nPlayer " + winPlayer.getName() + " wins!");
            System.out.println("Points: " + winPlayer.getScore() + Fonts.RESET);
        }
        System.out.println("Others score(s):");
        for (Player p : players) {
            if (!p.getName().equals(winPlayer.getName())) {
                System.out.println(Fonts.BLACK_BOLD + p.getName() + ", score: " + p.getScore() + Fonts.RESET);
            }
        }
    }

    Boolean checkColour(Card card) {
        boolean checkIfCardMatchesColor = card.getColor().equals(playerCards.get(0).getCard().getColor());
        boolean checkIfCardIsTrump = card.getColor().equals(deck.getTrumpColor());

        return checkIfCardIsTrump || checkIfCardMatchesColor;
    }

    private void createOtherPlayer() {
            players.add(new NPC( this));

        Collections.shuffle(players);

        for (int i = 0; i < players.size(); i++) {
            if (i / 2 != 0) {
                teamOne.add(players.get(i));
            } else {
                teamTwo.add(players.get(i));
            }
        }
    }

    private void dealCards() {

        for (Player p : players) {
            ArrayList<Card> tempDeck = new ArrayList<>(deck.getDeck());

            ArrayList<Card> tempHand = new ArrayList<>();

            for (Card c : tempDeck) {

                if (tempHand.size() < 5) {
                    tempHand.add(c);
                    deck.getDeck().remove(c);
                }
                p.setCardsInHand(tempHand);
            }
        }
    }
}