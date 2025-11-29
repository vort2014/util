package io.github.vort2014.util;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommandLineArgumentUtilTest {

    private static final Map<String, String> EXPECTED = Map.of("end", "New York", "start", "Los Angeles", "transportation-method", "electric-car-large");

    @Test
    void testParseArguments() {

        var args = new String[] {
                " --end ",
                "\" New York \"",
                "--=",
                "--transportation-method",
                "=",
                "electric-car-large"
        };
        var requiredArgumentNames = Set.of("end", "start",  "transportation-method");

        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(args, requiredArgumentNames))
                .hasMessageStartingWith("Missing required argument: --start");
    }

    @Test
    void testEmptyRequiredArguments() {

        // given
        var args = new String[] {
                " --end ",
                "\"New York\"",
                " --start ",
                "\"Los Angeles\"",
                "--transportation-method=electric-car-large"
        };

        // when
        var map1 = CommandLineArgumentUtil.parseArguments(args, null);
        var map2 = CommandLineArgumentUtil.parseArguments(args, Set.of());
        var map3 = CommandLineArgumentUtil.parseArguments(args, Set.of("end", "start", "transportation-method"));

        // then
        assertThat(map1).containsExactlyInAnyOrderEntriesOf(EXPECTED);
        assertThat(map2).containsExactlyInAnyOrderEntriesOf(EXPECTED);
        assertThat(map3).containsExactlyInAnyOrderEntriesOf(EXPECTED);
    }

    @Test
    void testParse2() {

        // given
        var args = new String[] {
                " --end ",
                " = ",
                "\" New York \"",
                "--transportation-method=electric-car-large",
                " --start ",
                " = ",
                "\"Los Angeles\"",
        };

        // when
        var actual = CommandLineArgumentUtil.parseArguments(args, null);

        // then
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(EXPECTED);
    }

    @Test
    void testParse3() {

        // given
        var expected = new HashMap<String, String>();
        expected.put("end", "New York");
        expected.put("start", "Los Angeles");
        expected.put("transportation-method", "electric-car-large");
        expected.put("detach", null);
        var args1 = new String[] {
                " --detach ",
                " --end ",
                " = ",
                "\" New York \"",
                "--transportation-method=electric-car-large",
                " --start ",
                " = ",
                "\"Los Angeles\"",
        };
        var args2 = new String[] {
                " --end ",
                " = ",
                "\" New York \"",
                " --detach ",
                "--transportation-method=electric-car-large",
                " --start ",
                " = ",
                "\"Los Angeles\"",
        };
        var args3 = new String[] {
                " --end ",
                " = ",
                "\" New York \"",
                "--transportation-method=electric-car-large",
                " --start ",
                " = ",
                "\"Los Angeles\"",
                " --detach ",
        };

        // when
        var actual1 = CommandLineArgumentUtil.parseArguments(args1);
        var actual2 = CommandLineArgumentUtil.parseArguments(args2);
        var actual3 = CommandLineArgumentUtil.parseArguments(args3);

        // then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actual1).containsExactlyInAnyOrderEntriesOf(expected);
        softly.assertThat(actual2).containsExactlyInAnyOrderEntriesOf(expected);
        softly.assertThat(actual3).containsExactlyInAnyOrderEntriesOf(expected);
        softly.assertAll();
    }

    @Test
    void testParseWithMissingArguments() {
        var requiredArgumentNames = Set.of("start", "end",  "transportation-method");
        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(new String[0], requiredArgumentNames))
                .hasMessageContainingAll(
                "Missing required argument: --start",
                        "Missing required argument: --transportation-method",
                        "Missing required argument: --end"
                );
        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(new String[]{" --end "}, requiredArgumentNames))
                .hasMessageContainingAll(
                        "Missing required argument: --start",
                        "Missing required argument: --transportation-method"
                );

        var args2 = new String[] {
                " --end ",
                "\" New York \"",
                "--=",
                "--transportation-method",
                "=",
                "electric-car-large"
        };
        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(args2, requiredArgumentNames))
                .hasMessageStartingWith("Missing required argument: --start");

        var args3 = new String[] {
                " --end ",
                "=",
                "\" New York \"",
                " --start ",
                "=",
        };
        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(args3, requiredArgumentNames))
                .hasMessageStartingWith("Missing required argument: --transportation-method");
    }
}