/* Generated By:JJTree: Do not edit this line. ASTDlvID.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2021 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.parser;

/**
 * The {@code ASTDlvID} class represents a node in the abstract syntax tree (AST)
 * for identifiers in the DLV language, which is a logic programming language used in
 * Answer Set Programming (ASP). This class extends {@code SimpleNode} and is used
 * within the context of the ASP parser.
 */
public class ASTDlvID extends SimpleNode {

    /**
     * The name or identifier represented by this AST node.
     */
    protected String name;

    /**
     * Constructs a new {@code ASTDlvID} node with the specified identifier.
     *
     * @param id The node identifier.
     */
    public ASTDlvID(int id) {
        super(id);
    }

    /**
     * Constructs a new {@code ASTDlvID} node with the specified parser and identifier.
     *
     * @param p  The {@code ASPParser} that is constructing this node.
     * @param id The node identifier.
     */
    public ASTDlvID(ASPParser p, int id) {
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

    /**
     * Sets the name or identifier represented by this AST node.
     *
     * @param image The string representing the name or identifier.
     */
    public void name(String image) {
        this.name = image;
    }
}

/*
 * JavaCC - OriginalChecksum=fb0631c0b19f9beb11f7605c1dae300f (do not edit this
 * line)
 */
