/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
/* Generated By:JavaCC: Do not edit this line. POParserTokenManager.java */
package org.tweetyproject.preferences.io;

import java.io.File;
import java.util.*;

import org.tweetyproject.commons.util.Triple;
import org.tweetyproject.preferences.*;

/** Token Manager. */
@SuppressWarnings("all")

/** POParserTokenManager class */
public class POParserTokenManager implements POParserConstants {

   /** Debug output. */
   public java.io.PrintStream debugStream = System.out;

   /**
 * Sets the debug stream for the parser.
 * <p>
 * This method allows setting a {@link java.io.PrintStream} where debug
 * information can be printed during the parsing process. It can be used
 * to redirect debug output to a specific stream, such as {@code System.out}
 * or a log file.
 * </p>
 *
 * @param ds The {@link java.io.PrintStream} to be used for printing debug information.
 */
   public void setDebugStream(java.io.PrintStream ds) {
      debugStream = ds;
   }

   private final int jjStopStringLiteralDfa_0(int pos, long active0) {
      switch (pos) {
         default:
            return -1;
      }
   }

   private final int jjStartNfa_0(int pos, long active0) {
      return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
   }

   private int jjStopAtPos(int pos, int kind) {
      jjmatchedKind = kind;
      jjmatchedPos = pos;
      return pos + 1;
   }

   private int jjMoveStringLiteralDfa0_0() {
      switch (curChar) {
         case 44:
            return jjStopAtPos(0, 7);
         case 123:
            return jjStopAtPos(0, 2);
         case 125:
            return jjStopAtPos(0, 3);
         default:
            return jjMoveNfa_0(1, 0);
      }
   }

   private int jjMoveNfa_0(int startState, int curPos) {
      int startsAt = 0;
      jjnewStateCnt = 7;
      int i = 1;
      jjstateSet[0] = startState;
      int kind = 0x7fffffff;
      for (;;) {
         if (++jjround == 0x7fffffff)
            ReInitRounds();
         if (curChar < 64) {
            long l = 1L << curChar;
            do {
               switch (jjstateSet[--i]) {
                  case 1:
                     if ((0x3ff000000000000L & l) != 0L) {
                        if (kind > 4)
                           kind = 4;
                        jjCheckNAdd(0);
                     } else if ((0x600L & l) != 0L) {
                        if (kind > 6)
                           kind = 6;
                     } else if (curChar == 13)
                        jjstateSet[jjnewStateCnt++] = 5;
                     else if (curChar == 60)
                        jjstateSet[jjnewStateCnt++] = 2;
                     if (curChar == 60) {
                        if (kind > 5)
                           kind = 5;
                     }
                     break;
                  case 0:
                     if ((0x3ff000000000000L & l) == 0L)
                        break;
                     kind = 4;
                     jjCheckNAdd(0);
                     break;
                  case 2:
                     if (curChar == 61)
                        kind = 5;
                     break;
                  case 3:
                     if (curChar == 60)
                        jjstateSet[jjnewStateCnt++] = 2;
                     break;
                  case 4:
                     if ((0x600L & l) != 0L && kind > 6)
                        kind = 6;
                     break;
                  case 5:
                     if (curChar == 10 && kind > 6)
                        kind = 6;
                     break;
                  case 6:
                     if (curChar == 13)
                        jjstateSet[jjnewStateCnt++] = 5;
                     break;
                  default:
                     break;
               }
            } while (i != startsAt);
         } else if (curChar < 128) {
            long l = 1L << (curChar & 077);
            do {
               switch (jjstateSet[--i]) {
                  case 1:
                  case 0:
                     if ((0x7fffffe07fffffeL & l) == 0L)
                        break;
                     kind = 4;
                     jjCheckNAdd(0);
                     break;
                  default:
                     break;
               }
            } while (i != startsAt);
         } else {
            int i2 = (curChar & 0xff) >> 6;
            long l2 = 1L << (curChar & 077);
            do {
               switch (jjstateSet[--i]) {
                  default:
                     break;
               }
            } while (i != startsAt);
         }
         if (kind != 0x7fffffff) {
            jjmatchedKind = kind;
            jjmatchedPos = curPos;
            kind = 0x7fffffff;
         }
         ++curPos;
         if ((i = jjnewStateCnt) == (startsAt = 7 - (jjnewStateCnt = startsAt)))
            return curPos;
         try {
            curChar = input_stream.readChar();
         } catch (java.io.IOException e) {
            return curPos;
         }
      }
   }

