package de.tud.stg.popart.builder.test.generated;

import de.tud.stg.popart.builder.utils.DSLInvoker;
import de.tud.stg.popart.builder.test.dsls.ForEachSyntaxDSL;
	 
new DSLInvoker(ForEachSyntaxDSL.class).eval() {
	String[] abc = ["a", "b", "ac"];
	
	forEach(
String.class,
s,
abc,
{
if (s.startsWith("a")) {
			System.out.println(s);
		}}
)
}