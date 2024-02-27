package com.propzy.job;

import com.propzy.job.test.JunitSample;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

public class JunitSampleTest {
    @DisplayName("testPrioritizeMinCount: Should return a List with userIds have the same min count")
    @ParameterizedTest(name = "{index} => userIds={0}, userIdAndCountMap={1}, expectUserIds={2}")
    @MethodSource("testPrioritizeMinCountFactory")
    void testPrioritizeMinCount(List<Integer> userIds, Map<Integer, Integer> userIdAndCountMap, List<Integer> expectUserIds) {
        JunitSample.prioritizeMinCount(userIds, userIdAndCountMap);
        Assertions.assertEquals(expectUserIds, userIds, "Arrays should be equal");
    }

    private static Stream<Arguments> testPrioritizeMinCountFactory() {
        List<Integer> userIds1 = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        Map<Integer, Integer> userIdAndCountMap1 = new HashMap<>();
        userIdAndCountMap1.put(2, 3);
        userIdAndCountMap1.put(4, 2);
        userIdAndCountMap1.put(5, 1);
        List<Integer> expectUserIds1 = Arrays.asList(1, 3);

        List<Integer> userIds2 = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        Map<Integer, Integer> userIdAndCountMap2 = new HashMap<>();
        userIdAndCountMap2.put(1, 3);
        userIdAndCountMap2.put(2, 2);
        userIdAndCountMap2.put(3, 1);
        userIdAndCountMap2.put(4, 4);
        userIdAndCountMap2.put(5, 5);
        List<Integer> expectUserIds2 = Arrays.asList(3);

        List<Integer> userIds3 = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        Map<Integer, Integer> userIdAndCountMap3 = new HashMap<>();
        userIdAndCountMap3.put(1, 1);
        userIdAndCountMap3.put(2, 1);
        userIdAndCountMap3.put(3, 1);
        userIdAndCountMap3.put(4, 1);
        userIdAndCountMap3.put(5, 1);
        List<Integer> expectUserIds3 = new ArrayList<>();
        expectUserIds3.addAll(userIds3);

        return Stream.of(
                Arguments.of(userIds1, userIdAndCountMap1, expectUserIds1),
                Arguments.of(userIds2, userIdAndCountMap2, expectUserIds2),
                Arguments.of(userIds3, userIdAndCountMap3, expectUserIds3)
        );
    }
}
