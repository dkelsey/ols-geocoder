/**
 * Copyright © 2008-2019, Province of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.bc.gov.ols.geocoder.parser;

import ca.bc.gov.ols.geocoder.parser.AddressParser;
import ca.bc.gov.ols.geocoder.parser.generator.AddressParserGenerator;
import ca.bc.gov.ols.geocoder.parser.generator.Rule;
import ca.bc.gov.ols.geocoder.parser.generator.RuleChoice;
import ca.bc.gov.ols.geocoder.parser.generator.RuleOperator;
import ca.bc.gov.ols.geocoder.parser.generator.RuleSequence;
import ca.bc.gov.ols.geocoder.parser.generator.RuleTerm;
import junit.framework.TestCase;
import junit.textui.TestRunner;

public class SimpleAddressParseTest extends TestCase
{
	
	public static void main(String args[]) {
		TestRunner.run(SimpleAddressParseTest.class);
	}
	
	public SimpleAddressParseTest(String name) {
		super(name);
	}
	
	public void testAddress()
	{
		AddressParser parser = createParser();
		
		run(parser, "123 main");
		run(parser, "123 n main");
		run(parser, "123 n san pedro");
		run(parser, "123 n 13th");
	}
	
	void run(AddressParser parser, String sentence)
	{
		run(parser, sentence, true);
	}
	
	void run(AddressParser parser, String sentence, boolean expected)
	{
		
		parser.setTrace(true);
		BasicParseDerivationHandler handler = new BasicParseDerivationHandler();
		parser.parse(sentence, false, handler);
		boolean isValid = handler.getDerivations().size() > 0;
		
		assertTrue(isValid == expected);
	}
	
	/**
	 * Unit numbers
	 * 
	 * Examples: #1234, #A1234A, #1, #1A A#308, Apt#17
	 */
	public static final String RE_UNIT = ".*#.*";
	/**
	 * Examples: 1234, 1234A, 1, 1A
	 */
	public static final String RE_NUMBER = "\\d+[A-Z]?";
	
	/**
	 * Examples: 12TH, 23RD
	 */
	public static final String RE_NUMBERED_STREET = "\\d+((ST)|(TH)|(RD))";
	
	public static final String RE_NUMBERED_HWY = "(I|(SR))\\d+";
	
	public static final String RE_DIRECTIONAL = "N|NO|SNW|NE|S|SO|SE|SW|E|EA|W|WE";
	
	AddressParser createParser()
	{
		AddressParserGenerator parserGen = new AddressParserGenerator();
		
		Rule ruleAddr = new RuleSequence("addr", true, new RuleTerm[] {
				new RuleTerm("number"),
				new RuleTerm("directional", RuleOperator.OPTION),
				new RuleTerm("name"),
		});
		parserGen.addRule(ruleAddr);
		
		parserGen.addRule(new RuleTerm("number", "NUMBER"));
		
		parserGen.addRule(new RuleTerm("directional", "DIRECTIONAL"));
		
		parserGen.addRule(new RuleChoice("name", true, new RuleTerm[] {
				new RuleTerm("NUMBERED_ST"),
				new RuleTerm("WORD", RuleOperator.STAR)
		}));
		
		return parserGen.getParser();
	}
	
}
