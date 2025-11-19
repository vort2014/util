package io.github.vort2014.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class CommandLineArgumentUtilTest {

    private static final Map<String, String> expected = Map.of("end", "New York", "start", "Los Angeles", "transportation-method", "electric-car-large");

    @Test
    void testParseArguments() {

        var args = new String[] {
                " --end ",
                "\" New York \"",
                " --start ",
                "--=",
                "--transportation-method",
                "=",
                "electric-car-large"
        };
        var requiredArgumentNames = Set.of("end", "start",  "transportation-method");

        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(requiredArgumentNames, args))
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
        var map1 = CommandLineArgumentUtil.parseArguments(null, args);
        var map2 = CommandLineArgumentUtil.parseArguments(Set.of(), args);
        var map3 = CommandLineArgumentUtil.parseArguments(Set.of("end", "start", "transportation-method"), args);

        // then
        assertThat(map1).containsExactlyInAnyOrderEntriesOf(expected);
        assertThat(map2).containsExactlyInAnyOrderEntriesOf(expected);
        assertThat(map3).containsExactlyInAnyOrderEntriesOf(expected);
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
        var actual = CommandLineArgumentUtil.parseArguments(null, args);

        // then
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @Test
    void testParseWithMissingArguments() {
        var requiredArgumentNames = Set.of("start", "end",  "transportation-method");
        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(requiredArgumentNames, new String[0]))
                .hasMessageContainingAll(
                "Missing required argument: --start",
                        "Missing required argument: --transportation-method",
                        "Missing required argument: --end"
                );
        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(requiredArgumentNames, new String[]{" --end "}))
                .hasMessageContainingAll(
                        "Missing required argument: --start",
                        "Missing required argument: --transportation-method",
                        "Missing required argument: --end"
                );

        var args = new String[] {
                " --end ",
                " --start ",
                "\"Los Angeles\"",
                "--transportation-method=electric-car-large"
        };
        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(requiredArgumentNames, args))
                .hasMessageStartingWith("Missing required argument: --end");

        var args2 = new String[] {
                " --end ",
                "\" New York \"",
                " --start ",
                "--=",
                "--transportation-method",
                "=",
                "electric-car-large"
        };
        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(requiredArgumentNames, args2))
                .hasMessageStartingWith("Missing required argument: --start");

        var args3 = new String[] {
                " --end ",
                "=",
                "\" New York \"",
                "--transportation-method=electric-car-large",
                " --start ",
                "=",
        };
        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(requiredArgumentNames, args3))
                .hasMessageStartingWith("Missing required argument: --start");
    }
}