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
/* Generated By:JJTree: Do not edit this line. ASTProgram.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.tweetyproject.lp.asp.parser;

/**
 * The {@code ASTProgram} class represents a node in the abstract syntax tree (AST)
 * for a program in the context of Answer Set Programming (ASP). This class
 * extends {@code SimpleNode} and is used by the ASP parser to encapsulate an entire
 * ASP program, which may consist of rules, facts, and other constructs.
 */
public class ASTProgram extends SimpleNode {

    /**
     * Constructs a new {@code ASTProgram} node with the specified identifier.
     *
     * @param id The node identifier.
     */
    public ASTProgram(int id) {
        super(id);
    }

    /**
     * Constructs a new {@code ASTProgram} node with the specified parser and identifier.
     *
     * @param p  The {@code ASPParser} that is constructing this node.
     * @param id The node identifier.
     */
    public ASTProgram(ASPParser p, int id) {
        super(p, id);
    }

    /**
     * Accepts a visitor object, which implements the {@code ASPParserVisitor} interface,
     * and allows it to process this node in the AST.
     *
     * @param visitor The visitor object that processes this node.
     * @param data    Additional data that might be needed for the visitor's processing.
     * @return The result of the visitor's processing, typically dependent on the visitor's implementation.
     */
    public Object jjtAccept(ASPParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

/*
 * JavaCC - OriginalChecksum=8d7508eb77c6926d942967f6a2ed1fc9 (do not edit this
 * line)
 */
