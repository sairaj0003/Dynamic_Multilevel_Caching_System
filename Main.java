import java.util.*;

class CacheLevel {
    private int size;
    private String evictionPolicy;
    private LinkedHashMap<String, String> lruCache; // for LRU eviction
    private Map<String, String> lfuCache; // for LFU eviction
    private Map<String, Integer> lfuFrequency; // to track frequency for LFU

    // Constructor to initialize the cache level
    public CacheLevel(int size, String evictionPolicy) {
		this.size = size;
		this.evictionPolicy = evictionPolicy;
		
		if (evictionPolicy.equals("LRU")) {
			this.lruCache = new LinkedHashMap<String, String>(size, 0.75f, true);
		} else if (evictionPolicy.equals("LFU")) {
			this.lfuCache = new HashMap<>();
            this.lfuFrequency = new HashMap<>();
		}
    }

    public String get(String key) {
		String value = null;
		if (evictionPolicy.equals("LRU")) {
			value = lruCache.getOrDefault(key, null);
			lruCache.remove(key);
		} else if (evictionPolicy.equals("LFU")) {
			if (lfuCache.containsKey(key)) {
				value = lfuCache.get(key);
				lfuCache.remove(key);
				lfuFrequency.remove(key);
			}
		}
		return value;
	}

    public Map.Entry<String, String> put(String key, String value) {
		Map.Entry<String, String> evictEntry = null;
		if (evictionPolicy.equals("LRU")) {
			if (lruCache.size() >= size) {
				Iterator<Map.Entry<String, String>> iterator = lruCache.entrySet().iterator();
				evictEntry = iterator.next();
				iterator.remove();
			}
			lruCache.put(key, value);
		} else if (evictionPolicy.equals("LFU")) {
            if (lfuCache.size() >= size) {
				String leastFrequentKey = null;
				int minFreq = MultiLevelCacheSystem.frequency.getOrDefault(key, 1);
				for (String fkey : lfuFrequency.keySet()) {
					int freq = lfuFrequency.get(fkey);
					if (freq <= minFreq) {
						minFreq = freq;
						leastFrequentKey = fkey;
					}
				}
				if (leastFrequentKey != null) {
					evictEntry = new AbstractMap.SimpleEntry<>(leastFrequentKey, lfuCache.get(leastFrequentKey));
					lfuCache.remove(leastFrequentKey);
					lfuFrequency.remove(leastFrequentKey);
				} else {
					evictEntry = new AbstractMap.SimpleEntry<>(key, value);
					return evictEntry;
				}
            }
			lfuCache.put(key, value);
			
			
			if (MultiLevelCacheSystem.frequency.getOrDefault(key, 1) > 1) {
				lfuFrequency.put(key, MultiLevelCacheSystem.frequency.get(key));
			} else {
				lfuFrequency.put(key, lfuFrequency.getOrDefault(key, 0) + 1);
			}
			
			if (lfuCache.size() < size && evictEntry != null) {
				lfuCache.put(evictEntry.getKey(), evictEntry.getValue());
				return null;
			}
        }
		return evictEntry;
    }
	
	public void removeCacheLevel() {
		if (evictionPolicy.equals("LRU")) {
			for (String key : lruCache.keySet()) {
				MultiLevelCacheSystem.frequency.remove(key);
			}
		} else {
			for (String key : lfuCache.keySet()) {
				MultiLevelCacheSystem.frequency.remove(key);
			}
		}
	}
	
    public void displayCache() {
		if (evictionPolicy.equals("LRU")) {
            System.out.println(lruCache);
        } else if (evictionPolicy.equals("LFU")) {
            System.out.println(lfuCache);
        }
    }
}


class MultiLevelCacheSystem {
	public static Map<String, Integer> frequency;
    public static List<CacheLevel> cacheLevels;

    // Constructor to initialize the cache system
    public MultiLevelCacheSystem() {
		frequency = new HashMap<>();
        cacheLevels = new ArrayList<>();
    }

    // Add a new cache level
    public void addCacheLevel(int size, String evictionPolicy) {
		if (evictionPolicy.equals("LRU") || evictionPolicy.equals("LFU")) {
			CacheLevel newCacheLevel = new CacheLevel(size, evictionPolicy);
			cacheLevels.add(newCacheLevel);
		} else {
			System.out.println("Invalid Eviction Policy!");
		}
    }

    // Retrieve data from the cache system
    public String get(String key) {
		for (int i = 0; i < cacheLevels.size(); i++) {
			String value = cacheLevels.get(i).get(key);
			if (value != null) {
				put(key, value);
				return value;
			}
		}
        return null;
	}

    // Insert data into the L1 cache
    public void put(String key, String value) {
        if (!cacheLevels.isEmpty()) {
			int index = 0;
			frequency.put(key, frequency.getOrDefault(key, 0) + 1);
            Map.Entry<String, String> evictEntry = cacheLevels.get(index).put(key, value);
			while(evictEntry != null) {
				index += 1;
				if (index < cacheLevels.size()) {
					evictEntry = cacheLevels.get(index).put(evictEntry.getKey(), evictEntry.getValue());
				} else {
					System.out.println("All Cache Storage is full!");
					break;
				}
			}
        } else {
			System.out.println("No Cache Levels!");
		}
    }

    // Remove a cache level at the specified level
    public void removeCacheLevel(int level) {
		if (level >= 0 && level < cacheLevels.size()) {
			cacheLevels.get(level).removeCacheLevel();
			cacheLevels.remove(level);
		} else {
			System.out.println("No Cache found for level: " + (level + 1));
		}
	}
	