   static final int[] jjnextStates = {
   };

   /** Token literal values. */
   public static final String[] jjstrLiteralImages = {
         "", null, "\173", "\175", null, null, null, "\54", };

   /** Lexer state names. */
   public static final String[] lexStateNames = {
         "DEFAULT",
   };
   static final long[] jjtoToken = {
         0xfdL,
   };
   static final long[] jjtoSkip = {
         0x2L,
   };
   /**
    * The character input stream that this parser reads from.
    * <p>
    * This stream is used by the parser to read input characters sequentially for
    * tokenization and parsing.
    */
   protected SimpleCharStream input_stream;

   /**
    * Array that holds the round number for each state in the finite state machine
    * during parsing.
    * <p>
    * This is used for managing the state of the parsing algorithm and helps in
    * determining whether a state has been visited during the current parsing
    * round.
    */
   private final int[] jjrounds = new int[7];

   /**
    * Array that represents the set of states in the finite state machine during
    * parsing.
    * <p>
    * This array is used to store the active states that the parser is considering
    * as it moves through the input data.
    */
   private final int[] jjstateSet = new int[14];

   /**
    * The current character being processed by the parser.
    * <p>
    * This character is the next one read from the input stream and is used by the
    * tokenizing methods to identify tokens and syntax in the input data.
    */
   protected char curChar;

   /**
    * Constructor that initializes the POParserTokenManager with a provided
    * SimpleCharStream.
    * <p>
    * This constructor initializes the lexical analyzer with the given character
    * stream, which will be used for tokenization.
    * If the provided SimpleCharStream is static, an error is thrown because a
    * non-static lexical analyzer cannot use a static CharStream.
    *
    * @param stream The input character stream that the lexer will read from.
    * @throws Error if a static SimpleCharStream is used with a non-static lexical
    *               analyzer.
    */
   public POParserTokenManager(SimpleCharStream stream) {
      if (SimpleCharStream.staticFlag)
         throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
      input_stream = stream;
   }

   /**
    * Constructor that initializes the POParserTokenManager with a provided
    * SimpleCharStream and a specific lexical state.
    * <p>
    * This constructor initializes the lexical analyzer with the given character
    * stream and sets the initial lexical state.
    * It calls the base constructor to initialize the stream and then switches the
    * lexical analyzer to the specified state.
    *
    * @param stream   The input character stream that the lexer will read from.
    * @param lexState The initial lexical state to switch to.
    * @throws Error if a static SimpleCharStream is used with a non-static lexical
    *               analyzer.
    */
   public POParserTokenManager(SimpleCharStream stream, int lexState) {
      this(stream);
      SwitchTo(lexState);
   }

   /**
    * Reinitializes the parser's state with a new input stream.
    * <p>
    * This method resets the internal state of the parser so that it can begin
    * reading from a new input stream. It sets the matched position and state
    * counters to zero, assigns the new input stream to the parser, and resets
    * the rounds used for tracking the state of the lexer.
    *
    * @param stream The new {@link SimpleCharStream} to be used by the parser.
    */
   public void ReInit(SimpleCharStream stream) {
      jjmatchedPos = jjnewStateCnt = 0;
      curLexState = defaultLexState;
      input_stream = stream;
      ReInitRounds();
   }

   private void ReInitRounds() {
      int i;
      jjround = 0x80000001;
      for (i = 7; i-- > 0;)
         jjrounds[i] = 0x80000000;
   }

   /**
    * Reinitializes the parser's state with a new input stream and switches to the
    * specified lexical state.
    * <p>
    * This method reinitializes the parser by setting a new input stream and
    * also switches the parser to the specified lexical state. It is useful
    * when both the input source and the lexical state need to be reset.
    *
    * @param stream   The new {@link SimpleCharStream} to be used by the parser.
    * @param lexState The lexical state to switch to after reinitialization.
    */
   public void ReInit(SimpleCharStream stream, int lexState) {
      ReInit(stream);
      SwitchTo(lexState);
   }

