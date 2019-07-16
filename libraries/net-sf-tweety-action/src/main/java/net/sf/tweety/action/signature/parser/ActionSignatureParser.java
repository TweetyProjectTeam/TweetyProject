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
package net.sf.tweety.action.signature.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.tweety.action.signature.ActionSignature;
import net.sf.tweety.action.signature.FolActionName;
import net.sf.tweety.action.signature.FolFluentName;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Sort;

/**
 * This class implements a parser for signatures of action descriptions. The BNF for such signatures is given by: (starting symbol is SIG) <br>
 * <br>
 * SIG ::== (SORTSDEC | DECLAR)* <br>
 * SORTSDEC ::== ( SORTNAME "=" "{" (CONSTANTNAME ("," CONSTANTNAME)*)? "}" "\n" )* <br>
 * DECLAR ::== (ACTIONDEC | FLUENTDEC)* <br>
 * ACTIONDEC ::== "action" "(" ACTIONNAME ("(" SORTNAME ("," SORTNAME)* ")")? ")" "\n" <br>
 * FLUENTDEC ::== "fluent" "(" FLUENTNAME ("(" SORTNAME ("," SORTNAME)* ")")? ")" "\n" <br>
 * <br>
 * where SORTNAME, ACTIONNAME, FLUENTNAME, CONSTANTNAME are sequences of <br>
 * symbols from {a,...,z,A,...,Z,0,...,9} with a letter at the beginning.
 * 
 * @author Sebastian Homann
 */
public class ActionSignatureParser
{

  /**
   * Resulting signature.
   */
  private ActionSignature signature = new ActionSignature();

  /**
   * Parses the file of the given filename into a signature.
   * 
   * @param filename the name of a file
   * @return a formula
   * @throws FileNotFoundException if the file is not found
   * @throws IOException if IO fails
   * @throws ParserException some parsing exceptions may be added here.
   */
  public ActionSignature parseSignatureFromFile( String filename )
    throws FileNotFoundException, IOException, ParserException
  {
    return this.parseSignature( new InputStreamReader(
      new java.io.FileInputStream( filename ) ) );
  }

  /**
   * Parses the given text into a signature of the given type.
   * 
   * @param text a string
   * @return a formula
   * @throws IOException if IO fails
   * @throws ParserException some parsing exceptions may be added here.
   */
  public ActionSignature parseSignature( String text )
    throws IOException, ParserException
  {
    return this.parseSignature( new StringReader( text ) );
  }

  /**
   * Parses the given reader into a signature of the given type.
   * 
   * @param reader a reader
   * @return a formula
   * @throws IOException if IO fails
   * @throws ParserException some parsing exceptions may be added here.
   */
  public ActionSignature parseSignature( Reader reader )
    throws IOException, ParserException
  {
    String s = "";
    // read from the reader and separate formulas by "\n"
    try {
      int c;
      do {
        c = reader.read();
        if ( c == 10 || c == -1 ) {
          if ( !s.equals( "" ) && !s.trim().startsWith( "%" ) ) {
            String strim = s.trim();
            if ( strim.startsWith( "fluent" ) || strim.startsWith( "action" ) )
              this.parseTypeDeclaration( strim );
            else
              this.parseSortDeclaration( strim );
          }
          s = "";
        }
        else {
          s += (char) c;
        }
      } while ( c != -1 );
    }
    catch ( Exception e ) {
      throw new ParserException( e );
    }
    return signature;
  }

