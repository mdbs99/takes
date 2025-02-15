/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.tk;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.StartsWith;
import org.takes.HttpException;
import org.takes.rq.RqFake;
import org.takes.rs.RsHeadPrint;

/**
 * Test case for {@link TkClasspath}.
 * @since 0.1
 */
final class TkClasspathTest {

    @Test
    void dispatchesByResourceName() throws Exception {
        MatcherAssert.assertThat(
            new RsHeadPrint(
                new TkClasspath().act(
                    new RqFake(
                        "GET", "/org/takes/Take.class?a", ""
                    )
                )
            ),
            new StartsWith("HTTP/1.1 200 OK")
        );
    }

    @Test
    void throwsWhenResourceNotFound() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new TkClasspath().act(
                new RqFake("PUT", "/something-else", "")
            )
        );
    }
}
