package it.polimi.ingsw.Constants;

import it.polimi.ingsw.Model.Cloud;
import it.polimi.ingsw.Model.Islands.Island;
import it.polimi.ingsw.Model.Islands.IslandContainer;
import it.polimi.ingsw.Model.Islands.SuperIsland;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Printable {

    static final String ANSI_BLACK = "\u001B[30m";
    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_YELLOW = "\u001B[33m";
    static final String ANSI_BLUE = "\u001B[34m";
    static final String ANSI_PINK = "\u001B[35m";
    static final String RESET = "\u001B[0m";
    /* BRIGHT RED */
    static final String ANSI_BRED = "\u001B[91m";
    static final String ANSI_WHITE = "\u001B[37m";
    static final String ANSI_GREY = "\u001B[90m";

    static final String emptyRow = "                     ";
    static final String space = "   ";


    public static void printBoard(IslandContainer islandContainer, List<Cloud> clouds, int mn) {
        String[] island = {" ▄█████████████████▄ ",
                "██  " + ANSI_RED + "█" + RESET + ":%02d     " + ANSI_YELLOW + "█" + RESET + ":%02d  ██",
                "██  " + ANSI_BLUE + "█" + RESET + ":%02d     " + ANSI_GREEN + "█" + RESET + ":%02d  ██",
                "██  " + ANSI_PINK + "█" + RESET + ":%02d     " + "      ██",
                "██                 ██",
                "██   " + ANSI_BRED + "%s" + RESET + "             ██",
                "██  " + ANSI_BRED + "%s" + RESET + "            ██",
                " ▀█████████████████▀ ",
        };
        String[] cloud = {
                ""
        };
        String[][] islands = prepareIslands(island, islandContainer, mn);
        topRow(islands);
        topMidRow(islands);
        bottomMidRow(islands);
        bottomRow(islands);
    }


    private static void topRow(String[][] islands) {
        for (int i = 0; i < islands[0].length; i++) {
            System.out.print(space);
            System.out.print(islands[0][i]);
            System.out.print(space);
            System.out.print(islands[1][i]);
            System.out.print(space);
            System.out.print(islands[2][i]);
            System.out.print(space);
            System.out.print(islands[3][i]);
            System.out.println();
        }
        System.out.println();
    }

    private static void topMidRow(String[][] islands) {
        for (int i = 0; i < islands[0].length; i++) {
            System.out.print(space);
            System.out.print(islands[11][i]);
            System.out.print(space + space + space + emptyRow + emptyRow);
            System.out.print(islands[4][i]);
            System.out.println();
        }
        System.out.println();
    }

    private static void bottomMidRow(String[][] islands) {
        for (int i = 0; i < islands[0].length; i++) {
            System.out.print(space);
            System.out.print(islands[10][i]);
            System.out.print(space + space + space + emptyRow + emptyRow);
            System.out.print(islands[5][i]);
            System.out.println();
        }
        System.out.println();
    }

    private static void bottomRow(String[][] islands) {
        for (int i = 0; i < islands[0].length; i++) {
            System.out.print(space);
            System.out.print(islands[9][i]);
            System.out.print(space);
            System.out.print(islands[8][i]);
            System.out.print(space);
            System.out.print(islands[7][i]);
            System.out.print(space);
            System.out.print(islands[6][i]);
            System.out.println();
        }
        System.out.println();
    }

    private static String[] islandFormat(boolean isMerged, boolean motherNature, String pIsland[], Map<Color, Integer> map) {
        String[] island = Arrays.copyOf(pIsland, pIsland.length);
        String s = "   ";
        if (isMerged) {
            for (int j = 0; j < island.length; j++) {
                island[j] = emptyRow;
            }
        } else {
            island[1] = String.format(island[1], map.getOrDefault(Color.RED, 0), map.getOrDefault(Color.YELLOW, 0));
            island[2] = String.format(island[2], map.getOrDefault(Color.BLUE, 0), map.getOrDefault(Color.GREEN, 0));
            island[3] = String.format(island[3], map.getOrDefault(Color.PINK, 0));
            if (motherNature) {
                island[5] = String.format(island[5], "█");
                island[6] = String.format(island[6], "███");
            } else {
                island[5] = String.format(island[5], " ");
                island[6] = String.format(island[6], s);
            }
        }
        return island;
    }

    private static String[][] prepareIslands(String[] island, IslandContainer islandContainer, int mn) {
        String[][] stringIslands = new String[12][];
        int i = 0;
        boolean motherNature;
        while (i < 12) {
            motherNature = false;
            Island isl = islandContainer.get(i);
            if (mn == i) {
                motherNature = true;
            }
            if (isl instanceof SuperIsland) {
                int n = isl.size(); // 3 isole, i = 4
                stringIslands[i] = islandFormat(false, motherNature, island, isl.getStudents());
                for (int j = 0; j < n - 1; j++) {
                    i++;
                    stringIslands[i] = islandFormat(true, false, island, isl.getStudents());
                }
            } else {
                stringIslands[i] = islandFormat(false, motherNature, island, isl.getStudents());
            }
            i++;
        }
        return stringIslands;
    }

    private static String getOwnerColor(Island island, Map<String, TowerColor> map) {
        String s = "    ";
        String tower = "♜";
        if (island.getOwner() == null) {
            return s;
        } else {
            switch (map.get(island.getOwner())) {
                case TowerColor.BLACK -> s = ANSI_BLACK + "" + RESET + ":%01d";
                case TowerColor.WHITE -> s = ANSI_WHITE + "" + RESET + ":%01d";
                case TowerColor.GRAY -> s= ANSI_GREY + "" + RESET + ":%01d";
            }
        }
    }
}