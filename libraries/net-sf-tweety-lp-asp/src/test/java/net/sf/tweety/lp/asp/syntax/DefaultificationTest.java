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
package net.sf.tweety.lp.asp.syntax;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DefaultificationTest extends TestCase {
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DefaultificationTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DefaultificationTest.class );
    }
    
    public void testSimpleDefaultifcation() {
    	Program p = new Program();
    	ASPRule onlyRule = new ASPRule();
    	onlyRule.setConclusion(new ASPAtom("x"));
    	onlyRule.addPremise(new ASPAtom("y"));
    	p.add(onlyRule);
    	
    	Program dp = Program.defaultification(p);
    	assertTrue(dp.size() == p.size());
    	ASPRule dr = dp.iterator().next();
    	
    	assertEquals(dr.getConclusion(), onlyRule.getConclusion());
    	assertTrue(dr.getPremise().contains(onlyRule.getPremise().iterator().next()));
    	DefaultNegation defNot = new DefaultNegation(new StrictNegation(dr.getConclusion().iterator().next().getAtom()));
    	assertTrue(dr.getPremise().contains(defNot));
    	
    	p = new Program();
    	onlyRule = new ASPRule();
    	onlyRule.setConclusion(new StrictNegation(new ASPAtom("x")));
    	
    	p.add(onlyRule);
    	dp = Program.defaultification(p);
    	assertEquals(p.size(), dp.size());
    	dr = dp.iterator().next();
    	
    	assertEquals(dr.getConclusion(), onlyRule.getConclusion());
    	defNot = new DefaultNegation(dr.getConclusion().iterator().next().getAtom());
    	assertEquals(true, dr.getPremise().contains(defNot));
    }
    
    public void testDefaultificationOfAlreadyDefaulticated() {
    	Program p = new Program();
    	ASPRule r = new ASPRule();
    	ASPAtom a = new ASPAtom("x");
    	r.setConclusion(a);
    	DefaultNegation defLit = new DefaultNegation(new StrictNegation(a));
    	r.addPremise(defLit);
    	p.add(r);
    	
    	// We want the program dp look like p cause p was already
    	// defaultisated
    	Program dp = Program.defaultification(p);
    	ASPRule rd = dp.iterator().next();
    	assertEquals(rd, r);
    	assertEquals(dp, p);
    }
}