    // Display all cache levels
    public void displayCache() {
		int i;
        System.out.println("");
		for (i = 0; i < cacheLevels.size(); i++) {
            System.out.print("L" + (i + 1) + " Cache: ");
            cacheLevels.get(i).displayCache();
        }
		if (i == 0) {
			System.out.println("No Cache found!");
		}
	}
}


public class Main {
    public static void main(String[] args) {
        MultiLevelCacheSystem mlc = new MultiLevelCacheSystem();
		
		// Comment this while running test case
		// Run Time for testing
		int opt, size, level;
		String evictionPolicy, key, value;
		Scanner sc = new Scanner(System.in);
		
		while (true) {
			System.out.println("\n1. addCacheLevel(size, evictionPolicy)");
			System.out.println("2. get(key)");
			System.out.println("3. put(key, value)");
			System.out.println("4. removeCacheLevel(level)");
			System.out.println("5. displayCache()");
			System.out.println("6. exit()");
			System.out.print("Select Option: ");
			
			opt = sc.nextInt();
			
			switch (opt) {
				case 1:
					System.out.print("\nEnter Size: ");
					size = sc.nextInt();
					System.out.print("Enter evictionPolicy(LRU or LFU): ");
					evictionPolicy = sc.next();
					mlc.addCacheLevel(size, evictionPolicy);
					break;
				case 2:
					System.out.print("\nEnter Key: ");
					key = sc.next();
					System.out.println("Value: " + mlc.get(key));
					break;
				case 3:
					System.out.print("\nEnter Key: ");
					key = sc.next();
					System.out.print("Enter Value: ");
					value = sc.next();
					mlc.put(key, value);
					break;
				case 4:
					System.out.print("\nEnter level: ");
					level = sc.nextInt();
					mlc.removeCacheLevel(level - 1);
					break;
				case 5:
					mlc.displayCache();
					break;
				case 6:
					System.exit(0);
					break;
				default:
					System.out.println("Please enter a valid option!");
			}
		}
		
		// Uncomment the test case you want to run.
		/*
		// Test Case: 1

        System.out.println("\naddCacheLevel(3, \"LRU\")");
		mlc.addCacheLevel(3, "LRU");
		
        System.out.println("\naddCacheLevel(2, \"LFU\")");
		mlc.addCacheLevel(2, "LFU");

        System.out.println("\nput(\"A\", \"1\")");
		mlc.put("A", "1");
		
        System.out.println("\nput(\"B\", \"2\")");
		mlc.put("B", "2");
		
        System.out.println("\nput(\"C\", \"3\")");
		mlc.put("C", "3");
		
		System.out.println("\nGet A: " + mlc.get("A"));
		
        System.out.println("\nput(\"D\", \"4\")");
		mlc.put("D", "4");
		
        System.out.println("\nGet C: " + mlc.get("C"));

        mlc.displayCache();
		*/
		
		/*
		// Test Case: 2
		
        System.out.println("\naddCacheLevel(3, \"LFU\")");
		mlc.addCacheLevel(3, "LFU");
        
		System.out.println("\naddCacheLevel(2, \"LRU\")");
		mlc.addCacheLevel(2, "LRU");

        System.out.println("\nput(\"A\", \"1\")");
		mlc.put("A", "1");
		
        System.out.println("\nput(\"B\", \"2\")");
		mlc.put("B", "2");
		
        System.out.println("\nput(\"C\", \"3\")");
		mlc.put("C", "3");
		
		System.out.println("\nGet A: " + mlc.get("A"));
		
        System.out.println("\nput(\"D\", \"4\")");
		mlc.put("D", "4");
		
        System.out.println("\nGet C: " + mlc.get("C"));

        mlc.displayCache();
		*/
		
		/*
		// Test Case: 3
		
		System.out.println("\naddCacheLevel(3, \"LFU\")");
		mlc.addCacheLevel(3, "LFU");
		
		System.out.println("\naddCacheLevel(2, \"LRU\")");
        mlc.addCacheLevel(2, "LRU");
		
		System.out.println("\naddCacheLevel(5, \"LFU\")");
        mlc.addCacheLevel(5, "LFU");

        
		System.out.println("\nput(\"A\", \"1\")");
		mlc.put("A", "1");
		
        System.out.println("\nput(\"B\", \"2\")");
		mlc.put("B", "2");
		
        System.out.println("\nput(\"C\", \"3\")");
		mlc.put("C", "3");
		
        System.out.println("\nput(\"D\", \"4\")");
		mlc.put("D", "4");
		
        System.out.println("\nGet A: " + mlc.get("A"));
		
        System.out.println("\nput(\"E\", \"5\")");
		mlc.put("E", "5");
		
        System.out.println("\nput(\"F\", \"6\")");
		mlc.put("F", "6");
		
        System.out.println("\nput(\"G\", \"7\")");
		mlc.put("G", "7");
		
        System.out.println("\nGet A: " + mlc.get("A"));
		
        System.out.println("\nput(\"H\", \"8\")");
		mlc.put("H", "8");
		
		System.out.println("\nremoveCacheLevel(1)");
		mlc.removeCacheLevel(1);
		
        System.out.println("\nGet C: " + mlc.get("C"));
		
        System.out.println("\nGet G: " + mlc.get("G"));
		
		System.out.println("\nput(\"I\", \"9\")");
		mlc.put("I", "9");
		
		System.out.println("\nput(\"J\", \"10\")");
		mlc.put("J", "10");

        mlc.displayCache();
		*/
    }
}