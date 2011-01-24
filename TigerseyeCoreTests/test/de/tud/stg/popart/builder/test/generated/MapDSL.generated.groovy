package de.tud.stg.popart.builder.test.generated;

import de.tud.stg.popart.builder.utils.DSLInvoker;
import de.tud.stg.popart.builder.test.dsls.MapDSL;
import de.tud.stg.popart.builder.test.dsls.MapDSL.Entry;
	 
new DSLInvoker(MapDSL.class).eval() {

	Map map = buildMap(
Integer.class,
String.class,
[
buildEntry(
1,
"hans"),
buildEntry(
2,
"peter")
] as Entry[])
	
	print map
}