  /**
   * Parses a sort declaration of the form "SORTNAME "=" "{" (CONSTANTNAME ("," CONSTANTNAME)*)? "}""
   * and adds it to the signature.
   * 
   * @param s a string
   */
  protected void parseSortDeclaration( String s )
    throws ParserException
  {
    if ( !s.contains( "=" ) )
      throw new ParserException( "Missing '=' in sort declaration '" + s + "'." );
    String sortname = s.substring( 0, s.indexOf( "=" ) ).trim();
    if ( !sortname.matches( "[a-z,A-Z]([a-z,A-Z,0-9])*" ) )
      throw new ParserException( "Illegal characters in constant definition '" +
        sortname + "'; declartion must conform to [a-z,A-Z]([a-z,A-Z,0-9])*" );
    if ( this.signature.containsSort( sortname ) )
      throw new ParserException( "Multiple declarations of sort '" + sortname +
        "'." );
    Sort theSort = new Sort( sortname );
    this.signature.add( theSort );
    if ( !s.contains( "{" ) )
      throw new ParserException( "Missing '{' in sort declaration '" + s + "'," );
    if ( !s.contains( "}" ) )
      throw new ParserException( "Missing '}' in sort declaration '" + s + "'," );
    String constants = s.substring( s.indexOf( "{" ) + 1, s.lastIndexOf( "}" ) );
    if ( constants.contains( "{" ) )
      throw new ParserException( "Multiple '{'s in sort declaration '" + s +
        "'." );
    if ( constants.contains( "}" ) )
      throw new ParserException( "Multiple '}'s in sort declaration '" + s +
        "'." );
    String[] tokens = constants.split( "," );
    for ( String token : tokens ) {
      String c = token.trim();
      if ( signature.containsConstant( c ) )
        throw new ParserException( "Constant '" + c +
          "' has already been defined to be of sort '" +
          this.signature.getConstant( c ).getSort() + "'." );
      if ( c.matches( "[a-z,A-Z]([a-z,A-Z,0-9])*" ) )
        this.signature.add( new Constant( c, theSort ) );
      else
        throw new ParserException(
          "Illegal characters in constant definition '" + c +
            "'; declartion must conform to [a-z,A-Z]([a-z,A-Z,0-9])*" );
    }
  }

  /**
   * Parses an action declaration of the form "action" "(" ACTIONNAME ("(" SORTNAME ("," SORTNAME)* ")")? ")" 
   * or a fluent declaration of the form "fluent" "(" FLUENTNAME ("(" SORTNAME ("," SORTNAME)* ")")? ")" 
   * and adds them to the signature.
   * 
   * @param s a string
   */
  protected void parseTypeDeclaration( String s )
    throws ParserException
  {
    boolean action;
    if ( s.startsWith( "action" ) ) {
      action = true;
    }
    else if ( s.startsWith( "fluent" ) ) {
      action = false;
    }
    else
      throw new ParserException(
        "Type declaration has to start either with 'action' or 'fluent'." );
    String dec = s.substring( 6 ).trim();
    if ( !dec.contains( "(" ) ) {
      if ( dec.contains( ")" ) )
        throw new ParserException( "Dangling ')' in type declaration." );
      String name = dec;
      if ( !name.matches( "[a-z,A-Z]([a-z,A-Z,0-9])*" ) )
        throw new ParserException(
          "Illegal characters in constant definition '" + name +
            "'; declartion must conform to [a-z,A-Z]([a-z,A-Z,0-9])*" );
      if ( action )
        this.signature.add( new FolActionName( name ) );
      else
        this.signature.add( new FolFluentName( name ) );
    }
    else {
      if ( !dec.contains( ")" ) )
        throw new ParserException( "Missing ')' in type declaration." );
      String name = dec.substring( 0, dec.indexOf( "(" ) ).trim();
      if ( !name.matches( "[a-z,A-Z]([a-z,A-Z,0-9])*" ) )
        throw new ParserException(
          "Illegal characters in constant definition '" + name +
            "'; declartion must conform to [a-z,A-Z]([a-z,A-Z,0-9])*" );
      String sorts =
        dec.substring( dec.indexOf( "(" ) + 1, dec.lastIndexOf( ")" ) );
      if ( sorts.trim().equals( "" ) ) {
        if ( action )
          this.signature.add( new FolActionName( name ) );
        else
          this.signature.add( new FolFluentName( name ) );
        return;
      }
      String[] tokens = sorts.split( "," );
      List< Sort > theSorts = new ArrayList< Sort >();
      for ( String token : tokens ) {
        String sort = token.trim();
        if ( !this.signature.containsSort( sort ) )
          throw new ParserException( "Sort '" + sort +
            "' has not been declared before." );
        theSorts.add( this.signature.getSort( sort ) );
      }
      if ( action )
        this.signature.add( new FolActionName( name, theSorts ) );
      else
        this.signature.add( new FolFluentName( name, theSorts ) );
    }
  }
}
