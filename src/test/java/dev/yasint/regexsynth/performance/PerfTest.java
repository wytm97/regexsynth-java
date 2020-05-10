package dev.yasint.regexsynth.performance;

import dev.yasint.regexsynth.api.Expression;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static dev.yasint.regexsynth.dsl.CharClasses.rangedSet;
import static dev.yasint.regexsynth.dsl.Numeric.integerRange;
import static dev.yasint.regexsynth.dsl.Operators.either;

public class PerfTest {

    private static void logElapsedTime(long startTime, long endTime) {
        log("Elapsed time (ms): " + (endTime - startTime));
    }

    private static void logUsedMemoryInMegabytes(long memory) {
        log("Used memory (mb): " + memory / (1024L * 1024L));
    }

    private static void log(String... messages) {
        for (String message : messages) {
            System.out.print(message);
        }
        System.out.println();
    }

    @Test
    public void perfTestTrieExpressionSynthesis() throws IOException, InterruptedException {

        // Load 7K word list. These are exceptional cases
        // you don't want to create such regular expressions ...

        final List<String> words = new ArrayList<>(
                Files.readAllLines(Paths.get("src/test/resources/words.txt"))
        );

        final Runtime runtime = Runtime.getRuntime(); // get runtime
        runtime.gc(); // invoke gc
        Thread.sleep(1000); // sleep 1sec

        log();
        log("Word list size: ", String.valueOf(words.size())); // print the word count

        long startTime = System.currentTimeMillis(); // record start time
        Expression alternation = either(new HashSet<>(words)); // actual code
        runtime.gc(); // clear the memory
        long memory = runtime.totalMemory() - runtime.freeMemory(); // used memory
        long stopTime = System.currentTimeMillis(); // mark end time

        logElapsedTime(startTime, stopTime);
        logUsedMemoryInMegabytes(memory);
        log("Synthesized: ", alternation.toRegex().toString());
        log();

    }

    @Test
    public void perfTestIntRangeExpressionSynthesis() throws InterruptedException {

        final Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // initial invoke gc
        Thread.sleep(1000); // sleep for 1sec

        log();

        long startTime = System.currentTimeMillis(); // record start time
        Expression range = integerRange(0, (int) Math.pow(10, 9) - 1); // actual code
        runtime.gc(); // clear the memory
        long memory = runtime.totalMemory() - runtime.freeMemory(); // used memory
        long stopTime = System.currentTimeMillis(); // mark end time

        logElapsedTime(startTime, stopTime);
        logUsedMemoryInMegabytes(memory);
        log("Synthesized: ", range.toRegex().toString());
        log();

    }

    @Test
    public void perfTestSetExpressionSynthesis() throws InterruptedException {

        final Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // initial invoke gc
        Thread.sleep(1000); // sleep for 1sec

        int codepointA = 0x0; // MIN
        int codepointB = 0x10FFFF; // MAX

        log();
        log("set character count: ", new BigInteger(Integer.toHexString(codepointB), 16).toString());

        long startTime = System.currentTimeMillis(); // record start time
        Expression set = rangedSet(codepointA, codepointB);
        runtime.gc(); // clear the memory
        long memory = runtime.totalMemory() - runtime.freeMemory(); // used memory
        long stopTime = System.currentTimeMillis(); // mark end time

        logElapsedTime(startTime, stopTime);
        logUsedMemoryInMegabytes(memory);
        log("Synthesized: ", set.toRegex().toString());
        log();

    }

}
