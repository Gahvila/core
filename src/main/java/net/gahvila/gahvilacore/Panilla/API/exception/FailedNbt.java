/*
 * MIT License
 *
 * Copyright (c) 2019 Ruinscraft, LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.gahvila.gahvilacore.Panilla.API.exception;

import net.gahvila.gahvilacore.Panilla.API.nbt.NbtCheck;

public class FailedNbt {

    public static FailedNbt FAIL_KEY_THRESHOLD = new FailedNbt(null, NbtCheck.NbtCheckResult.CRITICAL);

    public final String key;
    public final NbtCheck.NbtCheckResult result;

    public FailedNbt(String key, NbtCheck.NbtCheckResult result) {
        this.key = key;
        this.result = result;
    }

    public static boolean passes(FailedNbt failedNbt) {
        if (failedNbt == null) {
            return true;
        } else {
            return failedNbt.result == NbtCheck.NbtCheckResult.PASS;
        }
    }

    public static boolean fails(FailedNbt failedNbt) {
        return !passes(failedNbt);
    }

    public static boolean failsThreshold(FailedNbt failedNbt) {
        return failedNbt.equals(FAIL_KEY_THRESHOLD);
    }

}
