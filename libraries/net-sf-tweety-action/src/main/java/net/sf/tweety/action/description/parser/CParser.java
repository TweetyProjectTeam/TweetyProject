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
package net.sf.tweety.action.description.parser;

import java.io.IOException;
import java.io.Reader;

import net.sf.tweety.action.description.syntax.CActionDescription;
import net.sf.tweety.action.signature.ActionSignature;
import net.sf.tweety.action.signature.parser.ActionSignatureParser;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;

/**
 * This class implements a parser for an Action Description in C. 
 * The BNF is given by: (starting symbol is DESC) <br>
 * <br> DESC ::== ":- signature" "\n" SIGNATURE "\n" ":- laws" "\n" LAWS
 * <br>
 * where SIGNATURE is parsed by CSignatureParser and LAWS is parsed by CLawParser.
 * @author Sebastian Homann
 */
public class CParser
  extends Parser<CActionDescription,Formula> {
  protected ActionSignature signature;
  
  /*
   * (non-Javadoc)
   * @see net.sf.tweety.Parser#parseBeliefBase(java.io.Reader)
   */
  @Override
  public CActionDescription parseBeliefBase( Reader reader )
    throws IOException, ParserException {
    // State 0 : initialize
    // State 1 : read signature
    // State 2 : read lawbase
    int state = 0;
    String s = "";
    String sig = "";
    String laws = "";
    // read from the reader and separate formulas by "\n"
    try {
      int c;
      do {
        c = reader.read();
        if ( c == 10 || c == -1 ) {
          if ( !s.trim().equals( "" ) ) {
            if(s.trim().contains( ":- signature" )) {
              state = 1;
            } else if(s.trim().contains( ":- laws" )) {
              state = 2;
            } else {
              if(state == 1) sig += s+"\n";
              else laws += s+"\n";
            }
          }
          s = "";
        } else {
          s += (char) c;
        }
      } while ( c != -1 );
      
      signature = new ActionSignatureParser().parseSignature( sig );
      return new CLawParser( signature ).parseBeliefBase( laws );
    } catch ( Exception e ) {
      throw new ParserException( e );
    }
  }

  /*
   * (non-Javadoc)
   * @see net.sf.tweety.Parser#parseFormula(java.io.Reader)
   */
  @Override
  public Formula parseFormula( Reader reader )
    throws IOException, ParserException {
    String s = "";
    int c;
    do{
     c = reader.read();
     s += (char) c;
    } while(c != -1);
    return parseFormula(s);
  }
  
  /*
   * (non-Javadoc)
   * @see net.sf.tweety.Parser#parseFormula(java.lang.String)
   */
  @Override
  public Formula parseFormula( String formula) throws ParserException, IOException {
    return new CLawParser( signature ).parseFormula( formula );
  }
  
  public void setSignature( ActionSignature signature) {
    this.signature = signature;
  }
  
  public ActionSignature getSignature() {
    return this.signature;
  }
}
