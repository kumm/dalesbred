/*
 * Copyright (c) 2015 Evident Solutions Oy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.dalesbred.integration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static java.util.Objects.requireNonNull;

/**
 * Executes tests with given value of given system-property.
 */
public final class SystemPropertyRule implements TestRule {

    @NotNull
    private final String property;
    private final String value;

    public SystemPropertyRule(@NotNull String property, String value) {
        this.property = requireNonNull(property);
        this.value = value;
    }

    @Nullable
    @Override
    public Statement apply(@NotNull Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                String old = System.getProperty(property);
                try {
                    setProperty(property, value);
                    base.evaluate();

                } finally {
                    setProperty(property, old);
                }
            }

            private void setProperty(@NotNull String name, @Nullable String value) {
                if (value != null)
                    System.setProperty(name, value);
                else
                    System.clearProperty(name);
            }
        };
    }
}
