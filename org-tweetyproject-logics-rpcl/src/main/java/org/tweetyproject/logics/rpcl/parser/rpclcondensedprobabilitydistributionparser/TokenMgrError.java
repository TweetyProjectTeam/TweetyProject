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
/* Generated By:JavaCC: Do not edit this line. TokenMgrError.java Version 5.0 */
/* JavaCCOptions: */
package org.tweetyproject.logics.rpcl.parser.rpclcondensedprobabilitydistributionparser;

/** Token Manager Error. */
public class TokenMgrError extends Error
{

  /**
   * The version identifier for this Serializable class.
   * Increment only if the <i>serialized</i> form of the
   * class changes.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Ordinals for various reasons why an Error of this type can be thrown.
   */

  /**
   * Lexical error occurred.
   */
  static final int LEXICAL_ERROR = 0;

  /**
   * An attempt was made to create a second instance of a static token manager.
   */
  static final int STATIC_LEXER_ERROR = 1;

  /**
   * Tried to change to an invalid lexical state.
   */
  static final int INVALID_LEXICAL_STATE = 2;

  /**
   * Detected (and bailed out of) an infinite loop in the token manager.
   */
  static final int LOOP_DETECTED = 3;

  /**
   * Indicates the reason why the exception is thrown. It will have
   * one of the above 4 values.
   */
  int errorCode;

  /**
   * Replaces unprintable characters by their escaped (or unicode escaped)
   * equivalents in the given string
   * @param str the string
   * @return unprintable characters by their escaped (or unicode escaped)
   * equivalents
   */
  protected static final String addEscapes(String str) {
    StringBuffer retval = new StringBuffer();
    char ch;
    for (int i = 0; i < str.length(); i++) {
      switch (str.charAt(i))
      {
        case 0 :
          continue;
        case '\b':
          retval.append("\\b");
          continue;
        case '\t':
          retval.append("\\t");
          continue;
        case '\n':
          retval.append("\\n");
          continue;
        case '\f':
          retval.append("\\f");
          continue;
        case '\r':
          retval.append("\\r");
          continue;
        case '\"':
          retval.append("\\\"");
          continue;
        case '\'':
          retval.append("\\\'");
          continue;
        case '\\':
          retval.append("\\\\");
          continue;
        default:
          if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
            String s = "0000" + Integer.toString(ch, 16);
            retval.append("\\u" + s.substring(s.length() - 4, s.length()));
          } else {
            retval.append(ch);
          }
          continue;
      }
    }
    return retval.toString();
  }

  /**
 * Generates a detailed error message for a lexical error encountered during parsing.
 * <p>
 * This method constructs an error message that provides specific details about the lexical error,
 * including the location in the input where the error occurred (line and column numbers),
 * the character that caused the error, and the text that was parsed immediately before the error.
 * If the end-of-file (EOF) was encountered, this is indicated in the message.
 * </p>
 *
 * @param EOFSeen A boolean indicating whether the end-of-file (EOF) was encountered during the error.
 * @param lexState The lexical state of the parser when the error occurred.
 * @param errorLine The line number where the error occurred.
 * @param errorColumn The column number where the error occurred.
 * @param errorAfter The text that was parsed immediately before the error occurred.
 * @param curChar The character that caused the error.
 * @return A string describing the lexical error, including its location, the problematic character, and the text preceding the error.
 */
  protected static String LexicalError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar) {
    return("Lexical error at line " +
          errorLine + ", column " +
          errorColumn + ".  Encountered: " +
          (EOFSeen ? "<EOF> " : ("\"" + addEscapes(String.valueOf(curChar)) + "\"") + " (" + (int)curChar + "), ") +
          "after : \"" + addEscapes(errorAfter) + "\"");
  }

  /**
   * You can also modify the body of this method to customize your error messages.
   * For example, cases like LOOP_DETECTED and INVALID_LEXICAL_STATE are not
   * of end-users concern, so you can return something like :
   *
   *     "Internal Error : Please file a bug report .... "
   *
   * from this method for such cases in the release version of your parser.
   */
  @Override
public String getMessage() {
    return super.getMessage();
  }

  /**
   * Constructors of various flavors follow.
   */

  /** No arg constructor. */
  public TokenMgrError() {
  }

/**
 * Constructs a {@code TokenMgrError} with a specified error message and reason code.
 *
 * @param message A description of the error.
 * @param reason An integer code representing the type of error.
 */
public TokenMgrError(String message, int reason) {
  super(message);
  errorCode = reason;
}

/**
* Constructs a {@code TokenMgrError} with detailed information about the lexical error.
*
* @param EOFSeen A boolean indicating if the end-of-file was seen during the error.
* @param lexState An integer representing the lexical state when the error occurred.
* @param errorLine An integer indicating the line number where the error occurred.
* @param errorColumn An integer indicating the column number where the error occurred.
* @param errorAfter A string representing the text after which the error occurred.
* @param curChar The character that was being processed when the error occurred.
* @param reason An integer code representing the type of error.
*/
public TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar, int reason) {
  this(LexicalError(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason);
}

}
/* JavaCC - OriginalChecksum=8dd96503e6780572e360ecc1295ca890 (do not edit this line) */
