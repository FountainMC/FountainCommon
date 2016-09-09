package org.fountainmc.common.utils;

import org.fountainmc.api.entity.data.PlayerData;

public final class ExperienceMath {
    private ExperienceMath() {
    }

    public static void calculateExperience(final PlayerData player, final long exp) {
        if (exp < 0) throw new NullPointerException("Negative experience: " + exp);
        long amountToAdd = exp;
        int level = 0;
        long expUntilNextLevel;
        while (amountToAdd >= (expUntilNextLevel = getExpUntilNextLevel(level))) {
            amountToAdd -= expUntilNextLevel;
            level++;
        }
    }

    public static long getExpUntilNextLevel(final int level) {
        if (level <= 15) {
            return (2L * level) + 7L;
        } else if ((level >= 16) && (level <= 30)) {
            return (5L * level) - 38L;
        } else {
            return (9L * level) - 158L;
        }
    }

    public static long getExpAtLevel(final int level) {
        long exp = 0;
        for (int i = 0; i < level; i++) {
            exp = Math.addExact(exp, getExpUntilNextLevel(i));
        }
        return exp;
    }
}
