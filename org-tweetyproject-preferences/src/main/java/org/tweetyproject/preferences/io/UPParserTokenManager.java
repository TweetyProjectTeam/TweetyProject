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
/* Generated By:JavaCC: Do not edit this line. UPParserTokenManager.java */
package org.tweetyproject.preferences.io;
import java.util.*;
import org.tweetyproject.preferences.*;
import org.tweetyproject.preferences.update.Update;
import org.tweetyproject.preferences.Operation;

/** Token Manager. */
@SuppressWarnings("all")
public class UPParserTokenManager implements UPParserConstants
{

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output.
   * @param ds print stream
  */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 40:
         return jjStopAtPos(0, 2);
      case 41:
         return jjStopAtPos(0, 3);
      case 44:
         return jjStopAtPos(0, 8);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 10;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 4)
                        kind = 4;
                  }
                  else if ((0x600L & l) != 0L)
                  {
                     if (kind > 7)
                        kind = 7;
                  }
                  else if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 8;
                  else if (curChar == 45)
                     jjCheckNAdd(4);
                  else if (curChar == 43)
                     jjCheckNAdd(4);
                  if ((0x3fe000000000000L & l) != 0L)
                     jjCheckNAdd(2);
                  break;
               case 1:
                  if ((0x3fe000000000000L & l) != 0L)
                     jjCheckNAdd(2);
                  break;
               case 2:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAdd(2);
                  break;
               case 3:
                  if (curChar == 43)
                     jjCheckNAdd(4);
                  break;
               case 4:
                  if ((0x3fe000000000000L & l) != 0L && kind > 5)
                     kind = 5;
                  break;
               case 5:
                  if (curChar == 45)
                     jjCheckNAdd(4);
                  break;
               case 7:
                  if ((0x600L & l) != 0L && kind > 7)
                     kind = 7;
                  break;
               case 8:
                  if (curChar == 10 && kind > 7)
                     kind = 7;
                  break;
               case 9:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 6:
                  if ((0x7fffffe00000000L & l) == 0L)
                     break;
                  kind = 6;
                  jjCheckNAdd(6);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 10 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
};

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, "\50", "\51", null, null, null, null, "\54", };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
};
static final long[] jjtoToken = {
   0x1fdL,
};
static final long[] jjtoSkip = {
   0x2L,
};
/** input stream */
protected SimpleCharStream input_stream;
/** jjrounds */
private final int[] jjrounds = new int[10];
/** jjstateSet */
private final int[] jjstateSet = new int[20];
/** char */
protected char curChar;
/**
 * Token manager for parsing with the given input stream.
 * @param stream the simple
 */
public UPParserTokenManager(SimpleCharStream stream) {
   if (SimpleCharStream.staticFlag) {
       throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   }
   input_stream = stream;
}

/**
* Token manager for parsing with the given input stream and initial lexical state.
*
* @param stream The input stream to be used by the token manager.
* @param lexState The initial lexical state to switch to.
*/
public UPParserTokenManager(SimpleCharStream stream, int lexState) {
   this(stream);
   SwitchTo(lexState);
}

/**
* Reinitializes the parser with a new input stream, resetting to default lexical state.
*
* @param stream The new input stream to be used.
*/
public void ReInit(SimpleCharStream stream) {
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}

/**
* Resets the internal state rounds for the token manager.
*/
private void ReInitRounds() {
   int i;
   jjround = 0x80000001;
   for (i = 10; i-- > 0;) {
       jjrounds[i] = 0x80000000;
   }
}

/**
* Reinitializes the parser with a new input stream and sets the lexical state.
*
* @param stream The new input stream to be used.
* @param lexState The lexical state to switch to.
*/
public void ReInit(SimpleCharStream stream, int lexState) {
   ReInit(stream);
   SwitchTo(lexState);
}

/**
* Switches the lexical analyzer to a specified lexical state.
*
* @param lexState The new lexical state to switch to.
* @throws TokenMgrError If the specified lexical state is invalid.
*/
public void SwitchTo(int lexState) {
   if (lexState >= 1 || lexState < 0) {
       throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   } else {
       curLexState = lexState;
   }
}
/**
 * Return fill token
 *
 * @return token
 */
protected Token jjFillToken()
{
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

/** Get the next Token.
 * @return token
*/
public Token getNextToken()
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100000000L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

}
