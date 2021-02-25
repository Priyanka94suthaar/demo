package Utils;

public class RandomNumberGenerator {

    public static int getRandomNumber(int min, int max) {
        return (int)(Math.random()*(max - min) + min);
    }
}
