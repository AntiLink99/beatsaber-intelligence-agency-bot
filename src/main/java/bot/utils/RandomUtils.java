package bot.utils;

import java.util.List;
import java.util.Random;

public class RandomUtils {

	private static Random randomGenerator = new Random();
	
	public static <T> T getRandomItem(List<T> list) {
		int index = randomGenerator.nextInt(list.size());
		return list.get(index);
	}
}
