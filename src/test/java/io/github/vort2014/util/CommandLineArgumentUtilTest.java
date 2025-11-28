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
        var actual = CommandLineArgumentUtil.parseArguments(args, null);

        // then
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(expected);
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
                        "Missing required argument: --transportation-method",
                        "Missing required argument: --end"
                );

        var args = new String[] {
                " --end ",
                " --start ",
                "\"Los Angeles\"",
                "--transportation-method=electric-car-large"
        };
        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(args, requiredArgumentNames))
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
        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(args2, requiredArgumentNames))
                .hasMessageStartingWith("Missing required argument: --start");

        var args3 = new String[] {
                " --end ",
                "=",
                "\" New York \"",
                "--transportation-method=electric-car-large",
                " --start ",
                "=",
        };
        assertThatThrownBy(() -> CommandLineArgumentUtil.parseArguments(args3, requiredArgumentNames))
                .hasMessageStartingWith("Missing required argument: --start");
    }
}