package com.propzy.job.test;

import java.util.*;

public class JunitSample {
    //userIds = [1,2,3,4,5]
    //userIdAndCountMap = <2,3><4,2><5,1>
    //Processing: <1,0><2,3><3,0><4,2><5,1>
    //Output: userIds = [1,3]
    public static void prioritizeMinCount(List<Integer> userIds, Map<Integer, Integer> userIdAndCountMap) {
        userIds.forEach(userId -> {
            if (!userIdAndCountMap.containsKey(userId)) {
                userIdAndCountMap.put(userId, 0);
            }
        });
        int minCount = Collections.min(userIdAndCountMap.values());
        userIds.clear();
        userIdAndCountMap.forEach((k, v) -> {
            if (v == minCount) {
                userIds.add(k);
            }
        });
    }
}
