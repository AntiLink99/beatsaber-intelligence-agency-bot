package bot.utils;

import bot.main.BotConstants;

import java.util.ArrayList;
import java.util.List;

public class ListValueUtils {

    public static int findLowestSurpassedValue(int valueToCheck, Integer[] values) {
        for (int milestone : values) {
            if (valueToCheck <= milestone) {
                return milestone;
            }
        }
        return -1;
    }

    public static int findMilestoneForRank(int rank) {
        return ListValueUtils.findLowestSurpassedValue(rank, BotConstants.rankMilestones);
    }

    public static int findHighestSurpassedValue(float valueToCheck, Integer[] values) {
        if (valueToCheck < values[0]) {
            return -1;
        }

        if (valueToCheck > values[values.length - 1]) {
            return values[values.length - 1];
        }

        for (int i = 0; values.length > i; i++) {
            if (valueToCheck < values[i] && i != 0) {
                return values[i - 1];
            }
        }
        return -1;
    }

    public static List<Integer> addElementReturnList(List<Integer> list, int elem) {
        List<Integer> newList = new ArrayList<>(list);
        newList.add(elem);
        return newList;
    }
}
