/*
 * Copyright (c) 2012 Evident Solutions Oy
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

package fi.evident.dalesbred.instantiation;

import org.jetbrains.annotations.NotNull;

import static fi.evident.dalesbred.utils.Primitives.isAssignableByBoxing;
import static fi.evident.dalesbred.utils.Require.requireNonNull;

/**
 * Abstract base class for simple conversions.
 */
public abstract class TypeConversionBase<S,T> extends TypeConversion<S,T> {

    private final Class<S> source;
    private final Class<T> target;

    protected TypeConversionBase(@NotNull Class<S> source, @NotNull Class<T> target) {
        this.source = requireNonNull(source);
        this.target = requireNonNull(target);
    }

    @NotNull
    @Override
    public boolean canConvert(@NotNull Class<?> source, @NotNull Class<?> target) {
        return isAssignableByBoxing(this.source, source) && isAssignableByBoxing(target, this.target);
    }

    @Override
    public String toString() {
        return getClass().getName() + " [" + source.getName() + " -> " + target.getName() + "]";
    }
}