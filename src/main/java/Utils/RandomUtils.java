package Utils;

import java.util.Random;
public class RandomUtils {
    private static final Random rand = new Random();
    public static Integer getRandomIntInRange(int range) {
        return rand.nextInt(range);
    }
}
