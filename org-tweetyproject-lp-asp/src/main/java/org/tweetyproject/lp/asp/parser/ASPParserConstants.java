/* Generated By:JJTree&JavaCC: Do not edit this line. ASPParserConstants.java */
package org.tweetyproject.lp.asp.parser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface ASPParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int COMMENT = 5;
  /** RegularExpression Id. */
  int MULTI_LINE_COMMENT = 6;
  /** RegularExpression Id. */
  int NAF = 7;
  /** RegularExpression Id. */
  int OR = 8;
  /** RegularExpression Id. */
  int ID = 9;
  /** RegularExpression Id. */
  int VARIABLE = 10;
  /** RegularExpression Id. */
  int STRING = 11;
  /** RegularExpression Id. */
  int NUMBER = 12;
  /** RegularExpression Id. */
  int ANONYMOUS_VARIABLE = 13;
  /** RegularExpression Id. */
  int DOT = 14;
  /** RegularExpression Id. */
  int COMMA = 15;
  /** RegularExpression Id. */
  int QUERY_MARK = 16;
  /** RegularExpression Id. */
  int COLON = 17;
  /** RegularExpression Id. */
  int SEMICOLON = 18;
  /** RegularExpression Id. */
  int CONS = 19;
  /** RegularExpression Id. */
  int WCONS = 20;
  /** RegularExpression Id. */
  int PLUS = 21;
  /** RegularExpression Id. */
  int MINUS = 22;
  /** RegularExpression Id. */
  int TIMES = 23;
  /** RegularExpression Id. */
  int DIV = 24;
  /** RegularExpression Id. */
  int MODULO = 25;
  /** RegularExpression Id. */
  int AT = 26;
  /** RegularExpression Id. */
  int PAREN_OPEN = 27;
  /** RegularExpression Id. */
  int PAREN_CLOSE = 28;
  /** RegularExpression Id. */
  int SQUARE_OPEN = 29;
  /** RegularExpression Id. */
  int SQUARE_CLOSE = 30;
  /** RegularExpression Id. */
  int CURLY_OPEN = 31;
  /** RegularExpression Id. */
  int CURLY_CLOSE = 32;
  /** RegularExpression Id. */
  int EQUAL = 33;
  /** RegularExpression Id. */
  int UNEQUAL = 34;
  /** RegularExpression Id. */
  int LESS = 35;
  /** RegularExpression Id. */
  int GREATER = 36;
  /** RegularExpression Id. */
  int LESS_OR_EQ = 37;
  /** RegularExpression Id. */
  int GREATER_OR_EQ = 38;
  /** RegularExpression Id. */
  int AGGREGATE_COUNT = 39;
  /** RegularExpression Id. */
  int AGGREGATE_MAX = 40;
  /** RegularExpression Id. */
  int AGGREGATE_MIN = 41;
  /** RegularExpression Id. */
  int MINIMIZE = 42;
  /** RegularExpression Id. */
  int MAXIMIZE = 43;
  /** RegularExpression Id. */
  int AGGREGATE_SUM = 44;
  /** RegularExpression Id. */
  int AGGREGATE_SUM_PLUS = 45;
  /** RegularExpression Id. */
  int AGGREGATE_TIMES = 46;
  /** RegularExpression Id. */
  int CLINGO_SHOW = 47;
  /** RegularExpression Id. */
  int META_MAXINT = 48;
  /** RegularExpression Id. */
  int META_CONST = 49;
  /** RegularExpression Id. */
  int DLV_ARITHMETIC_ID = 50;
  /** RegularExpression Id. */
  int DLV_ID = 51;
  /** RegularExpression Id. */
  int META_OTHER = 52;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<COMMENT>",
    "<MULTI_LINE_COMMENT>",
    "\"not\"",
    "<OR>",
    "<ID>",
    "<VARIABLE>",
    "<STRING>",
    "<NUMBER>",
    "\"_\"",
    "\".\"",
    "\",\"",
    "\"?\"",
    "\":\"",
    "\";\"",
    "\":-\"",
    "\":~\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"\\\\\"",
    "\"@\"",
    "\"(\"",
    "\")\"",
    "\"[\"",
    "\"]\"",
    "\"{\"",
    "\"}\"",
    "<EQUAL>",
    "<UNEQUAL>",
    "\"<\"",
    "\">\"",
    "\"<=\"",
    "\">=\"",
    "\"#count\"",
    "\"#max\"",
    "\"#min\"",
    "<MINIMIZE>",
    "<MAXIMIZE>",
    "\"#sum\"",
    "\"#sum+\"",
    "\"#times\"",
    "<CLINGO_SHOW>",
    "<META_MAXINT>",
    "<META_CONST>",
    "<DLV_ARITHMETIC_ID>",
    "<DLV_ID>",
    "<META_OTHER>",
  };

}
