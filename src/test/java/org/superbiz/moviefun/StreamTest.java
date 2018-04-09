package org.superbiz.moviefun;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class StreamTest {

    @Test
    public void noneMatch() {
        List<Integer> list = Arrays.asList(1,2,3,4,5);
        assertTrue(list.stream().noneMatch(x -> x == 6));
        assertFalse(list.stream().noneMatch(x -> x == 2));
    }

    @Test
    public void filter() {
        List<Integer> list = Arrays.asList(1,2,3,4,5);
        assertEquals(list.stream().filter(x -> x%2 == 0).count(), 2);
    }
}
