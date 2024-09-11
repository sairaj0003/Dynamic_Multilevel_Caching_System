# Dynamic Multi-Level Cache System

## Introduction
This project implements a **Dynamic Multi-Level Cache System** that supports both **LRU (Least Recently Used)** and **LFU (Least Frequently Used)** eviction policies. The cache system consists of multiple levels, each with its own size and eviction policy. The main goal of the system is to store and manage data across different cache levels efficiently, using a custom-defined eviction strategy.

## Key Components
1. **Cache Level**: Represents each level in the multi-level cache system. Each level can have either an LRU or LFU eviction policy.
2. **Eviction Policies**: 
   - **LRU (Least Recently Used)**: Evicts the least recently used items when the cache is full.
   - **LFU (Least Frequently Used)**: Evicts the least frequently used items when the cache is full, using a frequency count.
3. **Multi-Level Cache System**: A system where multiple cache levels are managed. Data flows from higher to lower levels, lower to higher levels and evictions are handled based on the defined eviction policy for each level.

## Approach and Key Decisions
### 1. **Eviction Policies**
   - For **LRU**, we used a `LinkedHashMap` with access-order, allowing easy tracking of recently accessed entries.
   - For **LFU**, we used a `HashMap` to store the cache and another `HashMap` to maintain frequency counts of accesses to determine the least frequently used entries.

### 2. **Cache Hierarchy**
   - The system is organized into multiple cache levels, where data first enters the top-level cache (L1) and, if needed, is evicted to lower levels.
   - When retrieving data, the system checks each level starting from the highest until it finds the data or returns `null` if the data is not in any cache level.

### 3. **Eviction and Frequency Management**
   - **LRU** relies on the access order of entries in the cache, automatically updating the order on each access or insertion.
   - **LFU** requires manual tracking of the access frequency for each key. When the cache is full, the entry with the lowest frequency is evicted.

### 4. **Data Consistency**
   - If data is found in a lower cache level, it is moved to the highest level (L1) to ensure that frequently accessed data stays at the highest level for faster retrieval.
   - Frequency counts are maintained globally across cache levels, so an item's access frequency is consistent across the entire system.

### 5. **User Interaction**
   - The system provides a menu-driven approach for interaction, where users can add cache levels, put/get data, remove cache levels, and display the current state of the cache.

## Features
- Supports multiple cache levels.
- LRU and LFU eviction policies.
- Dynamically add or remove cache levels.
- Retrieve, insert, and display cache content.
- Track and evict entries based on the selected eviction policy.

## Class Structure
1. **CacheLevel**
   - **Attributes**: Size, eviction policy (LRU/LFU), and internal cache structures.
   - **Methods**:
     - `get()`: Retrieve data and update eviction logic.
     - `put()`: Insert data into the cache with eviction if necessary.
     - `removeCacheLevel()`: Clear the cache for that level.
     - `displayCache()`: Display the current cache contents.

2. **MultiLevelCacheSystem**
   - **Attributes**: Global frequency map, list of cache levels.
   - **Methods**:
     - `addCacheLevel()`: Add a new cache level.
     - `get()`: Retrieve data from the cache system.
     - `put()`: Insert data into the cache, starting from the top level.
     - `removeCacheLevel()`: Remove a specific cache level.
     - `displayCache()`: Display the contents of all cache levels.

## How to Run the Application

### Prerequisites
- **Java Development Kit (JDK)** installed on your system.

### Steps to Run:
1. **Clone or Download the Code**: Download the project files or clone the repository.

2. **Compile the Code**: Open a terminal or command prompt and navigate to the folder where the code is stored. Run the following command to compile:
   ```bash
   javac Main.java
   ```

3. **Run the Application**: After compiling, run the application by executing:
   ```bash
   java Main
   ```

4. **User Interaction**: Follow the on-screen menu to interact with the multi-level cache system:
   - **addCacheLevel(size, evictionPolicy)**: Add a new cache level by specifying the size and eviction policy (LRU or LFU).
   - **get(key)**: Retrieve a value for a specific key.
   - **put(key, value)**: Insert a key-value pair into the cache.
   - **removeCacheLevel(level)**: Remove a cache level by specifying the level number.
   - **displayCache()**: Display the current state of all cache levels.
   - **exit()**: Exit the application.

### Sample Run:

```bash
1. addCacheLevel(size, evictionPolicy)
2. get(key)
3. put(key, value)
4. removeCacheLevel(level)
5. displayCache()
6. exit()
Select Option: 1

Enter Size: 3
Enter evictionPolicy(LRU or LFU): LRU

1. addCacheLevel(size, evictionPolicy)
2. get(key)
3. put(key, value)
4. removeCacheLevel(level)
5. displayCache()
6. exit()
Select Option: 3

Enter Key: A
Enter Value: 1

1. addCacheLevel(size, evictionPolicy)
2. get(key)
3. put(key, value)
4. removeCacheLevel(level)
5. displayCache()
6. exit()
Select Option: 3

Enter Key: B
Enter Value: 2

1. addCacheLevel(size, evictionPolicy)
2. get(key)
3. put(key, value)
4. removeCacheLevel(level)
5. displayCache()
6. exit()
Select Option: 1

Enter Size: 2
Enter evictionPolicy(LRU or LFU): LFU

1. addCacheLevel(size, evictionPolicy)
2. get(key)
3. put(key, value)
4. removeCacheLevel(level)
5. displayCache()
6. exit()
Select Option: 3

Enter Key: C
Enter Value: 3

1. addCacheLevel(size, evictionPolicy)
2. get(key)
3. put(key, value)
4. removeCacheLevel(level)
5. displayCache()
6. exit()
Select Option: 3

Enter Key: D
Enter Value: 4

1. addCacheLevel(size, evictionPolicy)
2. get(key)
3. put(key, value)
4. removeCacheLevel(level)
5. displayCache()
6. exit()
Select Option: 2

Enter Key: B
Value: 2

1. addCacheLevel(size, evictionPolicy)
2. get(key)
3. put(key, value)
4. removeCacheLevel(level)
5. displayCache()
6. exit()
Select Option: 5

L1 Cache: {C=3, D=4, B=2}
L2 Cache: {A=1}

1. addCacheLevel(size, evictionPolicy)
2. get(key)
3. put(key, value)
4. removeCacheLevel(level)
5. displayCache()
6. exit()
Select Option: 4

Enter level: 1

1. addCacheLevel(size, evictionPolicy)
2. get(key)
3. put(key, value)
4. removeCacheLevel(level)
5. displayCache()
6. exit()
Select Option: 5

L1 Cache: {A=1}

1. addCacheLevel(size, evictionPolicy)
2. get(key)
3. put(key, value)
4. removeCacheLevel(level)
5. displayCache()
6. exit()
Select Option: 6
```

--- 
