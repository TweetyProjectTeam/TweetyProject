/* Generated By:JavaCC: Do not edit this line. ASPParserVisitor.java Version 5.0 */
package org.tweetyproject.lp.asp.parser;
/**
 * ASPParserVisitor
 * @author Anna Gessler
 *
 */
public interface ASPParserVisitor
{
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(SimpleNode node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTAnswerSet node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTProgram node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTRuleList node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTQuery node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTRule node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTHead node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTBodyList node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTBody node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTHeadElementsList node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTChoice node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTChoiceElementList node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTChoiceElement node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTAggregate node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTAggrElementList node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTAggrElement node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTAggrFunc node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTClingoMeta node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTOpt node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTOptElementList node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTOptElement node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTOptFunc node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTWeight node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTNAFLiteralList node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTNAFLiteral node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTLiteral node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTBuiltInAtom node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTBinop node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTTermList node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTTerm node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTAriTerm node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTArithop node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTNumber node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTVar node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTID node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTDlvID node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTClingoID node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTDlvArithmeticID node, Object data);
	/**
	 *Constructor
	 * @param node node
	 * @param data data
	 * @return object
	 */
  public Object visit(ASTString node, Object data);
}
/* JavaCC - OriginalChecksum=e515ecb09463a4c035c8b45eaed6aa20 (do not edit this line) */
