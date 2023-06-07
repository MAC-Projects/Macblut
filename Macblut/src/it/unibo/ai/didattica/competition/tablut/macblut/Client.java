package it.unibo.ai.didattica.competition.tablut.macblut;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aima.core.search.adversarial.AlphaBetaSearch;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import aima.core.search.adversarial.MinimaxSearch;
import aima.core.search.framework.SearchAgent;
import com.google.gson.Gson;

import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.client.TablutRandomClient;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

/**
 *
 * @author Mattia Moffa, Valentino Cavallotti, Luca Andreetti
 *
 */
public class Client extends TablutClient {

    public Client(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
    }
    public Client(String player, int timeout, String ipAddress) throws UnknownHostException, IOException {
        this(player, "macblut", timeout, ipAddress);
    }
    public Client(String player) throws UnknownHostException, IOException {
        this(player, "macblut", 60, "localhost");
    }

    public static void printUsage() {
        System.out.println("Arguments: <role> [timeout] [ip address]");
        System.out.println("\t<role>: either WHITE or BLACK");
        System.out.println("\t[timeout]: expected timeout");
        System.out.println("\t[ip address]: server IP address");
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        String role = "";
        String name = "macblut";
        String ipAddress = "localhost";
        int timeout = 60;

        if (args.length < 1) {
            System.out.println("Error: missing role");
            printUsage();
            System.exit(-1);
        }

        role = (args[0]);

        if (args.length >= 2) {
            try {
                timeout = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Error: invalid timeout: must be an integer");
                printUsage();
                System.exit(-1);
            }
        }
        if (args.length == 3) {
            ipAddress = args[2];
        }
        if (args.length > 3) {
            System.out.println("Error: too many arguments");
            printUsage();
            System.exit(-1);
        }

        Client client = new Client(role, name, timeout, ipAddress);
        client.run();
    }

    @Override
    public void run() {
        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String whiteName, blackName;
        Turn player = getPlayer();
        if (player.equals(Turn.WHITE)) {
            whiteName = "Macblut";
            blackName = "Rivale";
        } else {
            whiteName = "Rivale";
            blackName = "Macblut";
        }

        MacblutGame aimaGame = new MacblutGame();

        // One iteration per turn
        while (true) {
            try {
                // Read state from server
                this.read();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }

            State state = getCurrentState();

            System.out.println("Current state:");
            System.out.println(state);

            if (player.equals(state.getTurn())) {
                int timeout = this.getTimeout();
                if (timeout < 4) {
                    timeout = timeout / 2;
                } else {
                    timeout -= 3;
                }
                //IterativeDeepeningAlphaBetaSearch<State, Action, Turn> search = new IterativeDeepeningAlphaBetaSearch<>(aimaGame, 0, 1, timeout);
                Search search = new Search(aimaGame, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, timeout);
                Action action = search.makeDecision(state);

                System.out.println(action);

                try {
                    this.write(action);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (state.getTurn() == Turn.DRAW) {
                    System.out.println("--- PAREGGIO ---");
                    break;
                } else if (state.getTurn() == Turn.WHITEWIN) {
                    if (player == Turn.WHITE) {
                        System.out.println("--- VITTORIA DA BIANCO ---");
                    } else {
                        System.out.println("--- SCONFITTA DA NERO ---");
                    }
                    break;
                } else if (state.getTurn() == Turn.BLACKWIN) {
                    if (player == Turn.BLACK) {
                        System.out.println("--- VITTORIA DA NERO ---");
                    } else {
                        System.out.println("--- SCONFITTA DA BIANCO ---");
                    }
                    break;
                } else {
                    System.out.println("Other player's turn");
                }
            }
            System.out.println("--- Fine turno ---");
        }
    }
}