   /**
 * Switches the lexer to the specified lexical state.
 * <p>
 * This method changes the current lexical state of the parser to the specified state.
 * Lexical states are typically used to manage different parsing contexts within the same parser.
 * </p>
 *
 * @param lexState The new lexical state to switch to. It must be a valid state between 0 and the maximum defined state.
 * @throws TokenMgrError if the specified lexical state is invalid (less than 0 or greater than or equal to 1).
 */
   public void SwitchTo(int lexState) {
      if (lexState >= 1 || lexState < 0)
         throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.",
               TokenMgrError.INVALID_LEXICAL_STATE);
      else
         curLexState = lexState;
   }

   /**
    * Fills and returns a new token using the matched kind and token image.
    * <p>
    * This method constructs a new {@link Token} based on the current matched token
    * kind
    * (`jjmatchedKind`) and the image from the input stream. It also sets the
    * token's
    * position information (line and column numbers) for both the beginning and end
    * of the token.
    *
    * @return A newly created {@link Token} with the appropriate kind, image, and
    *         position information.
    */
   protected Token jjFillToken() {
      final Token t;
      final String curTokenImage;
      final int beginLine;
      final int endLine;
      final int beginColumn;
      final int endColumn;
      String im = jjstrLiteralImages[jjmatchedKind];
      curTokenImage = (im == null) ? input_stream.GetImage() : im;
      beginLine = input_stream.getBeginLine();
      beginColumn = input_stream.getBeginColumn();
      endLine = input_stream.getEndLine();
      endColumn = input_stream.getEndColumn();
      t = Token.newToken(jjmatchedKind, curTokenImage);

      t.beginLine = beginLine;
      t.endLine = endLine;
      t.beginColumn = beginColumn;
      t.endColumn = endColumn;

      return t;
   }

   int curLexState = 0;
   int defaultLexState = 0;
   int jjnewStateCnt;
   int jjround;
   int jjmatchedPos;
   int jjmatchedKind;

   /**
    *
    * Return the next token
    *
    * @return the next token
    */
   public Token getNextToken() {
      Token matchedToken;
      int curPos = 0;

      EOFLoop: for (;;) {
         try {
            curChar = input_stream.BeginToken();
         } catch (java.io.IOException e) {
            jjmatchedKind = 0;
            matchedToken = jjFillToken();
            return matchedToken;
         }

         try {
            input_stream.backup(0);
            while (curChar <= 32 && (0x100000000L & (1L << curChar)) != 0L)
               curChar = input_stream.BeginToken();
         } catch (java.io.IOException e1) {
            continue EOFLoop;
         }
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_0();
         if (jjmatchedKind != 0x7fffffff) {
            if (jjmatchedPos + 1 < curPos)
               input_stream.backup(curPos - jjmatchedPos - 1);
            if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L) {
               matchedToken = jjFillToken();
               return matchedToken;
            } else {
               continue EOFLoop;
            }
         }
         int error_line = input_stream.getEndLine();
         int error_column = input_stream.getEndColumn();
         String error_after = null;
         boolean EOFSeen = false;
         try {
            input_stream.readChar();
            input_stream.backup(1);
         } catch (java.io.IOException e1) {
            EOFSeen = true;
            error_after = curPos <= 1 ? "" : input_stream.GetImage();
            if (curChar == '\n' || curChar == '\r') {
               error_line++;
               error_column = 0;
            } else
               error_column++;
         }
         if (!EOFSeen) {
            input_stream.backup(1);
            error_after = curPos <= 1 ? "" : input_stream.GetImage();
         }
         throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar,
               TokenMgrError.LEXICAL_ERROR);
      }
   }

   private void jjCheckNAdd(int state) {
      if (jjrounds[state] != jjround) {
         jjstateSet[jjnewStateCnt++] = state;
         jjrounds[state] = jjround;
      }
   }

   private void jjAddStates(int start, int end) {
      do {
         jjstateSet[jjnewStateCnt++] = jjnextStates[start];
      } while (start++ != end);
   }

   private void jjCheckNAddTwoStates(int state1, int state2) {
      jjCheckNAdd(state1);
      jjCheckNAdd(state2);
   }

}
