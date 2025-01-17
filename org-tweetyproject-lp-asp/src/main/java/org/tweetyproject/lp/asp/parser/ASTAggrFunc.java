/* Generated By:JJTree&JavaCC: Do not edit this line. ASPParserTokenManager.java */
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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
/* Generated By:JJTree: Do not edit this line. ASTAggrFunc.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.tweetyproject.lp.asp.parser;
/**
 * class ASTAggrFunc
 * @author Anna Gessler
 *
 */
public class ASTAggrFunc extends SimpleNode {
	/**func*/
	public String func;
/**
 *constructor
 * @param id ID
 */
	public ASTAggrFunc(int id) {
		super(id);
	}
/**
 *constructor
 * @param p parser
 * @param id ID
 */
	public ASTAggrFunc(ASPParser p, int id) {
		super(p, id);
	}
/**
 * setter func
 * @param f func
 */
	public void func(String f) {
		this.func = f;
	}

	/** Accept the visitor. **/
	public Object jjtAccept(ASPParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=7dc32b0f749c5be1d6a5c39d60bc2f07 (do not edit this
 * line)
 */
