/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Yegor Bugayenko
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

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkMethods;
import org.takes.facets.fork.TkFork;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;

/**
 * Test case for {@link TkProxy}.
 * @author Dragan Bozanovic (bozanovicdr@gmail.com)
 * @version $Id$
 * @since 0.25
 * @todo #458:30min/DEV We need more tests for TkProxy.
 *  The tests should verify different combinations of request/response headers.
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@RunWith(Parameterized.class)
public final class TkProxyTest {
    /**
     * Http method.
     */
    private String method;

    /**
     * Expected test result.
     */
    private String expected;

    /**
     * Constructor.
     * @param method Http method.
     * @param expected Expected test result.
     */
    public TkProxyTest(final String method, final String expected) {
        this.method = method;
        this.expected = expected;
    }

    /**
     * Http methods for testing.
     * @return The testing data
     */
    @Parameterized.Parameters
    public static Collection<Object[]> methods() {
        return Arrays.asList(
            new Object[][]{
                {RqMethod.POST, "hello, post!"},
                {RqMethod.GET, "hello, get!"},
                {RqMethod.PUT, "hello, put!"},
                {RqMethod.DELETE, "hello, delete!"},
                {RqMethod.TRACE, "hello, trace!"},
            });
    }

    /**
     * TkProxy can just work.
     * @throws Exception If some problem inside
     */
    @Test
    public void justWorks() throws Exception {
        new FtRemote(
            new TkFork(
                new FkMethods(this.method, new TkFixed(this.expected))
            )
        ).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(
                            new TkProxy(home).act(
                                new RqFake(TkProxyTest.this.method)
                            )
                        ).print(),
                        Matchers.containsString(
                            TkProxyTest.this.expected
                        )
                    );
                }
            }
        );
    }

    /**
     * TkProxy can correctly maps path string.
     * @throws Exception If some problem inside
     */
    @Test
    public void correctlyMapsPathString() throws Exception {
        final Take take = new Take() {
            @Override
            public Response act(final Request req) throws IOException {
                return new RsText(new RqHref.Base(req).href().toString());
            }
        };
        new FtRemote(
            new TkFork(
                new FkMethods(this.method, take)
            )
        ).exec(
            new FtRemote.Script() {
                @Override
                public void exec(final URI home) throws IOException {
                    MatcherAssert.assertThat(
                        new RsPrint(
                            new TkProxy(home).act(
                                new RqFake(
                                    TkProxyTest.this.method, "/a/b/c"
                                )
                            )
                        ).printBody(),
                        Matchers.equalTo(
                            String.format(
                                "http://%s:%d/a/b/c",
                                home.getHost(), home.getPort()
                            )
                        )
                    );
                }
            }
        );
    }
}
