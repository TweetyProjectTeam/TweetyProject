/**
 * This file is part of jArgSemSAT
 * <p>
 * Copyright (c) 2015 Federico Cerutti <federico.cerutti@acm.org>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures;

public class Encoding {
    private static final int NUMENCSPOS = 6;
    private static final int POS_C_IN_RIGHT = 0;
    private static final int POS_C_IN_LEFT = 1;
    private static final int POS_C_OUT_RIGHT = 2;
    private static final int POS_C_OUT_LEFT = 3;
    private static final int POS_C_UNDEC_RIGHT = 4;
    private static final int POS_C_UNDEC_LEFT = 5;

    private final int[] positions = new int[]
            {POS_C_IN_RIGHT, POS_C_IN_LEFT, POS_C_OUT_RIGHT, POS_C_OUT_LEFT,
                    POS_C_UNDEC_RIGHT, POS_C_UNDEC_LEFT};

    private final boolean[] encoding = new boolean[]
            {false, false, false, false, false, false};

    public Encoding(String i) throws Exception {
        if (i.length() != NUMENCSPOS) {
            throw new Exception("I was expecting six values");
        }

        for (int p : positions) {
            if (i.charAt(p) != '0')
                this.encoding[p] = true;
        }

        if (!this.check()) {
            throw new Exception("Weak encoding");
        }
    }

    public static Encoding defaultEncoding() {
        Encoding ret = null;
        try {
            ret = new Encoding("111100");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private boolean check() {
        int sum = 0;
        for (int i = 0; i < NUMENCSPOS; i++) {
            sum += this.encoding[i] ? 1 : 0;
        }

        if (sum < 3)
            return false;

        if (sum == 3) {
            return (this.get_C_in_right() && this.get_C_out_right() && this
                    .get_C_undec_right())
                    || (this.get_C_in_left() && this.get_C_out_left() && this
                    .get_C_undec_left());
        }

        if (sum == 4) {
            return (!this.get_C_undec_right() || !this.get_C_undec_left()
                    || !this.get_C_in_right() || !this.get_C_out_left())
                    && (!this.get_C_undec_right() || !this.get_C_undec_left()
                    || !this.get_C_in_left() || !this.get_C_out_right())
                    && (!this.get_C_out_right() || !this.get_C_out_left()
                    || !this.get_C_in_right() || !this.get_C_undec_left())
                    && (!this.get_C_out_right() || !this.get_C_out_right()
                    || !this.get_C_in_left() || !this.get_C_undec_right())
                    && (!this.get_C_in_right() || !this.get_C_in_left()
                    || !this.get_C_out_right() || !this
                    .get_C_undec_left())
                    && (!this.get_C_in_right() || !this.get_C_in_left()
                    || !this.get_C_out_left() || !this
                    .get_C_undec_right());
        }
        return true;
    }

    public boolean get_C_in_right() {
        return this.encoding[POS_C_IN_RIGHT];
    }

    public boolean get_C_in_left() {
        return this.encoding[POS_C_IN_LEFT];
    }

    public boolean get_C_out_right() {
        return this.encoding[POS_C_OUT_RIGHT];
    }

    public boolean get_C_out_left() {
        return this.encoding[POS_C_OUT_LEFT];
    }

    public boolean get_C_undec_right() {
        return this.encoding[POS_C_UNDEC_RIGHT];
    }

    public boolean get_C_undec_left() {
        return this.encoding[POS_C_UNDEC_LEFT];
    }
}
